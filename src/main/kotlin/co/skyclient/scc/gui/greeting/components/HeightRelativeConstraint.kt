package co.skyclient.scc.gui.greeting.components

import cc.polyfrost.oneconfig.libs.elementa.UIComponent
import cc.polyfrost.oneconfig.libs.elementa.constraints.ConstraintType
import cc.polyfrost.oneconfig.libs.elementa.constraints.HeightConstraint
import cc.polyfrost.oneconfig.libs.elementa.constraints.WidthConstraint
import cc.polyfrost.oneconfig.libs.elementa.constraints.resolution.ConstraintVisitor
import cc.polyfrost.oneconfig.libs.elementa.state.BasicState
import cc.polyfrost.oneconfig.libs.elementa.state.State

class HeightRelativeConstraint(value: Float) : WidthConstraint, HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private var valueState: State<Float> = BasicState(value)

    var value: Float
        get() = valueState.get()
        set(value) { valueState.set(value) }

    override fun getWidthImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getHeight() * valueState.get()
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getHeight() * valueState.get()
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {

    }
}