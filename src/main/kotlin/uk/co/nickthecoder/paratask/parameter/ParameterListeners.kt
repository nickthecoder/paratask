package uk.co.nickthecoder.paratask.parameter

class ParameterListeners {

    private val listeners = mutableListOf<ParameterListener>()

    fun add(listener: ParameterListener) {
        listeners.add(listener)
    }

    fun remove(listener: ParameterListener) {
        listeners.remove(listener)
    }

    fun fireChanged(parameter: Parameter) {
        listeners.forEach {
            it.parameterChanged(parameter)
        }
    }

    fun count(): Int = listeners.size
}