package com.micatechnologies.minecraft.ldfl.network;

import com.micatechnologies.minecraft.ldfl.Ldfl;
import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySlot;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Server to client: the player's hero state, for the HUD. Sent when it changes, plus a slow
 * periodic refresh. The handler goes through the proxy, so this class stays safe to load on a
 * dedicated server.
 */
public class PacketHeroSync implements IMessage {

    /** Active hero id, or empty for none. */
    public String heroId = "";
    public int[] cooldowns = new int[AbilitySlot.values().length];
    public float ultCharge;
    /** The hero's extra resource as 0-1 (Bulwark: barrier health), or -1 for none. */
    public float resource = -1.0F;

    public PacketHeroSync() {
    }

    public PacketHeroSync(String heroId, int[] cooldowns, float ultCharge, float resource) {
        this.heroId = heroId == null ? "" : heroId;
        this.cooldowns = cooldowns;
        this.ultCharge = ultCharge;
        this.resource = resource;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        heroId = ByteBufUtils.readUTF8String(buf);
        int n = buf.readByte();
        cooldowns = new int[AbilitySlot.values().length];
        for (int i = 0; i < n; i++) {
            int v = ByteBufUtils.readVarInt(buf, 5);
            if (i < cooldowns.length) {
                cooldowns[i] = v;
            }
        }
        ultCharge = buf.readFloat();
        resource = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, heroId);
        buf.writeByte(cooldowns.length);
        for (int c : cooldowns) {
            ByteBufUtils.writeVarInt(buf, c, 5);
        }
        buf.writeFloat(ultCharge);
        buf.writeFloat(resource);
    }

    public static class Handler implements IMessageHandler<PacketHeroSync, IMessage> {
        @Override
        public IMessage onMessage(PacketHeroSync message, MessageContext ctx) {
            Ldfl.proxy.onHeroSync(message);
            return null;
        }
    }
}
