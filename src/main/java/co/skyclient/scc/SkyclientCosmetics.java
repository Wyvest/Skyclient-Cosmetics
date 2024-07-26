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

import cc.polyfrost.oneconfig.utils.TickDelay;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import club.sk1er.patcher.config.PatcherConfig;
import co.skyclient.scc.commands.SccComand;
import co.skyclient.scc.config.Settings;
import co.skyclient.scc.cosmetics.TagCosmetics;
import co.skyclient.scc.gui.greeting.IntroductionGreetingSlide;
import co.skyclient.scc.gui.greeting.OptimizationSlide;
import co.skyclient.scc.gui.greeting.components.GreetingSlide;
import co.skyclient.scc.listeners.TagListeners;
import co.skyclient.scc.listeners.GuiListeners;
import co.skyclient.scc.listeners.PlayerListeners;
import co.skyclient.scc.mixins.ServerListAccessor;
import co.skyclient.scc.rpc.RPC;
import co.skyclient.scc.utils.Files;
import co.skyclient.scc.utils.ReplayModCompat;
import de.jcm.discordgamesdk.Core;
import me.partlysanestudios.partlysaneskies.config.OneConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    public static boolean isEssential;
    public static boolean isReplayMod = false;
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
        MinecraftForge.EVENT_BUS.register(new TagListeners());
        MinecraftForge.EVENT_BUS.register(new PlayerListeners());
        MinecraftForge.EVENT_BUS.register(new GuiListeners());

        progress.step("Registering Commands");

        CommandManager.INSTANCE.registerCommand(new SccComand());

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
            } else if ("essential".equals(mod.getModId())) {
                isEssential = true;
            } else if ("partlysaneskies".equals(mod.getModId())) {
                try {
                    try {
                        if (!Settings.hasWipedOutPSS) {
                            Class.forName("me.partlysanestudios.partlysaneskies.config.OneConfigScreen"); // check if the class exists
                            OneConfigScreen.INSTANCE.setCustomMainMenu(false);
                            OneConfigScreen.INSTANCE.save();
                            Settings.hasWipedOutPSS = true;
                            config.save();
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("replaymod".equals(mod.getModId())) {
                isReplayMod = true;
                try {
                    Class<?> replayMod = Class.forName("com.replaymod.core.ReplayMod", false, getClass().getClassLoader());
                    replayMod.getDeclaredField("instance");
                    replayMod.getDeclaredMethod("getSettingsRegistry");
                    Class<?> settingsRegistry = Class.forName("com.replaymod.core.SettingsRegistry", false, getClass().getClassLoader());
                    Class<?> settingKey = Class.forName("com.replaymod.core.SettingsRegistry$SettingKey", false, getClass().getClassLoader());
                    Class<?> settingKeys = Class.forName("com.replaymod.core.SettingsRegistry$SettingKeys", false, getClass().getClassLoader());
                    settingsRegistry.getDeclaredMethod("set", settingsRegistry.getDeclaredClasses()[0], Object.class);
                    settingsRegistry.getDeclaredMethod("save");
                    Class<?> settings = Class.forName("com.replaymod.recording.Setting", false, getClass().getClassLoader());
                    settings.getDeclaredField("AUTO_START_RECORDING");
                } catch (Exception e) {
                    e.printStackTrace();
                    isReplayMod = false;
                }
                if (isReplayMod) {
                    if (!Settings.hasWipedOutReplayModAutoRecording) {
                        ReplayModCompat.doReplayModStuff();
                    }
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

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu && !Files.greetingFile.exists()) {
            new TickDelay(() -> {
                try {
                    Class<GreetingSlide<?>> clazz = GreetingSlide.Companion.getCurrentSlide();
                    Minecraft.getMinecraft().displayGuiScreen(clazz != null ? clazz.newInstance() : new IntroductionGreetingSlide());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }, 3);
        }
    }

}
