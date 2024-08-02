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

import cc.polyfrost.oneconfig.libs.universal.wrappers.message.UTextComponent;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import co.skyclient.scc.SkyclientCosmetics;
import co.skyclient.scc.config.Settings;
import co.skyclient.scc.cosmetics.Tag;
import co.skyclient.scc.cosmetics.TagCosmetics;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class TagListeners {

    private static final HashSet<String> ranks = new HashSet<>(Arrays.asList(
            "[VIP]", "[VIP+]", "[MVP]", "[MVP+]", "[MVP++]", "[YOUTUBE]", "[GM]", "[ADMIN]", "[MOJANG]",
            "[EVENTS]", "[PIG+++]", "[OWNER]", "[MCP]"
    ));
    private static final HashMap<String, String> filteredPotentialRanks = new HashMap<>(50);
    public static Map<String, String> nametags = new HashMap<>(400);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatMsgTags(ClientChatReceivedEvent event) {
        if (SkyclientCosmetics.config.enabled && Settings.showTags && HypixelUtils.INSTANCE.isHypixel()) {
            try {
                if (event.type == 0) {
                    if (!(event.message instanceof ChatComponentText)) return;
                    String text = event.message.getFormattedText();
                    for (Map.Entry<String, Tag> tagEntry : TagCosmetics.getInstance().getTags().entrySet()) {
                        String username = tagEntry.getKey();
                        if (text.contains(username)) {
                            event.message = replaceChat(event.message, username, tagEntry.getValue());
                            text = event.message.getFormattedText();
                            if (text.charAt(text.indexOf(username) + username.length() + 6) == ':') {
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (SkyclientCosmetics.config.enabled && Settings.showTags && HypixelUtils.INSTANCE.isHypixel()) {
            for (int i = 0; i < event.toolTip.size(); i++) {
                String tooltip = event.toolTip.get(i);
                for (Map.Entry<String, Tag> tagEntry : TagCosmetics.getInstance().getTags().entrySet()) {
                    String username = tagEntry.getKey();
                    if (tooltip.contains(username)) {
                        event.toolTip.set(i, addTag(username, tagEntry.getValue(), tooltip, null));
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderLiving(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
        if (SkyclientCosmetics.config.enabled && Settings.showTags && HypixelUtils.INSTANCE.isHypixel()) {
            Entity entity = event.entity;
            if (entity instanceof EntityArmorStand && !entity.isDead && entity.hasCustomName()) {
                String name = entity.getCustomNameTag();
                if (name.charAt(name.length() - 1) == '❤') return;

                String id = entity.getUniqueID().toString();
                String nametag = nametags.get(id);

                if (name.equals(nametag)) {
                    entity.setCustomNameTag(nametag);
                } else {
                    for (Map.Entry<String, Tag> tagEntry : TagCosmetics.getInstance().getTags().entrySet()) {
                        String username = tagEntry.getKey();
                        if (name.contains(username)) {
                            name = addTag(username, tagEntry.getValue(), name, null);
                        }
                    }

                    nametags.put(id, name);
                    entity.setCustomNameTag(name);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldChange(WorldEvent.Load event) {
        nametags.clear();
    }

    // https://github.com/Moulberry/Hychat/blob/master/src/main/java/io/github/moulberry/hychat/util/TextProcessing.java#L23
    private IChatComponent replaceChat(IChatComponent component, String user, Tag tag) {
        IChatComponent newComponent;
        ChatComponentText text = (ChatComponentText) component;

        newComponent = new ChatComponentText(addTag(user, tag, text.getUnformattedTextForChat(), text.getFormattedText()));
        ChatStyle style = text.getChatStyle().createShallowCopy();
        if (style.getChatHoverEvent() != null && style.getChatHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT) {
            style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, replaceChat(style.getChatHoverEvent().getValue(), user, tag)));
        }
        newComponent.setChatStyle(style);

        for (IChatComponent sibling : text.getSiblings()) {
            newComponent.appendSibling(replaceChat(sibling, user, tag));
        }

        return newComponent;
    }

    private String addTag(String username, Tag tag, String message, String formattedMessage) {
        if (!message.contains(username)) return message;
        int usernameIndex = message.indexOf(username);

        int wordStart = usernameIndex - 2;

        while (wordStart >= 0 && !Character.isWhitespace(message.charAt(wordStart))) {
            wordStart--;
        }
        wordStart++;
        if (wordStart < 0) wordStart = 0;

        String wordBeforeUsername = message.substring(wordStart, usernameIndex);
        String afterUsername = message.substring(usernameIndex + username.length());

        String substringed = message.substring(wordStart, usernameIndex + username.length());
        boolean shouldReiterate = afterUsername.contains(username);
        if (wordBeforeUsername.contains("[") && wordBeforeUsername.contains("]") && !wordBeforeUsername.contains("✫")) { // do some rudimentary checks to see if it's a rank
            if (ranks.contains(filteredPotentialRanks.computeIfAbsent(wordBeforeUsername, k -> UTextComponent.Companion.stripFormatting(wordBeforeUsername).trim()))) {

                return message.substring(0, wordStart) + tag.getTag() + " " + (shouldReiterate ?
                        (substringed + addTag(username, tag, afterUsername, null))
                        : message.substring(wordStart));
            }
        }
        if (message.substring(wordStart, usernameIndex).contains("§")) {
            return message.substring(0, wordStart) + tag.getTag() + " " + (shouldReiterate ?
                    (substringed + addTag(username, tag, afterUsername, null))
                    : message.substring(wordStart));
        } else if (formattedMessage != null) {
            int formattedUsernameIndex = formattedMessage.indexOf(username);
            if (formattedUsernameIndex - 2 < 0) return message;
            if (formattedMessage.substring(formattedUsernameIndex - 2, formattedUsernameIndex).contains("§")) {
                return message.substring(0, usernameIndex) + tag.getTag() + " " + (shouldReiterate ?
                        (message.substring(usernameIndex, usernameIndex + username.length()) + addTag(username, tag, afterUsername, null))
                        : message.substring(usernameIndex));
            }
        }

        return message;
    }
}
