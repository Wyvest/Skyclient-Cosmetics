/*
 * SkyclientCosmetics - Cool cosmetics for a mod installer Skyclient!
 * Copyright (C) koxx12-dev [2021 - 2021]
 *
 * This program comes with ABSOLUTELY NO WARRANTY
 * This is free software, and you are welcome to redistribute it
 * under the certain conditions that can be found here
 * https://www.gnu.org/licenses/lgpl-3.0.en.html
 *
 * If you have any questions or concerns, please create
 * an issue on the github page that can be found under this url
 * https://github.com/koxx12-dev/Skyclient-Cosmetics
 *
 * If you have a private concern, please contact me on
 * Discord: Koxx12#8061
 */

package co.skyclient.scc;

import club.sk1er.patcher.config.PatcherConfig;
import co.skyclient.scc.commands.SccComand;
import co.skyclient.scc.config.Settings;
import co.skyclient.scc.cosmetics.TagCosmetics;
import co.skyclient.scc.gui.greeting.OptimizationSlide;
import co.skyclient.scc.listeners.ChatListeners;
import co.skyclient.scc.listeners.GuiListeners;
import co.skyclient.scc.listeners.PlayerListeners;
import co.skyclient.scc.mixins.ServerListAccessor;
import co.skyclient.scc.rpc.RPC;
import co.skyclient.scc.utils.Files;
import de.jcm.discordgamesdk.Core;
import gg.essential.vigilance.Vigilant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Mod(modid = SkyclientCosmetics.MOD_ID, name = SkyclientCosmetics.MOD_NAME, version = SkyclientCosmetics.MOD_VERSION, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class SkyclientCosmetics {

    public static final String MOD_NAME = "@NAME@";
    public static final String MOD_ID = "@ID@";
    public static final String MOD_VERSION = "@VER@";

    public static boolean rpcRunning = false;

    public static boolean rpcOn = false;

    public static Settings config;

    public static Core rpcCore;

    //public static String partyID = RPC.generateID();

    public static Logger LOGGER;

    public static boolean isPatcher;
    public static Vigilant scuConfig = null;
    //private static boolean hasFailed;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {

        ProgressManager.ProgressBar progress = ProgressManager.push("Preinitialization", 3);

        progress.step("Setting up Files");

        Files.setup();

        progress.step("Loading Vigilance");

        config = new Settings();
        config.preload();

        progress.step("Getting Log4j Logger");

        LOGGER = event.getModLog();

        ProgressManager.pop(progress);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {

        ProgressManager.ProgressBar progress = ProgressManager.push("Initialization", 5);

        progress.step("Registering Listeners");

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ChatListeners());
        MinecraftForge.EVENT_BUS.register(new PlayerListeners());
        MinecraftForge.EVENT_BUS.register(new GuiListeners());

        progress.step("Registering Commands");

        new SccComand().register();

        progress.step("Loading Tags");

        TagCosmetics.getInstance().initialize();

        progress.step("Initializing Placeholders");

        co.skyclient.scc.utils.StringUtils.initPlaceholders();

        progress.step("Starting RPC");

        RPC.INSTANCE.rpcManager();

        MinecraftForge.EVENT_BUS.register(RPC.INSTANCE);

        ProgressManager.pop(progress);
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        ProgressManager.ProgressBar progress = ProgressManager.push("Postinitialization", 3);

        progress.step("Detecting Mods");
        for (ModContainer mod : Loader.instance().getActiveModList()) {
            if ("patcher".equals(mod.getModId())) {
                isPatcher = true;
                try {
                    if (new DefaultArtifactVersion(StringUtils.substringBeforeLast(mod.getVersion(), "+")).compareTo(new DefaultArtifactVersion("1.8.1")) > 0) {
                        OptimizationSlide.Companion.sendCTMFixNotification();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("skyblockclientupdater".equals(mod.getModId())) {
                try {
                    scuConfig = (Vigilant) Class.forName("mynameisjeff.skyblockclientupdater.config.Config").getDeclaredField("INSTANCE").get(null);
                } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        progress.step("Checking Greeting Slides");

        try {
            String text = FileUtils.readFileToString(Files.greetingFile, StandardCharsets.UTF_8);
            if (!text.endsWith("2")) {
                FileUtils.writeStringToFile(Files.greetingFile, text + "\n2", StandardCharsets.UTF_8);
                if (isPatcher) {
                    PatcherConfig.chunkUpdateLimit = 250;
                    PatcherConfig.INSTANCE.markDirty();
                    PatcherConfig.INSTANCE.writeData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        progress.step("Setting Default Servers");
        ServerList serverList = new ServerList(Minecraft.getMinecraft());
        if (((ServerListAccessor) serverList).getServers().stream().noneMatch((a) -> StringUtils.endsWithAny(a.serverIP.toLowerCase(Locale.ENGLISH), "hypixel.net", "hypixel.io"))) {
            serverList.addServerData(new ServerData("Hypixel", "mc.hypixel.net", false));
            serverList.saveServerList();
        }

        ProgressManager.pop(progress);
    }

}
