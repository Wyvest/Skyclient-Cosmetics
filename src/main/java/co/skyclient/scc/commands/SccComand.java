package co.skyclient.scc.commands;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Description;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import co.skyclient.scc.SkyclientCosmetics;
import co.skyclient.scc.cosmetics.Tag;
import co.skyclient.scc.cosmetics.TagCosmetics;
import co.skyclient.scc.utils.ChatUtils;

@Command(value = "scc", aliases = "skyclientcosmetics")
public class SccComand {

    @Main
    public void handle() {
        SkyclientCosmetics.config.openGui();
    }

    @SubCommand
    public void reload() {
        if (TagCosmetics.getInstance().isInitialized()) TagCosmetics.getInstance().reInitialize();
    }

    @SubCommand
    public void displaytag(@Description("Player name") String name) {
        Tag tag = TagCosmetics.getInstance().getTag(name);
        if (tag != null) {
            ChatUtils.sendSystemMessage(name + "'s tag: " + tag);
        }
    }

    @SubCommand
    public void tags() {
        ChatUtils.sendSystemMessage(String.valueOf(TagCosmetics.getInstance().getTags()));
    }
}
