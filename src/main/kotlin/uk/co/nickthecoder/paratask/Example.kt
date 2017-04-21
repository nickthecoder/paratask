package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter

class Example : Task("Example") {

	val oneToTen = IntParameter("oneToTen", range = 1..10)
	val fromOne = IntParameter("fromOne", range = 1..Int.MAX_VALUE, required = false)
	val greeting = StringParameter("Greeting")

	init {
		addParameters(oneToTen, fromOne, greeting)
	}

	override fun body() {
		println("Example Parameter values : ")
		// TODO Iteratate over all parameters, including those in a Group (when that feature has been implemented)
		root.forEach {
			if (it is ValueParameter<*>) {
				println("Parameter ${it.name} = ${it.getStringValue()} (${it.value})")
			}
		}
	}
}

fun main(args: Array<String>) {
	CommandTask(Example()).go(args)
}