package io.github.koxx12dev.gui.greeting

import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*

class DiscordSlide : GreetingSlide<ImportSlide>(ImportSlide::class.java) {
    val title by UIText("TODO") constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        textScale = 5.pixels()
    } childOf window
}