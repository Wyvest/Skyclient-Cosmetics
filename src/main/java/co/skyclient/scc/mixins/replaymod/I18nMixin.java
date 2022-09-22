package co.skyclient.scc.mixins.replaymod;

import co.skyclient.scc.config.Settings;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = I18n.class, remap = false)
public class I18nMixin {
    @Inject(method = "format", at = @At("HEAD"), cancellable = true)
    private static void onFormat(String translateKey, Object[] parameters, CallbackInfoReturnable<String> cir) {
        if (Settings.customMainMenu && translateKey.equals("replaymod.gui.replayviewer")) {
            cir.setReturnValue("");
        }
    }
}
