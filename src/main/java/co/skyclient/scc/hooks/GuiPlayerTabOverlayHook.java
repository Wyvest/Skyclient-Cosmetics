package co.skyclient.scc.hooks;

import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import co.skyclient.scc.SkyclientCosmetics;
import co.skyclient.scc.config.Settings;
import co.skyclient.scc.cosmetics.Tag;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class GuiPlayerTabOverlayHook {
    public static void iHateMixinZeroPointSeven(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> cir) {
        if (SkyclientCosmetics.config.enabled) {
            if (Settings.showTags) {
                if (HypixelUtils.INSTANCE.isHypixel()) {
                    Tag tag = ((NetworkPlayerInfoHook) networkPlayerInfoIn).scc$getTag();
                    if (tag != null) {
                        cir.setReturnValue(tag.getTag() + " " + cir.getReturnValue());
                    }
                }
            }
        }
    }
}
