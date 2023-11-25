/**
 * Taken from Vigilance under LGPL 3.0
 * https://github.com/EssentialGG/Vigilance/blob/master/LICENSE
 */

package co.skyclient.scc.gui.greeting.components

import cc.polyfrost.oneconfig.libs.elementa.UIComponent
import cc.polyfrost.oneconfig.libs.elementa.components.*
import cc.polyfrost.oneconfig.libs.elementa.constraints.CenterConstraint
import cc.polyfrost.oneconfig.libs.elementa.constraints.ChildBasedSizeConstraint
import cc.polyfrost.oneconfig.libs.elementa.constraints.animation.Animations
import cc.polyfrost.oneconfig.libs.elementa.dsl.*
import cc.polyfrost.oneconfig.libs.elementa.effects.Effect
import cc.polyfrost.oneconfig.libs.elementa.effects.ScissorEffect
import cc.polyfrost.oneconfig.libs.elementa.events.UIClickEvent
import cc.polyfrost.oneconfig.libs.elementa.state.BasicState
import cc.polyfrost.oneconfig.libs.elementa.state.State
import cc.polyfrost.oneconfig.libs.elementa.state.toConstraint
import cc.polyfrost.oneconfig.libs.elementa.utils.withAlpha
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.USound
import java.awt.Color
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

inline fun UIComponent.onLeftClick(crossinline method: UIComponent.(event: UIClickEvent) -> Unit) = onMouseClick {
    if (it.mouseButton == 0) {
        this.method(it)
    }
}

class ExpandingClickEffect @JvmOverloads constructor(
    private val color: Color,
    private val animationTime: Float = 0.2f,
    private val scissorBoundingBox: UIComponent? = null
) : Effect() {

    private var state = State.NotActive
    private var targetRadius = -1f
    private var radiusDelta = -1f
    private lateinit var scissorEffect: ScissorEffect
    private lateinit var lastClick: UIClickEvent

    private var circle = UICircle(color = color)

    override fun setup() {
        scissorEffect = ScissorEffect(scissorBoundingBox ?: boundComponent)
        circle effect scissorEffect
        Window.enqueueRenderOperation {
            circle childOf Window.of(boundComponent)
            circle.hide(true)
        }

        fun onClickHandler(clickEvent: UIClickEvent) {
            lastClick = clickEvent
            circle.unhide()
            state = State.Expanding

            val left = boundComponent.getLeft()
            val top = boundComponent.getTop()
            val right = boundComponent.getRight()
            val bottom = boundComponent.getBottom()

            val center = clickEvent.absoluteX to clickEvent.absoluteY
            targetRadius = max(
                max(
                    distance(center, left to top), distance(center, left to bottom)
                ), max(
                    distance(center, right to top), distance(center, right to bottom)
                )
            ) + 2f
            radiusDelta = targetRadius / animationTime / Window.of(boundComponent).animationFPS

            circle.constrain {
                x = clickEvent.absoluteX.pixels
                y = clickEvent.absoluteY.pixels
                color = this@ExpandingClickEffect.color.toConstraint()
                radius = 0.pixels
            }
        }

        boundComponent.onLeftClick { onClickHandler(it) }

        circle.onLeftClick {
            onClickHandler(it)
        }
    }

    override fun animationFrame() {
        when (state) {
            State.NotActive -> {}
            State.Expanding -> {
                val newRadius = circle.getRadius() + radiusDelta
                if (newRadius >= targetRadius) {
                    state = State.Expanded
                } else {
                    circle.setRadius(newRadius.pixels)
                }
            }

            State.Expanded -> {
                val currentColor = circle.getColor()
                val alpha = currentColor.alpha - 2
                if (alpha <= 0) {
                    state = State.NotActive
                    targetRadius = -1f
                    radiusDelta = -1f
                    circle.hide(true)
                } else {
                    circle.setColor(currentColor.withAlpha(alpha).toConstraint())
                }
            }
        }
    }

    override fun beforeDraw(matrixStack: UMatrixStack) {
        if (state != State.NotActive) circle.draw(matrixStack)
    }

    enum class State {
        NotActive, Expanding, Expanded,
    }

    companion object {
        private fun distance(p1: Pair<Float, Float>, p2: Pair<Float, Float>) =
            sqrt((p1.first - p2.first).pow(2f) + (p1.second - p2.second).pow(2f))
    }
}

