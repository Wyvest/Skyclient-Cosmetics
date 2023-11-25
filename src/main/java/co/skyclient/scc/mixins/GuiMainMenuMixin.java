package co.skyclient.scc.mixins;

import cc.polyfrost.oneconfig.utils.TickDelay;
import co.skyclient.scc.gui.greeting.IntroductionGreetingSlide;
import co.skyclient.scc.utils.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin {

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!Files.greetingFile.exists()) {
            new TickDelay(() -> Minecraft.getMinecraft().displayGuiScreen(new IntroductionGreetingSlide()), 2);
        }
    }
}
