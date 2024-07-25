package co.skyclient.scc.mixins;

import co.skyclient.scc.SkyclientCosmetics;
import co.skyclient.scc.config.Settings;
import co.skyclient.scc.gui.SkyClientMainMenu;
import co.skyclient.scc.utils.IconLoader;
import co.skyclient.scc.utils.JsDelivrUtils;
import co.skyclient.scc.utils.MixinHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.util.Objects;

@Mixin(value = Minecraft.class, priority = Integer.MIN_VALUE)
public abstract class MinecraftMixin {
    @Shadow
    public abstract void displayGuiScreen(GuiScreen guiScreenIn);

    @Inject(method = "startGame", at = @At("HEAD"))
    private void onStartGame(CallbackInfo ci) {
        JsDelivrUtils.initialize();
    }

    @Inject(method = "displayGuiScreen", at = @At("HEAD"), cancellable = true)
    private void onDisplayScreen(GuiScreen i, CallbackInfo ci) {
        if (Settings.customMainMenu && SkyclientCosmetics.config.enabled) {
            if (i instanceof GuiMainMenu && !(i instanceof SkyClientMainMenu) && MixinHook.hasEssentialAcceptedTOS()) {
                ci.cancel();
                displayGuiScreen(new SkyClientMainMenu());
            }
        }
    }

    @Inject(method = "setWindowIcon", at = @At("HEAD"), cancellable = true)
    private void redirectWindowIcon(CallbackInfo ci) {
        try {
            Display.setIcon(IconLoader.load(ImageIO.read(Objects.requireNonNull(SkyclientCosmetics.class.getResourceAsStream("/assets/scc/icon.png")))));
            ci.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Redirect(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V", remap = false))
    private void redirectTitle(String newTitle) {
        Display.setTitle("SkyClient (Forge 1.8.9)");
    }
}
