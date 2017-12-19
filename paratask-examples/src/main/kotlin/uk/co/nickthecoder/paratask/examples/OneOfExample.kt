package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*


class OneOfExample : AbstractTask() {

    val doubleP = DoubleParameter("double", value = 0.0)

    val stringP = StringParameter("string")

    val choiceP = ChoiceParameter<Int>("123")
            .choice("1", 1, "Number 1")
            .choice("2", 2, "Number 2")
            .choice("3", 3, "Number 3")

    val groupChoiceP = GroupedChoiceParameter("groupedChoice", value = 'A')
            .also {
                it.group("ABC")
                        .choice("A", 'A', "Letter A")
                        .choice("B", 'B', "Letter B")
                        .choice("C", 'C', "Letter C")
            }
            .also {
                it.group("DEF")
                        .choice("D", 'D', "Letter D")
                        .choice("E", 'E', "Letter E")
                        .choice("F", 'F', "Letter F")
            }

    val oneOfP = OneOfParameter("oneOf", value = doubleP, choiceLabel = "Choose")
            .addChoices(doubleP, stringP, choiceP, groupChoiceP)

    override val taskD = TaskDescription("oneOfExample")
            .addParameters(oneOfP, doubleP, stringP, choiceP, groupChoiceP)

    override fun run() {
        when (oneOfP.value) {
            doubleP -> {
                println("Double = ${doubleP.value}")
            }
            stringP -> {
                println("String = ${stringP.value}")
            }
            choiceP -> {
                println("Choice = ${choiceP.value}")
            }
            groupChoiceP -> {
                println("Group Choice = ${groupChoiceP.value}")
            }
        }

    }
}

fun main(args: Array<String>) {
    TaskParser(OneOfExample()).go(args, prompt = true)
}
