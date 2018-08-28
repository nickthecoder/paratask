package uk.co.nickthecoder.paratask.parameters

import uk.co.nickthecoder.paratask.parameters.fields.LabelField

/**
 * Similar to an [InformationParameter], but does not use the .information sytle class (and is therefore not green).
 * Also, [InformationParameter]s are stretchy, but LabelParameter's aren't.
 * It is rendered using a single Label.
 * If we compare a LabelParameter to a StringParameter, then the LabelParameter has a Label in place of the TextField.
 * Note, that a StringParameter also has a Label (usually to the left), but a LabelParameter has nothing in that
 * position. i.e. hasLabel = false.
 */
class LabelParameter(
        name: String,
        label: String = "")

    : AbstractParameter(name, label = label, description = "") {

    override fun errorMessage(): String? = null

    override fun isStretchy(): Boolean = false

    override fun createField() = LabelField(this).build()

    override fun copy(): LabelParameter {
        return LabelParameter(name = name, label = label)
    }
}
