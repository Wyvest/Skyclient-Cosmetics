package co.skyclient.scc.commands;

import cc.woverflow.onecore.utils.GuiUtils;
import co.skyclient.scc.cosmetics.Tag;
import co.skyclient.scc.cosmetics.TagCosmetics;
import co.skyclient.scc.utils.ChatUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.commands.DisplayName;
import gg.essential.api.commands.SubCommand;
import co.skyclient.scc.SkyclientCosmetics;

import java.util.HashSet;
import java.util.Set;

public class SccComand extends Command {
    private final Set<Alias> hashSet = new HashSet<>();

    public SccComand() {
        super("scc");
        hashSet.add(new Alias("skyclientcosmetics"));
    }

    @Override
    public Set<Alias> getCommandAliases() {
        return hashSet;
    }

    @DefaultHandler
    public void handle() {
        GuiUtils.openScreen(SkyclientCosmetics.config);
    }

    @SubCommand("reload")
    public void reload() {
        if (TagCosmetics.getInstance().isInitialized()) TagCosmetics.getInstance().reInitialize();
    }

    @SubCommand("displaytag")
    public void displaytag(@DisplayName("Player name") String name) {
        Tag tag = TagCosmetics.getInstance().getTag(name);
        if (tag != null) {
            ChatUtils.sendSystemMessage(name + "'s tag: " + tag);
        }
    }

    @SubCommand("tags")
    public void tags() {
        ChatUtils.sendSystemMessage(String.valueOf(TagCosmetics.getInstance().getTags()));
    }
}
