package co.skyclient.scc.mixins;

import co.skyclient.scc.hooks.GuiWinGameHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiWinGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiWinGame.class)
public class GuiWinGameMixin implements GuiWinGameHook {
    private boolean mainMenu = false;

    @Inject(method = "sendRespawnPacket", at = @At("HEAD"), cancellable = true)
    private void onPacketSend(CallbackInfo ci) {
        if (mainMenu) {
            ci.cancel();
            Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
        }
    }

    @Override
    public void setMainMenu() {
        mainMenu = true;
    }
}
