package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.GroupedChoiceParameter


class GroupedChoiceExample : AbstractTask() {

    val abcP = GroupedChoiceParameter("abc", value = 'A')
            .also {
                it.group("")
                        .choice("1", '1', "Number 1")
                        .choice("2", '2', "Number 2")
                        .choice("3", '3', "Number 3")
            }
            .also {
                it.group("abc")
                        .choice("A", 'A', "Letter A")
                        .choice("B", 'B', "Letter B")
                        .choice("C", 'C', "Letter C")
            }
            .also {
                it.group("def")
                        .choice("D", 'D', "Letter D")
                        .choice("E", 'E', "Letter E")
                        .choice("F", 'F', "Letter F")
            }

    override val taskD = TaskDescription("groupedChoiceExample")
            .addParameters(abcP)

    override fun run() {
        println("Abc      : ${abcP.value}")
    }
}

fun main(args: Array<String>) {
    TaskParser(GroupedChoiceExample()).go(args, prompt = true)
}
