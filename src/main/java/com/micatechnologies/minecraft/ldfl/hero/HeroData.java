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

    /** Fractional support regen carried between ticks. */
    public float regenCarry;

    public HeroState state() {
        return state;
    }
}
