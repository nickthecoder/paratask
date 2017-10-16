package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*

/**
 * Adding and removing a parameter from a SimpleGroupParameter.
 * I created this class to help find a bug.
 */
class AddRemoveExample : AbstractTask() {

    val intP = IntParameter("normalInt", label = "Int")
    val stringP = StringParameter("normalString", label = "String", columns = 10)
    val infoP = InformationParameter("info", information = "Hello world")
    val groupP = SimpleGroupParameter("normalGroup")
            .addParameters(intP, stringP)

    val removeIntP = ButtonParameter("removeInt", buttonText = "Remove Int") {
        groupP.remove(intP)
    }

    val addIntP = ButtonParameter("addInt", buttonText = "Add Int") {
        groupP.add(intP)
    }

    val addInfoP = ButtonParameter("addInfo", buttonText = "Add Info") {
        groupP.add(infoP)
    }
    val removeInfoP = ButtonParameter("removeInfo", buttonText = "Remove Info") {
        groupP.remove(infoP)
    }

    override val taskD = TaskDescription("addRemoveExample")
            .addParameters(groupP, removeIntP, addIntP, addInfoP, removeInfoP)

    override fun run() {
    }

}

fun main(args: Array<String>) {
    TaskParser(AddRemoveExample()).go(args, prompt = true)
}