abstract class SettingComponent : UIContainer() {

    private var onValueChange: (Any?) -> Unit = {}
    private var lastValue: Any? = null

    init {
        constrain {
            x = (13f + 10f).pixels(alignOpposite = true)
            y = CenterConstraint()
        }
    }

    fun onValueChange(listener: (Any?) -> Unit) {
        this.onValueChange = listener
    }

    fun changeValue(newValue: Any?, callListener: Boolean = true) {
        if (newValue != lastValue) {
            lastValue = newValue
            this.onValueChange(newValue)
        }
    }

    open fun closePopups(instantly: Boolean = false) {}

    open fun setupParentListeners(parent: UIComponent) {}

    companion object {
        const val DOWN_ARROW_PNG = "/vigilance/arrow-down.png"
        const val UP_ARROW_PNG = "/vigilance/arrow-up.png"
    }
}

class ButtonComponent(placeholder: String? = null, private val callback: () -> Unit) : SettingComponent() {

    private var textState: State<String> = BasicState(placeholder.orEmpty().ifEmpty { "Activate" })
    private var listener: () -> Unit = textState.onSetValue {
        text.setText(textState.get())
    }

    private val container by UIBlock(VigilancePalette.button).constrain {
        width = ChildBasedSizeConstraint() + 14.pixels
        height = ChildBasedSizeConstraint() + 8.pixels
    } childOf this

    private val text by UIWrappedText(
        textState.get(),
        trimText = true,
        shadowColor = VigilancePalette.getTextShadow()
    ).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = width.coerceAtMost(300.pixels)
        height = 10.pixels
        color = VigilancePalette.text.toConstraint()
    } childOf container

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        enableEffect(ExpandingClickEffect(VigilancePalette.getPrimary().withAlpha(0.5f), scissorBoundingBox = container))

        container.onMouseEnter {
            container.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, VigilancePalette.buttonHighlight.toConstraint())
            }
        }.onMouseLeave {
            container.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, VigilancePalette.button.toConstraint())
            }
        }.onLeftClick {
            USound.playButtonPress()
            callback()
        }
    }

    fun bindText(newTextState: State<String>) = apply {
        listener()
        textState = newTextState
        text.bindText(textState)

        listener =  textState.onSetValue {
            text.setText(textState.get())
        }
    }

    fun getText() = textState.get()
    fun setText(text: String) = apply { textState.set(text) }
}

object VigilancePalette {
    fun getPrimary(): Color = primary.get()
    fun getBackground(): Color = backgroundState.get()
    fun getButton(): Color = button.get()
    fun getButtonHighlight(): Color = buttonHighlight.get()
    fun getText(): Color = text.get()
    fun getTextHighlight(): Color = textHighlight.get()
    fun getTextShadow(): Color = textShadow.get()
    fun getTextDisabled(): Color = textDisabled.get()
    fun getComponentBackground(): Color = componentBackground.get()

    // These are marked as internal because ideally the user is only changing the colours in the settings gui
    // Old
    internal val brightDividerState = BasicState(Color.BLACK)
    internal val dividerState = BasicState(Color.BLACK)
    internal val darkDividerState = BasicState(Color.BLACK)
    internal val outlineState = BasicState(Color.BLACK)
    internal val scrollBarState = BasicState(Color.BLACK)
    internal val brightHighlightState = BasicState(Color.BLACK)
    internal val highlightState = BasicState(Color.BLACK)
    internal val darkHighlightState = BasicState(Color.BLACK)
    internal val lightBackgroundState = BasicState(Color.BLACK)
    internal val backgroundState = BasicState(Color.BLACK)
    internal val darkBackgroundState = BasicState(Color.BLACK)
    internal val searchBarBackgroundState = BasicState(Color.BLACK)
    internal val brightTextState = BasicState(Color.BLACK)
    internal val midTextState = BasicState(Color.BLACK)
    internal val darkTextState = BasicState(Color.BLACK)
    internal val modalBackgroundState = BasicState(Color.BLACK)
    internal val warningState = BasicState(Color.BLACK)
    internal val accentState = BasicState(Color.BLACK)
    internal val successState = BasicState(Color.BLACK)
    internal val transparentState = BasicState(Color.BLACK)
    internal val disabledState = BasicState(Color.BLACK)
    internal val bgNoAlpha = BasicState(Color.BLACK)

