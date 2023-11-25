package co.skyclient.scc.gui.greeting

import cc.polyfrost.oneconfig.libs.elementa.components.UIText
import cc.polyfrost.oneconfig.libs.elementa.constraints.CenterConstraint
import cc.polyfrost.oneconfig.libs.elementa.constraints.SiblingConstraint
import cc.polyfrost.oneconfig.libs.elementa.dsl.*
import cc.polyfrost.oneconfig.libs.universal.UGraphics
import co.skyclient.scc.gui.greeting.components.GreetingSlide
import java.awt.Color

class IntroductionGreetingSlide : GreetingSlide<DiscordSlide>(DiscordSlide::class.java) {
    val title by UIText("SkyClient") constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        textScale = 5.pixels()
        color = Color.CYAN.toConstraint()
    } childOf window

    val subtitle by UIText("Welcome to...") constrain {
        x = CenterConstraint()
        y = SiblingConstraint(2f, alignOpposite = true)
    } childOf window

    override fun initScreen(width: Int, height: Int) {
        @Suppress("DEPRECATION") // we need to run this to force reenable opengl stuff (also this wouldn't run in 1.17+)
        UGraphics.disableTexture2D()
        UGraphics.enableBlend()
        UGraphics.disableAlpha()
        UGraphics.shadeModel(7425)
        UGraphics.shadeModel(7424)
        UGraphics.disableBlend()
        UGraphics.enableAlpha()
        @Suppress("DEPRECATION")
        UGraphics.enableTexture2D()
        super.initScreen(width, height)
    }
}