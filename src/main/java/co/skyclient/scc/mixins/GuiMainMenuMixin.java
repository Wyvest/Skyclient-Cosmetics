package co.skyclient.scc.mixins;

import co.skyclient.scc.gui.greeting.IntroductionGreetingSlide;
import co.skyclient.scc.utils.Files;
import co.skyclient.scc.utils.TickDelay;
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
            new TickDelay(2, () -> Minecraft.getMinecraft().displayGuiScreen(new IntroductionGreetingSlide()));
        }
    }
}
