package uk.co.nickthecoder.paratask.util

import javafx.application.Platform
import javafx.stage.Stage

/**
 * My application may close the last remaining JavaFX stage, and then try to create another one.
 * This class ensures that JavaFX does not terminate when it shouldn't
 */
class AutoExit {
    companion object {

        val log = false

        init {
            Platform.setImplicitExit(false)
        }

        var resourceCounter = 0

        fun inc(message: String) {
            privateInc()
            if (log) printLog("inc $message $resourceCounter")
        }

        private fun privateInc() {
            resourceCounter++
        }

        fun dec(message: String) {
            privateDec()
            if (log) printLog("dec $message $resourceCounter")
        }

        private fun privateDec() {
            resourceCounter--
            if (resourceCounter == 0) {
                Platform.exit()
            }
        }

        fun show(stage: Stage) {
            privateInc()
            stage.show()
            if (log) printLog("Show $resourceCounter $stage")
            stage.setOnHiding {
                privateDec()
                if (log) printLog("Hide $resourceCounter $stage")
            }
        }

        fun printLog(message: String) {
            println(message)
        }
    }
}
