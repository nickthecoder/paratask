package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.ValueParameter

class CompoundParameterResolver(vararg resolvers: ParameterResolver) : ParameterResolver {

    private val children = mutableListOf<ParameterResolver>()

    init {
        children.addAll(resolvers)
    }

    fun add(resolver: ParameterResolver) {
        if (!children.contains(resolver)) {
            children.add(0, resolver)
        }
    }

    override fun resolve(parameter: ValueParameter<*>) {
        children.forEach {
            it.resolve(parameter)
        }
    }
}
