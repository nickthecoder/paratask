package uk.co.nickthecoder.paratask.parameter

enum class ParameterEventType {
    VALUE, STRUCTURAL, INNER
}

data class ParameterEvent(
        val parameter: Parameter,
        val type: ParameterEventType,
        val innerParameter: Parameter? = null // Will be non-null for ParameterEventType.INNER
) {
}
