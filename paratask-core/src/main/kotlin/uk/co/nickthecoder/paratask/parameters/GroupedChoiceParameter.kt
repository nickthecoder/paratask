package uk.co.nickthecoder.paratask.parameters

import uk.co.nickthecoder.paratask.parameters.fields.GroupedChoiceField
import uk.co.nickthecoder.paratask.util.uncamel

class GroupedChoiceParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: T? = null,
        val allowSingleItemSubMenus: Boolean = false,
        required: Boolean = true)

    : ChoiceParameter<T>(name, label, description, value, required) {

    private val groups = mutableMapOf<String, Group>()

    fun groups(): Map<String, Group> = groups

    override fun clear() {
        groups.clear()
        super.clear()
    }

    override fun addChoice(key: String, value: T, label: String): GroupedChoiceParameter<T> {
        group("").choice(key, value, label)
        return this
    }

    fun group(label: String): Group {
        var group = groups[label]
        if (group == null) {
            group = Group(label)
            groups.put(label, group)
        }
        return group
    }

    override fun createField(): GroupedChoiceField<T> {
        val result = GroupedChoiceField<T>(this)
        result.build()
        return result
    }

    override fun copy(): GroupedChoiceParameter<T> {
        val copy = GroupedChoiceParameter<T>(name, label, description, value, required)
        groups.values.forEach { group ->
            val groupCopy = copy.group(group.label)
            group.groupChoices.forEach { choice ->
                groupCopy.choice(choice.key, choice.value, choice.label)
            }
        }
        return copy
    }

    override fun toString(): String = "Grouped" + super.toString()


    inner class Group(val label: String) {
        val groupChoices = mutableListOf<Choice<T>>()

        fun choice(key: String, value: T, label: String = key.uncamel()): Group {
            val choice = Choice(key, value, label)
            groupChoices.add(choice)
            choices.add(choice)
            return this
        }
    }

}