    internal val primary = BasicState(Color.BLACK)
    internal val button = BasicState(Color.BLACK)
    internal val buttonHighlight = BasicState(Color.BLACK)
    internal val text = BasicState(Color.BLACK)
    internal val textHighlight = BasicState(Color.WHITE)
    internal val textShadow = BasicState(Color.BLACK)
    internal val textDisabled = BasicState(Color.BLACK)
    internal val componentBackground = BasicState(Color.BLACK)

    // Old
    private var brightDivider = Color(151, 151, 151)
    private var divider = Color(80, 80, 80)
    private var darkDivider = Color(50, 50, 50)
    private var outline = Color(48, 48, 49)
    private var scrollBar = Color(45, 45, 45)
    private var brightHighlight = Color(50, 50, 50)
    private var highlight = Color(33, 34, 38)
    private var darkHighlight = Color(27, 28, 33)
    private var lightBackground = Color(32, 32, 33)
    private var background = Color(22, 22, 24)
    private var darkBackground = Color(10, 10, 11)
    private var searchBarBackground = Color(27, 28, 33)
    private var brightText = Color(255, 255, 255)
    private var midText = Color(187, 187, 187)
    private var darkText = Color(119, 119, 121)
    private var modalBackground = Color(0, 0, 0, 100)
    private var warning = Color(239, 83, 80)
    private var accent = Color(1, 165, 82)
    private var success = Color(1, 165, 82)
    private var transparent = Color(0, 0, 0, 0)
    private var disabled = Color(80, 80, 80)

    private var primaryA = Color(0x2997FF)
    private var buttonA = Color(0x323232)
    private var buttonHighlightA = Color(0x474747)
    private var textA = Color(0xBBBBBB)
    private var textHighlightA = Color(0xFFFFFF)
    private var textShadowA = Color(0x161618)
    private var textDisabledA = Color(0x6A6A6A)
    private var componentBackgroundA = Color(0x232323)

    init {
        setAllInPalette()
    }

    internal fun setAllInPalette() {
        brightDividerState.set(brightDivider)
        dividerState.set(divider)
        darkDividerState.set(darkDivider)
        outlineState.set(outline)
        scrollBarState.set(scrollBar)
        brightHighlightState.set(brightHighlight)
        highlightState.set(highlight)
        darkHighlightState.set(darkHighlight)
        lightBackgroundState.set(lightBackground)
        backgroundState.set(background)
        darkBackgroundState.set(darkBackground)
        searchBarBackgroundState.set(searchBarBackground)
        brightTextState.set(brightText)
        midTextState.set(midText)
        darkTextState.set(darkText)
        modalBackgroundState.set(modalBackground)
        warningState.set(warning)
        accentState.set(accent)
        successState.set(success)
        transparentState.set(transparent)
        disabledState.set(disabled)
        bgNoAlpha.set(background.withAlpha(0))

        primary.set(primaryA)
        button.set(buttonA)
        buttonHighlight.set(buttonHighlightA)
        text.set(textA)
        textHighlight.set(textHighlightA)
        textShadow.set(textShadowA)
        textDisabled.set(textDisabledA)
        componentBackground.set(componentBackgroundA)
    }

    /* Utilities for colors */
    internal fun getTextColor(hovered: State<Boolean>, enabled: State<Boolean>): State<Color> {
        return hovered.zip(enabled).map { (hovered, enabled) ->
            if (enabled) {
                if (hovered) {
                    getTextHighlight()
                } else {
                    getText()
                }
            } else {
                getTextDisabled()
            }
        }
    }

    internal fun getTextColor(hovered: State<Boolean>): State<Color> = getTextColor(hovered, BasicState(true))

    internal fun getButtonColor(hovered: State<Boolean>, enabled: State<Boolean>): State<Color> {
        return hovered.zip(enabled).map { (hovered, enabled) ->
            if (enabled) {
                if (hovered) {
                    getButtonHighlight()
                } else {
                    getButton()
                }
            } else {
                getComponentBackground()
            }
        }
    }
}