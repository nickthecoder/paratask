package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*

class MultipleExample : AbstractTask() {

    val info1P = InformationParameter("info1", information = "The default is to allow zero items, so you don't HAVE to enter any strings.")

    val stringsP = MultipleParameter("strings") {
        StringParameter(name = "string") // Note. The name doesn't matter! It isn't ever used!
    }
    var strings by stringsP

    val info2P = InformationParameter("info2", information = "Note that there must be at least 2 integers entered, but you can add more (with the " + " buttons.")

    val intsP = MultipleParameter("integers", minItems = 2, isBoxed = true) {
        IntParameter(name = "int")
    }
    var ints by intsP

    val complexNumbersP = MultipleParameter("complexNumbers", isBoxed = true) {
        ImaginaryNumberParameters()
    }


    override val taskD = TaskDescription("multipleExample")
            .addParameters(info1P, stringsP, info2P, intsP, complexNumbersP)


    override fun run() {
        println("Strings = $strings")

        ints.forEach { println("Int = $it") }

        complexNumbersP.innerParameters.forEach { imaginaryNumberParameters ->
            println("ComplexNumber ${imaginaryNumberParameters.realP.value} + ${imaginaryNumberParameters.imaginaryP.value}i")
        }
    }

    class ImaginaryNumberParameters : MultipleGroupParameter("complexNumber", "Complex Number") {
        val realP = DoubleParameter("real") // This name IS used! See the run method
        val plusP = InformationParameter("plus", information = "+")
        val imaginaryP = DoubleParameter("imaginary") // This name IS used! See the run method
        val iP = InformationParameter("i", information = "i")

        init {
            addParameters(realP, plusP, imaginaryP, iP)
            asHorizontal(LabelPosition.NONE)
        }
    }
}

fun main(args: Array<String>) {
    TaskParser(MultipleExample()).go(args, prompt = true)
}
