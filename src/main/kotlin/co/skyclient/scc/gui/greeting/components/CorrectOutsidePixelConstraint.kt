package co.skyclient.scc.gui.greeting.components

import cc.polyfrost.oneconfig.libs.elementa.UIComponent
import cc.polyfrost.oneconfig.libs.elementa.constraints.ConstraintType
import cc.polyfrost.oneconfig.libs.elementa.constraints.XConstraint
import cc.polyfrost.oneconfig.libs.elementa.constraints.resolution.ConstraintVisitor

class CorrectOutsidePixelConstraint(
    private val value: Float
) : XConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent) = value - component.getWidth()

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {

    }

}