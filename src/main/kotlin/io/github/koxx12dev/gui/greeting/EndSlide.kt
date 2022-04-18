package io.github.koxx12dev.gui.greeting

import io.github.koxx12dev.scc.utils.Files
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import net.minecraft.client.gui.GuiMainMenu
import java.awt.Color

class EndSlide : GreetingSlide<GuiMainMenu>(GuiMainMenu::class.java, {
    Files.greetingFile.createNewFile()
    Files.greetingFile.writeText(
        "This file is used to mark that you completed the setup slides in Skyclient.\n" +
        "Deleting this file will cause you to go through the setup process again, so don't."
    );
    Thread.sleep(1000)
}) {
    val title by UIText("Finished!") constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        textScale = 3.pixels()
        color = Color.GREEN.toConstraint()
    } childOf window
    override fun onScreenClose() {
        super.onScreenClose()
        if (previousScale != Int.MIN_VALUE) {
            mc.gameSettings.guiScale = previousScale
            mc.gameSettings.saveOptions()
        }
    }
}