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

package co.skyclient.scc.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Button;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator;
import cc.polyfrost.oneconfig.utils.TickDelay;
import co.skyclient.scc.cosmetics.TagCosmetics;
import co.skyclient.scc.gui.greeting.IntroductionGreetingSlide;
import co.skyclient.scc.gui.greeting.components.GreetingSlide;
import co.skyclient.scc.utils.Files;
import net.minecraft.client.Minecraft;

public class Settings extends Config {

    @Switch(name = "Custom Main Menu", description = "Enable the SkyClient Custom Main Menu.", category = "Main")
    public static boolean customMainMenu = true;

    @Button(
            name = "Setup SkyClient Again",
            text = "Go",
            category = "Main"
    )
    public static void r() {
        Files.greetingFile.delete();
        GreetingSlide.Companion.setCurrentSlide(null);
        GreetingSlide.Companion.setPreviousScale(Integer.MIN_VALUE);
        new TickDelay(() -> Minecraft.getMinecraft().displayGuiScreen(new IntroductionGreetingSlide()), 2);
    }

    @Switch(name = "Show Custom Tags", description = "Show the custom tags, which are the main focus of this mod.", category = "Main", subcategory = "Tags")
    public static boolean showTags = true;

    @Switch(name = "Shorten Custom Tags", description = "Use shorter tags.\n[BOOSTER] becomes [B], for example.", category = "Main", subcategory = "Tags")
    public static boolean shortenTags = false;

    @Button(name = "Reload Tags", description = "Reloads the custom tags.", category = "Misc", subcategory = "Tags", text = "Reload")
    public static void reloadTags() {
        if (TagCosmetics.getInstance().isInitialized()) TagCosmetics.getInstance().reInitialize();
    }

    @Text(name = "Discord RPC Second Line", description = "Allows you to edit the second line of the Discord RPC\n\u00A7aAllows usage of Placeholders. More info on the wiki (https://github.com/koxx12-dev/Skyclient-Cosmetics/wiki/Discord-RPC)", category = "Main", subcategory = "Discord Rich Presence")
    public static String rpcLineTwo = "Playing Hypixel";

    @Text(name = "Discord RPC First Line", description = "Allows you to edit the first line of the Discord RPC\n\u00A7aAllows usage of Placeholders. More info on the wiki (https://github.com/koxx12-dev/Skyclient-Cosmetics/wiki/Discord-RPC)", category = "Main", subcategory = "Discord Rich Presence")
    public static String rpcLineOne = "%player% is very cool";

    @Text(name = "Discord RPC Img Text", description = "Allows you to set text of the img\n\u00A7aAllows usage of Placeholders. More info on the wiki (https://github.com/koxx12-dev/Skyclient-Cosmetics/wiki/Discord-RPC)", category = "Main", subcategory = "Discord Rich Presence")
    public static String rpcImgText = "SkyClient is cool";

    @Switch(name = "Discord RPC", description = "Enables Discord RPC", category = "Main", subcategory = "Discord Rich Presence")
    public static boolean rpc = true;

    @Switch(name = "First Time Message", description = "Get \"First time message\" when u join next time", category = "Misc", subcategory = "Chat")
    public static boolean joinMessage = true;

    public static boolean hasWipedOutPSS = false;
    public static boolean hasWipedOutReplayModAutoRecording = false;

    public Settings() {
        super(new Mod("SkyClientCosmetics", ModType.UTIL_QOL, "/assets/scc/SkyClient.png", new VigilanceMigrator("./SkyclientCosmetics/skyclientcosmetics.toml")), "skyclientcosmetics.json");

        //final Class<Settings> SettingsClass = Settings.class;

        initialize();

        //addDependency("sbeBadMode","rpc");
        addDependency("rpcLineTwo", "rpc");
        addDependency("rpcLineOne", "rpc");
        addDependency("rpcImgText", "rpc");
        hideIf("joinMessage", () -> true);

        addDependency("shortenTags", "showTags");
        //addDependency("reloadTags","showTags");
        //addDependency(SettingsClass.getField("r"),SettingsClass.getField("showTags"));
        addDependency("displayTags", "showTags");
    }
}
