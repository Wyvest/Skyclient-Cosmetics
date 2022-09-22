package co.skyclient.scc.mixins;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

// powered by nopo
@Pseudo
@Mixin(targets = "gg.skytils.skytilsmod.core.UpdateChecker")
public class SkytilsMixin {

    @Dynamic
    @Inject(method = "onGuiOpen", at = @At("HEAD"), remap = false, cancellable = true)
    public void onGuiOpen(CallbackInfo ci) {
        try {
            ci.cancel();
        } catch (CancellationException e) {
            e.printStackTrace();
        }
    }
}
