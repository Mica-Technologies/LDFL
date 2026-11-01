package com.micatechnologies.minecraft.ldfl;

import com.micatechnologies.minecraft.ldfl.command.CommandLdfl;
import com.micatechnologies.minecraft.ldfl.hero.HeroCapability;
import com.micatechnologies.minecraft.ldfl.hero.HeroEvents;
import com.micatechnologies.minecraft.ldfl.hero.Heroes;
import com.micatechnologies.minecraft.ldfl.hero.bulwark.BarrierLogic;
import com.micatechnologies.minecraft.ldfl.network.LdflNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
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
 * <p>Lifecycle: {@code preInit} sets up heroes, the network channel, the capability and event-bus
 * registrations, and must not touch a {@code World}. {@code init} registers client keybinds
 * through the proxy.</p>
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
        Heroes.init();
        LdflNetwork.register();
        HeroCapability.register();
        MinecraftForge.EVENT_BUS.register(new HeroCapability.Attacher());
        MinecraftForge.EVENT_BUS.register(new HeroEvents());
        MinecraftForge.EVENT_BUS.register(new BarrierLogic());
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandLdfl());
    }
}
