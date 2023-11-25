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

package co.skyclient.scc.listeners;

import cc.polyfrost.oneconfig.libs.universal.ChatColor;
import co.skyclient.scc.config.Settings;
import co.skyclient.scc.cosmetics.Tag;
import co.skyclient.scc.cosmetics.TagCosmetics;
import co.skyclient.scc.utils.StringUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListeners {

    //TODO: Stop using fucking regexes and make this more modular also optimize this mess

    public static Pattern chatRegex = Pattern.compile("((To|From)\\s)?((Guild|Co-op|Officer|Party)\\s\\>\\s)?(\\[(MVP|VIP|PIG|YOUTUBE|MOD|HELPER|ADMIN|OWNER|MOJANG|SLOTH|EVENTS|MCP)([\\+]{1,2})?\\]\\s)?[\\w]+:\\s.*");
    public static Pattern dmRegex = Pattern.compile("((To|From)\\s)(\\[(MVP|VIP|PIG|YOUTUBE|MOD|HELPER|ADMIN|OWNER|MOJANG|SLOTH|EVENTS|MCP)([\\+]{1,2})?\\]\\s)?[\\w]+:\\s.*");
    public static Pattern groupRegex = Pattern.compile("((Guild|Co-op|Officer|Party)\\s\\>\\s)(\\[(MVP|VIP|PIG|YOUTUBE|MOD|HELPER|ADMIN|OWNER|MOJANG|SLOTH|EVENTS|MCP)([\\+]{1,2})?\\]\\s)?[\\w]+:\\s.*");
    public static Pattern rankRegex = Pattern.compile("\\[(MVP|VIP|PIG|YOUTUBE|MOD|HELPER|ADMIN|OWNER|MOJANG|SLOTH|EVENTS|MCP)([\\+]{1,2})?\\]");
    public static Pattern groupRankRegex = Pattern.compile("((\u00A7[a-f0-9kmolnr](To|From))|(\u00A7[a-f0-9kmolnr](Guild|Co-op|Officer|Party)\\s(\u00A7[a-f0-9kmolnr])?\\\\u003e))\\s((\u00A7[a-f0-9kmolnr])?(\\[(MVP|VIP|PIG|YOUTUBE|MOD|HELPER|ADMIN|OWNER|MOJANG|SLOTH|EVENTS|MCP)([\\+]{1,2})?\\]\\s)?)?[\\w]+(\u00A7[a-f0-9kmolnr])?:");

    private static final JsonParser PARSER = new JsonParser();

    @SubscribeEvent
    public void onChatMsgTags(ClientChatReceivedEvent event) {
        if (Settings.showTags) {
            try {
                if (event.type == 0) {
                    String cleanMessage = StringUtils.cleanMessage(event.message.getUnformattedText());
                    List<String> splitMessage = Arrays.asList(cleanMessage.split("\\s"));
                    String parsedMessage = IChatComponent.Serializer.componentToJson(event.message);
                    Matcher parsedMatcher = groupRankRegex.matcher(parsedMessage);
                    String playerName;
                    if (chatRegex.matcher(cleanMessage).matches()) {
                        if (dmRegex.matcher(cleanMessage).matches()) {
                            JsonObject jsonParsedMsg = PARSER.parse(parsedMessage).getAsJsonObject();
                            String playerText = jsonParsedMsg.get("extra").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                            String playerColor;
                            try {
                                playerColor = ChatColor.valueOf(jsonParsedMsg.get("extra").getAsJsonArray().get(0).getAsJsonObject().get("color").getAsString().toUpperCase()).toString();
                            } catch (Exception e) {
                                playerColor = ChatColor.GRAY.toString();
                            }

                            playerName = playerText.replaceAll(rankRegex.pattern(), "").trim();

                            Tag tag = TagCosmetics.getInstance().getTag(playerName);
                            if (tag != null) {
                                String newVal = tag.getTag() + " " + playerColor + playerText;
                                jsonParsedMsg.get("extra").getAsJsonArray().get(0).getAsJsonObject().addProperty("text", newVal);
                                event.message = IChatComponent.Serializer.jsonToComponent(jsonParsedMsg.toString());
                            }
                        } else if (groupRegex.matcher(cleanMessage).matches()) {
                            if (parsedMatcher.find()) {
                                String msg = parsedMatcher.group(0);
                                List<String> msgList = new ArrayList<>(Arrays.asList(msg.split(" ")));
                                List<String> cleanMsg = Arrays.asList(StringUtils.cleanMessage(msg).split(" "));
                                playerName = cleanMsg.get(cleanMsg.size() - 1).replaceAll(":", "");
                                Tag tag = TagCosmetics.getInstance().getTag(playerName);
                                if (tag != null) {
                                    msgList.add(2, tag.getTag());
                                    String newVal = String.join(" ", msgList);
                                    event.message = IChatComponent.Serializer.jsonToComponent(parsedMessage.replace(msg, newVal));
                                }
                            }
                        } else {
                            if (rankRegex.matcher(splitMessage.get(0)).matches()) {
                                playerName = splitMessage.get(1);
                            } else {
                                playerName = splitMessage.get(0);
                            }
                            playerName = playerName.replaceAll(":", "");
                            Tag tag = TagCosmetics.getInstance().getTag(playerName);
                            if (tag != null) {
                                event.message = new ChatComponentText(tag.getTag() + " ").appendSibling(event.message);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
