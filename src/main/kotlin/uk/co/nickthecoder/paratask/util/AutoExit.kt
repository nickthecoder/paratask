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
            if (log) println( "inc ${resourceCounter}")
        }

        fun dec() {
            resourceCounter--
            if (log) println( "dec ${resourceCounter}")
            if (resourceCounter == 0) {
                Platform.exit()
            }
        }

        fun show(stage: Stage) {
            inc()
            stage.show()
            if (log) println( "Show ${resourceCounter}")
            stage.setOnHiding {
                dec()
                if (log) println( "Hide ${resourceCounter}")
            }
        }
    }
}
