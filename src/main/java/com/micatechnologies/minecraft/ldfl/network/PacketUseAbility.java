package com.micatechnologies.minecraft.ldfl.network;

import com.micatechnologies.minecraft.ldfl.hero.HeroEvents;
import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySlot;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client to server: "the player pressed this ability key". Carries nothing but the slot; the
 * server decides whether anything happens (decision D8).
 */
public class PacketUseAbility implements IMessage {

    private int slot;

    public PacketUseAbility() {
    }

    public PacketUseAbility(AbilitySlot slot) {
        this.slot = slot.ordinal();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(slot);
    }

    public static class Handler implements IMessageHandler<PacketUseAbility, IMessage> {
        @Override
        public IMessage onMessage(PacketUseAbility message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            final AbilitySlot slot = AbilitySlot.byOrdinal(message.slot);
            if (slot != null) {
                player.getServerWorld().addScheduledTask(() -> HeroEvents.tryUse(player, slot));
            }
            return null;
        }
    }
}
