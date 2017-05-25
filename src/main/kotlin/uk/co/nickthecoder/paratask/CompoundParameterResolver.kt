package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.ValueParameter

class CompoundParameterResolver(vararg resolvers: ParameterResolver) : ParameterResolver {

    val children = mutableListOf<ParameterResolver>()

    init {
        children.addAll(resolvers)
    }

    override fun resolve(parameter: ValueParameter<*>) {
        children.forEach { it.resolve(parameter) }
    }
}
