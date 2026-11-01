package com.micatechnologies.minecraft.ldfl.hero;

import com.micatechnologies.minecraft.ldfl.hero.core.HeroState;

/**
 * Per-player hero data, attached to every player as a capability on the server.
 *
 * <p>Deliberately not saved: who you are comes from the items in your hands, so it is rebuilt on
 * the first tick after login. Cooldowns and the ultimate meter reset on relog or death.</p>
 */
public final class HeroData {

    private final HeroState state = new HeroState();

    /** {@code player.ticksExisted} when this player last took damage. */
    public int lastHurtTick = Integer.MIN_VALUE / 2;

    /** Ticks left in an active Shoulder Charge (Bulwark); 0 when not charging. */
    public int chargeTicks;

    /** Set when the client mirror is out of date and a sync packet should go out. */
    public boolean dirty = true;

    /** False until passives have been reconciled once after login or respawn. */
    public boolean passivesReconciled;

    /** Ult charge quantised to whole percent, as last sent to the client. */
    public int lastSentUltPercent = -1;

    /** Bulwark: barrier health, kept while the barrier is lowered. Starts full. */
    public float barrierHealth = Float.NaN;

    /** Bulwark: ticks before a broken barrier can be raised again. */
    public int barrierBrokenTicks;

    /** Bulwark: {@code player.ticksExisted} when the barrier last absorbed a hit. */
    public int barrierLastHitTick = Integer.MIN_VALUE / 2;

    /** Bulwark: the raised barrier panel, or {@code null} while lowered. Server side only. */
    public com.micatechnologies.minecraft.ldfl.entity.EntityBarrier barrier;

    /** Solace: the active heal beam, or {@code null}. Server side only. */
    public com.micatechnologies.minecraft.ldfl.entity.EntityHealBeam beam;

    /** Forgewright: the deployed sentry, or {@code null}. Server side only. */
    public com.micatechnologies.minecraft.ldfl.entity.EntitySentry sentry;

    /** Forgewright: ticks left of the Slag Surge ultimate. */
    public int surgeTicks;

    /**
     * The active hero's extra resource as a 0-1 fraction (Bulwark: barrier health), or -1 when the
     * hero has none. Shown on the HUD.
     */
    public float resource = -1.0F;

    /** {@link #resource} quantised to whole percent, as last sent to the client. */
    public int lastSentResourcePercent = Integer.MIN_VALUE;

    public HeroState state() {
        return state;
    }
}
