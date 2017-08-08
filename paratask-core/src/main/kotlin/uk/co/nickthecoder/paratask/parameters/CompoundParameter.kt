package uk.co.nickthecoder.paratask.parameters

import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.fields.GroupField
import uk.co.nickthecoder.paratask.util.escapeNL
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.paratask.util.unescapeNL

/**
 * Very similar to a GroupParameter, but is specially designed to be used as the children of MultipleParameter
 * During save/load instead of each parameter being saved individually, the values will be saved in one go,
 * with this parameter's stringValue being in the form :
 *   name1=value1
 *   name2=value2
 * etc
 *
 * The 'value' of this parameter is always 'this'.
 */
open class CompoundParameter(name: String,
                        override val label: String = name.uncamel(),
                        description: String = "")

    : AbstractGroupParameter(name = name, label = label, description = description), ValueParameter<CompoundParameter> {


    override val expressionProperty = SimpleStringProperty()

    override var expression: String?
        get() = expressionProperty.get()
        set(v) {
            expressionProperty.set(v)
        }

    override var value: CompoundParameter
        get() = this
        set(newValue) {
            for (child in children) {
                if (child is ValueParameter<*>) {
                    child.stringValue = (newValue.find(child.name) as ValueParameter<*>).stringValue
                }
            }
        }

    override fun errorMessage(v: CompoundParameter?): String? {
        return null
    }

    override val converter = object : StringConverter<CompoundParameter>() {

        override fun fromString(str: String): CompoundParameter? {
            val lines = str.split("\n")
            for (line in lines) {
                val eq = line.indexOf("=")
                if (eq > 0) {
                    val childName = line.substring(0, eq)
                    val stringValue = line.substring(eq + 1).unescapeNL()
                    val child = find(childName)
                    if (child is ValueParameter<*>) {
                        child.stringValue = stringValue
                    }
                }
            }

            return this@CompoundParameter
        }

        override fun toString(obj: CompoundParameter): String {
            return obj.descendants().filter { it is ValueParameter<*> }.map {
                if (it is ValueParameter<*>) {
                    val strValue = it.expression ?: it.stringValue ?: "?"
                    "${it.name}=${strValue.escapeNL()}"
                } else {
                    "" // Won't ever be used due to the filter above
                }
            }.joinToString(separator = "\n")

        }
    }

    override fun createField(): GroupField {
        val result = GroupField(this)
        result.buildContent()
        return result
    }


    override fun copy(): CompoundParameter {
        val result = CompoundParameter(name = name, label = label, description = description)
        copyChildren(result)
        return result
    }
}
