package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter


class ChoiceExample : AbstractTask() {

    val abcP = ChoiceParameter("abc", value = 'A')
            .addChoice("A", 'A', "Letter A")
            .addChoice("B", 'B', "Letter B")
            .addChoice("C", 'C', "Letter C")

    val nullableP = ChoiceParameter<Char?>("nullable", required = false, value = null)
            .addChoice("", null, "<none>")
            .addChoice("A", 'A', "Letter A")
            .addChoice("B", 'B', "Letter B")
            .addChoice("C", 'C', "Letter C")

    override val taskD = TaskDescription("choiceExample")
            .addParameters(abcP, nullableP)

    override fun run() {
        println("Abc      : ${abcP.value}")
        println("Nullable : ${nullableP.value}")
    }
}

fun main(args: Array<String>) {
    TaskParser(ChoiceExample()).go(args, prompt = true)
}
