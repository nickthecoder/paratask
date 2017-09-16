package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.IntParameter

class IntExample : AbstractTask() {

    override val taskD = TaskDescription("intExample")

    val requiredP = IntParameter("required")
    var required by requiredP

    val optionalP = IntParameter("optional", required = false)
    val optional by optionalP

    val oneToTenP = IntParameter("oneToTen", minValue = 1, maxValue = 10)

    val sliderP = IntParameter("slider", minValue = 1, maxValue = 10)
            .asSlider()

    val slider2P = IntParameter("slider2", minValue = 1, maxValue = 10)
            .asSlider(IntParameter.SliderInfo(showValue = true))


    init {
        taskD.addParameters(requiredP, optionalP, oneToTenP, sliderP, slider2P)
    }

    override fun run() {
        println("Required=$required, Optional=$optional")
        println("OneToTen=${oneToTenP.value}")
        println("Slider=${sliderP.value}")
        println("Slider2=${slider2P.value}")
    }

}

fun main(args: Array<String>) {
    TaskParser(IntExample()).go(args, prompt = true)
}
