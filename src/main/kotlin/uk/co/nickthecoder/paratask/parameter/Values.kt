package uk.co.nickthecoder.paratask.parameter

class Values(override val parameter: GroupParameter) : Value<Unit>, ValueListener {

    override val valueListeners = ValueListeners()

    override var value
        get() = Unit
        set(u: Unit) {}

    override var stringValue
        get() = ""
        set(U: String) {}

    val values = mutableMapOf<String, Value<*>>()

    fun get(name: String) = values.get(name)

    fun put(name: String, value: Value<*>) {
        values.put(name, value)
        value.valueListeners.add(this)
    }

    override fun errorMessage(): String? {
        return parameter.errorMessage(this)
    }

    override fun valueChanged(value: Value<*>) {
        valueListeners.fireChanged(value)
    }
}