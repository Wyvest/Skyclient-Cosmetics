package co.skyclient.scc.gui.greeting

import co.skyclient.scc.gui.greeting.components.GreetingSlide
import cc.polyfrost.oneconfig.libs.elementa.components.UIWrappedText
import cc.polyfrost.oneconfig.libs.elementa.constraints.CenterConstraint
import cc.polyfrost.oneconfig.libs.elementa.dsl.*
import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UDesktop
import co.skyclient.scc.gui.greeting.components.onLeftClick
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