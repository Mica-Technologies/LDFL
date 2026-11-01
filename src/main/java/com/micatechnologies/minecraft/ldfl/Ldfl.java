package com.micatechnologies.minecraft.ldfl;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main mod class for LDFL (Limitless Development: Frontline Heroes).
 *
 * <p>LDFL adds a hero system inspired by team hero shooters, with no third-party IP. Holding a
 * hero's signature item makes you that hero: you get the role passive (Tank, Damage or Support)
 * and three keybound abilities (two on cooldowns, one ultimate). It also adds the heroes' gear
 * and a set of futuristic decor blocks.</p>
 *
 * <p>Lifecycle: {@code preInit} sets up config, the network channel, capabilities and event-bus
 * registrations and must not touch a {@code World}. {@code init} handles client keybinds and
 * renderers through the proxy.</p>
 */
@Mod(modid = LdflConstants.MOD_NAMESPACE, name = LdflConstants.MOD_NAME,
     version = LdflConstants.MOD_VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class Ldfl {

    public static final Logger LOGGER = LogManager.getLogger(LdflConstants.MOD_NAMESPACE);

    @Mod.Instance(LdflConstants.MOD_NAMESPACE)
    public static Ldfl instance;

    @SidedProxy(clientSide = "com.micatechnologies.minecraft.ldfl.LdflClientProxy",
                serverSide = "com.micatechnologies.minecraft.ldfl.LdflCommonProxy")
    public static LdflProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
