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

        fun inc() {
            resourceCounter++
            if (log) printLog( "inc ${resourceCounter}")
        }

        fun dec() {
            resourceCounter--
            if (log) printLog( "dec ${resourceCounter}")
            if (resourceCounter == 0) {
                Platform.exit()
            }
        }

        fun show(stage: Stage) {
            inc()
            stage.show()
            if (log) printLog( "Show ${resourceCounter}")
            stage.setOnHiding {
                dec()
                if (log) printLog( "Hide ${resourceCounter}")
            }
        }
        
        fun printLog( message : String) {
            println( message)
        }
    }
}
