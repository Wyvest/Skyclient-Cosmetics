package co.skyclient.scc.mixins;

import co.skyclient.scc.gui.greeting.IntroductionGreetingSlide;
import co.skyclient.scc.utils.Files;
import co.skyclient.scc.utils.TickDelay;
import gg.essential.api.EssentialAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin {
    @Unique
    private static boolean skyclient$bypass = false;

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if ((!Files.greetingFile.exists() || EssentialAPI.getMinecraftUtil().isDevelopment()) && !skyclient$bypass) {
            skyclient$bypass = true;
            new TickDelay(3, () -> Minecraft.getMinecraft().displayGuiScreen(new IntroductionGreetingSlide()));
        }
    }
}
