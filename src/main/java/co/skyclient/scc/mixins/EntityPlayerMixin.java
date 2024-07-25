package co.skyclient.scc.mixins;

import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import co.skyclient.scc.SkyclientCosmetics;
import co.skyclient.scc.config.Settings;
import co.skyclient.scc.cosmetics.Tag;
import co.skyclient.scc.cosmetics.TagCosmetics;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {

    @Shadow public abstract String getName();
    @Unique private Tag scc$tag;
    @Unique private boolean scc$tagChecked;

    @ModifyVariable(method = "getDisplayName", at = @At("STORE"), ordinal = 0)
    private IChatComponent modifyName(IChatComponent component) {
        if (SkyclientCosmetics.config.enabled && Settings.showTags && HypixelUtils.INSTANCE.isHypixel()) {
            if (!scc$tagChecked) {
                scc$tag = TagCosmetics.getInstance().getTag(getName());
                scc$tagChecked = true;
            }
            if (scc$tag != null) {
                component.appendSibling(new ChatComponentText(scc$tag.getTag() + " "));
            }
        }
        return component;
    }
}
