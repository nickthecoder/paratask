package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*

class ListDetailExample : AbstractTask() {

    val stringsP = MultipleParameter("strings", isBoxed = true) {
        StringParameter(name = "string") // Note. The name doesn't matter! It isn't ever used!
    }.asListDetail(width = 200, height = 100) {
        it.value
    }

    val complexNumbersP = MultipleParameter("complexNumbers", isBoxed = true) {
        ImaginaryNumberParameters()
    }.asListDetail(width = 200, height = 100) {
        "${it.realP.value} + ${it.imaginaryP.value}i"
    }

    // Note width is used to ensure the window is wide enough.
    override val taskD = TaskDescription("multipleExample", width=650)
            .addParameters(stringsP, complexNumbersP)


    override fun run() {
        complexNumbersP.innerParameters.forEach { imaginaryNumberParameters ->
            println("ComplexNumber ${imaginaryNumberParameters.realP.value} + ${imaginaryNumberParameters.imaginaryP.value}i")
        }
    }

    class ImaginaryNumberParameters : MultipleGroupParameter("complexNumber", "Complex Number") {
        val realP = DoubleParameter("real") // This name IS used! See the run method
        val imaginaryP = DoubleParameter("imaginary") // This name IS used! See the run method

        init {
            addParameters(realP, imaginaryP)
        }
    }
}

fun main(args: Array<String>) {
    TaskParser(ListDetailExample()).go(args, prompt = true)
}
