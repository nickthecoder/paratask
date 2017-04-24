package uk.co.nickthecoder.paratask

import javafx.application.Application

/**
 * Use to process command line arguments, and then either run the Task directly, or start up the
 * GUI to prompt the parameters.
 */
class CommandTask(val task: Task) {
    fun go(args: Array<String>) {

        // TODO LATER Parse command line arguments, and decide if we need to prompt
        println("Creating ParaTaskAppliction. Number of args : ${args.size}")

        ParaTaskApp.task = task
        Application.launch(ParaTaskApp::class.java);
    }
}