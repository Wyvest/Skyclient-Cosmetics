package co.skyclient.scc.gui.greeting

import cc.polyfrost.oneconfig.libs.elementa.components.UIText
import cc.polyfrost.oneconfig.libs.elementa.constraints.CenterConstraint
import cc.polyfrost.oneconfig.libs.elementa.constraints.SiblingConstraint
import cc.polyfrost.oneconfig.libs.elementa.dsl.*
import co.skyclient.scc.gui.greeting.components.GreetingSlide
import co.skyclient.scc.utils.Files
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class EndSlide : GreetingSlide<GuiScreen>(null, {
    Files.greetingFile.createNewFile()
    Files.greetingFile.writeText("DELETING OR EDITING THIS FILE WILL CAUSE WEIRD THINGS TO HAPPEN! DO NOT TOUCH THIS UNLESS A SKYCLIENT STAFF MEMBER HAS GIVEN YOU PERMISSION TO DO SO!\n2")
    Thread.sleep(1000)
}) {
    val title by UIText("That's it!") constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        textScale = 5.pixels()
        color = Color.GREEN.darker().toConstraint()
    } childOf window

    val subtitle by UIText("Have fun using SkyClient!") constrain {
        x = CenterConstraint()
        y = SiblingConstraint(2f)
    } childOf window

    override fun onScreenClose() {
        super.onScreenClose()
        if (previousScale != Int.MIN_VALUE) {
            mc.gameSettings.guiScale = previousScale
            mc.gameSettings.saveOptions()
        }
    }
}