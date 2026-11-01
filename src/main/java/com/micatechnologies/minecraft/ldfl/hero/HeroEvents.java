package com.micatechnologies.minecraft.ldfl.hero;

import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySlot;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroResolver;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroSpec;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroState;
import com.micatechnologies.minecraft.ldfl.hero.core.Role;
import com.micatechnologies.minecraft.ldfl.network.LdflNetwork;
import com.micatechnologies.minecraft.ldfl.network.PacketHeroSync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * The server-side hero loop: resolve each player's hero from their hands every tick, keep passives
 * in step, tick cooldowns, charge ultimates from damage, run abilities, and sync the client mirror.
 *
 * <p>Registered on the Forge event bus by {@code Ldfl.preInit}.</p>
 */
public final class HeroEvents {

    /** The Damage role's "Wounded" effect. Registered in {@link #onRegisterPotions}. */
    public static final PotionWounded WOUNDED = new PotionWounded();

    /** Ticks between unconditional resyncs while a hero is active (cooldowns drift otherwise). */
    private static final int RESYNC_INTERVAL = 40;

    /** Kestrel's "Marked" effect: the target takes extra damage. */
    public static final PotionMarked MARKED = new PotionMarked();

    @SubscribeEvent
    public void onRegisterPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(WOUNDED);
        event.getRegistry().register(MARKED);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().isPotionActive(MARKED)) {
            event.setAmount((float) (event.getAmount() * (1.0D + LdflConfig.kestrel.markedDamageBonus)));
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            HeroData data = HeroCapability.get((EntityPlayer) event.getEntityLiving());
            if (data != null && data.noFallTicks > 0) {
                event.setDistance(0.0F);
            }
        }
    }

    // --- Per-tick loop -----------------------------------------------------------------------

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        HeroData data = HeroCapability.get(player);
        if (data == null) {
            return;
        }
        HeroState state = data.state();

        HeroDefinition def = player.isEntityAlive() && !player.isSpectator()
            ? HeroRegistry.get(HeroResolver.resolve(heroOf(player.getHeldItemMainhand()),
                                                    heroOf(player.getHeldItemOffhand())))
            : null;
        boolean changed = state.setHero(def == null ? null : def.spec());
        if (changed || !data.passivesReconciled) {
            HeroPassives.reconcile(player, def == null ? null : def.spec().role());
            data.passivesReconciled = true;
            data.chargeTicks = 0;
            data.dirty = true;
        }

        state.tick();
        // Fall protection lasts the whole flight and runs out only once the player is back on the
        // ground, so a long glide down is still covered.
        if (data.noFallTicks > 0 && player.onGround) {
            data.noFallTicks--;
        }

        if (def != null && def.spec().role() == Role.SUPPORT) {
            tickSupportRegen(player, data);
        }
        // Every other hero tears down what it left behind; the active one ticks. A hero with a HUD
        // resource sets it in its tick, so it starts each tick as "none".
        data.resource = -1.0F;
        for (HeroDefinition other : HeroRegistry.all()) {
            if (other != def) {
                other.cleanup(player, data);
            }
        }
        if (def != null) {
            def.tick(player, data);
        }

        int ultPercent = (int) (state.ultCharge() * 100.0F);
        int resourcePercent = (int) Math.floor(data.resource * 100.0F);
        if (ultPercent != data.lastSentUltPercent || resourcePercent != data.lastSentResourcePercent) {
            data.dirty = true;
        }
        if (data.dirty || (def != null && player.ticksExisted % RESYNC_INTERVAL == 0)) {
            sync(player, data);
        }
    }

    private static void tickSupportRegen(EntityPlayerMP player, HeroData data) {
        int delay = LdflConfig.passives.supportRegenDelaySeconds * 20;
        if (player.ticksExisted - data.lastHurtTick >= delay
            && player.getHealth() < player.getMaxHealth()) {
            player.heal((float) (LdflConfig.passives.supportRegenPerSecond / 20.0D));
        }
    }

    /** The hero id of a signature item, or {@code null} for anything else. */
    public static String heroOf(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ISignatureItem)) {
            return null;
        }
        return ((ISignatureItem) stack.getItem()).heroId(stack);
    }

    // --- Abilities ---------------------------------------------------------------------------

    /** Called on the server thread for a {@code PacketUseAbility}. Validates, runs, spends. */
    public static void tryUse(EntityPlayerMP player, AbilitySlot slot) {
        if (!player.isEntityAlive() || player.isSpectator()) {
            return;
        }
        HeroData data = HeroCapability.get(player);
        if (data == null) {
            return;
        }
        HeroState state = data.state();
        if (state.check(slot) != HeroState.Activation.OK) {
            // The client mirror let the key through, so it is out of date: correct it.
            data.dirty = true;
            return;
        }
        HeroSpec spec = state.hero();
        HeroDefinition def = HeroRegistry.get(spec.id());
        if (def != null && def.ability(slot).activate(player, data)) {
            state.commit(slot);
        }
        data.dirty = true;
    }

    private static void sync(EntityPlayerMP player, HeroData data) {
        HeroState state = data.state();
        LdflNetwork.CHANNEL.sendTo(new PacketHeroSync(
            state.hero() == null ? "" : state.hero().id(),
            state.cooldownSnapshot(), state.ultCharge(), data.resource), player);
        data.dirty = false;
        data.lastSentUltPercent = (int) (state.ultCharge() * 100.0F);
        data.lastSentResourcePercent = (int) Math.floor(data.resource * 100.0F);
    }

    /**
     * Credit healing done by a support hero to their ultimate meter. Only healing that actually
     * landed counts, so topping up a full-health ally earns nothing.
     */
    public static void creditHealing(EntityPlayer healer, float amountHealed) {
        HeroData data = HeroCapability.get(healer);
        if (data != null) {
            data.state().addUltPoints(amountHealed);
        }
    }

    // --- Damage: ult charge, Wounded, combat timer --------------------------------------------

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target.world.isRemote) {
            return;
        }
        if (target instanceof EntityPlayer) {
            HeroData victim = HeroCapability.get((EntityPlayer) target);
            if (victim != null) {
                victim.lastHurtTick = target.ticksExisted;
            }
        }

        Entity src = event.getSource().getTrueSource();
        if (!(src instanceof EntityPlayerMP) || !Targeting.isEnemy((EntityPlayer) src, target)) {
            return;
        }
        HeroData attacker = HeroCapability.get((EntityPlayer) src);
        if (attacker == null || attacker.state().hero() == null) {
            return;
        }
        // Overkill does not count, so one-shotting a zombie with 100 damage is not a full ultimate.
        attacker.state().addUltPoints(Math.min(event.getAmount(), target.getHealth()));
        int woundTicks = LdflConfig.passives.damageWoundSeconds * 20;
        if (attacker.state().hero().role() == Role.DAMAGE && woundTicks > 0) {
            target.addPotionEffect(new PotionEffect(WOUNDED, woundTicks, 0, false, true));
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        if (event.getEntityLiving().isPotionActive(WOUNDED)) {
            event.setAmount((float) (event.getAmount() * LdflConfig.passives.woundedHealingFactor));
        }
    }
}
