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

package co.skyclient.scc.utils;

import co.skyclient.scc.cosmetics.Tag;
import co.skyclient.scc.cosmetics.TagCosmetics;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern PlaceholderPattern = Pattern.compile("%([a-zA-Z]+)%");
    private static final Map<String, Supplier<String>> placeholders = new HashMap<>();

    public static String cleanMessage(String msg) {
        return msg.replaceAll("\u00A7[a-f0-9kmolnr]", "");
    }

    public static String cleanEmojis(String msg) {
        return msg.replaceAll("[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]", "");
    }

    public static void initPlaceholders() {
        placeholders.put("player", () -> Minecraft.getMinecraft().getSession().getUsername());
        placeholders.put("fps", () -> String.valueOf(Minecraft.getDebugFPS()));
        placeholders.put("hand", () -> cleanMessage(Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName()));
        placeholders.put("tag", () -> {
            Tag tag = TagCosmetics.getInstance().getTag(Minecraft.getMinecraft().getSession().getUsername());
            if (tag != null) {
                return TagCosmetics.getInstance().isInitialized() ? cleanMessage(tag.getFullTag()) : "";
            } else {
                return "";
            }
        });
        placeholders.put("shorttag", () -> {
            Tag tag = TagCosmetics.getInstance().getTag(Minecraft.getMinecraft().getSession().getUsername());
            if (tag != null) {
                return TagCosmetics.getInstance().isInitialized() ? cleanMessage(tag.getShortTag()) : "";
            } else {
                return "";
            }
        });
        placeholders.put("bits", SidebarUtils::getBits);
        placeholders.put("time", SidebarUtils::getSBTime);
        placeholders.put("date", SidebarUtils::getSBDate);
        placeholders.put("loc", SidebarUtils::getSBLoc);
        placeholders.put("server", SidebarUtils::getServer);
        placeholders.put("objective", SidebarUtils::getObjective);
        placeholders.put("purse", SidebarUtils::getPurse);
    }

    public static String discordPlaceholder(String text) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PlaceholderPattern.matcher(text);
        int lastEnd = 0;
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            Supplier<String> supplier = placeholders.get(placeholder);
            if (supplier != null) {
                sb.append(text, lastEnd, matcher.start())
                        .append(supplier.get());
            }
            lastEnd = matcher.end();
        }
        sb.append(text.substring(lastEnd));
        return sb.toString();
    }
}
