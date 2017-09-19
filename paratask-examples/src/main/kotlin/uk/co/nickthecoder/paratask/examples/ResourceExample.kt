package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.compound.ResourceParameter
import uk.co.nickthecoder.paratask.util.Resource
import java.io.File

class ResourceExample : AbstractTask() {


    val resourceP = ResourceParameter("resource", value = Resource(File("")))
    var resource by resourceP

    override val taskD = TaskDescription("resourceExample")
            .addParameters(resourceP)


    override fun run() {
        println("Resource = $resource")
    }

}

fun main(args: Array<String>) {
    TaskParser(ResourceExample()).go(args, prompt = true)
}
