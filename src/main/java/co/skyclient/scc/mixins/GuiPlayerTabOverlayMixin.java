package co.skyclient.scc.mixins;

import co.skyclient.scc.hooks.GuiPlayerTabOverlayHook;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin {
    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void getPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> cir) {
        GuiPlayerTabOverlayHook.iHateMixinZeroPointSeven(networkPlayerInfoIn, cir);
    }
}
