package io.github.koxx12dev.gui.greeting

import io.github.koxx12dev.scc.utils.Files
import net.minecraft.client.gui.GuiMainMenu

class EndSlide : GreetingSlide<GuiMainMenu>(GuiMainMenu::class.java, {
    Files.greetingFile.createNewFile()
    Files.greetingFile.writeText("DO NOT DELETE THIS PLEASE <3")
    Thread.sleep(1000)
}) {
    override fun onScreenClose() {
        super.onScreenClose()
        if (previousScale != Int.MIN_VALUE) {
            mc.gameSettings.guiScale = previousScale
            mc.gameSettings.saveOptions()
        }
    }
}