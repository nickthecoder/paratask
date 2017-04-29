package uk.co.nickthecoder.paratask.parameter

class ValueListeners {

    private val listeners = mutableListOf<ValueListener>()

    fun add(listener: ValueListener) {
        listeners.add(listener)
    }

    fun remove(listener: ValueListener) {
        listeners.remove(listener)
    }

    fun fireChanged(value: ParameterValue<*>) {
        listeners.forEach {
            it.valueChanged(value)
        }
    }

    fun count(): Int = listeners.size
}