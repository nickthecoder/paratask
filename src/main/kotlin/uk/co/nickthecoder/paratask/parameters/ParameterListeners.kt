package uk.co.nickthecoder.paratask.parameters

class ParameterListeners {

    private val listeners = mutableListOf<ParameterListener>()

    val size: Int
        get() = listeners.size

    fun add(listener: ParameterListener) {
        listeners.add(listener)
    }

    fun remove(listener: ParameterListener) {
        listeners.remove(listener)
    }

    // LATER. Many ParameterEvent will be created, so it may be worth while creating a pool of reusable
    // ParameterEvents, rather than creating a new one every time.

    fun fireValueChanged(parameter: Parameter) {
        val event = ParameterEvent(parameter, ParameterEventType.VALUE)
        listeners.forEach {
            it.parameterChanged(event)
        }
    }

    fun fireStructureChanged(parameter: Parameter) {
        val event = ParameterEvent(parameter, ParameterEventType.STRUCTURAL)
        listeners.forEach {
            it.parameterChanged(event)
        }
    }

    fun fireInnerParameterChanged(parameter: Parameter, innerParameter: Parameter) {
        val event = ParameterEvent(parameter, ParameterEventType.INNER, innerParameter)
        listeners.forEach {
            it.parameterChanged(event)
        }
    }

    fun count(): Int = listeners.size
}