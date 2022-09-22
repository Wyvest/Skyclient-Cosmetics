package co.skyclient.scc.gui.greeting

import co.skyclient.scc.gui.greeting.components.GreetingSlide
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.ChatColor
import gg.essential.universal.UDesktop
import gg.essential.vigilance.utils.onLeftClick
import java.net.URI

class DiscordSlide : GreetingSlide<ImportSlide>(ImportSlide::class.java) {
    val text by UIWrappedText("""
        You can get support via our ${ChatColor.BOLD}Discord Server${ChatColor.RESET} by going to ${ChatColor.BLUE}${ChatColor.BOLD}https://inv.wtf/skyclient${ChatColor.RESET} or clicking this text.
    """.trimIndent(), centered = true) constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 75.percent()
        textScale = 2.pixels()
    } childOf window

    init {
        text.onLeftClick { UDesktop.browse(URI.create("https://inv.wtf/skyclient")) }
    }
}