package uk.co.nickthecoder.paratask.parameter

abstract class AbstractParameter(override val name: String) : Parameter {

	private val listeners = mutableListOf<ParameterListener>()

	override fun addListener(l: ParameterListener) {
		listeners.add(l)
	}

	override fun removeListener(l: ParameterListener) {
		listeners.remove(l)
	}

	protected fun fireChanged(parameter: Parameter = this) {
		listeners.forEach { it.parameterChanged(parameter) }
	}

	override fun toString(): String {
		return "Parameter ${name}"
	}

}
