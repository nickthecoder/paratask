/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.project.Project
import uk.co.nickthecoder.paratask.project.ProjectWindow
import uk.co.nickthecoder.paratask.project.TaskPrompter
import java.io.File

/**
 * JavaFX really sucks for this type of application, where there are many differeny program entry points, and
 * especially when the application only *sometimes* needs to start JavaFX.
 * So this is NOT an entry point for the application, just an annoyance that I have to put up with!
 *
 * To make matters worse, I may have a Task, which is NOT prompted (i.e. run directly from the command line), and
 * part of it's run, it needs to pop up a window (such as prompting ANOTHER Task). In this case, it is this second
 * Task which must start JavaFX, and therefore ALL windows have to go through this Application class, just in case,
 * they are the first JavaFX window to be opened. Grr.
 *
 * This also maeans that Tasks CANNOT use the standard dialog boxes (such as Alert) unaided, because it is impossible
 * to create a JavaFX object before JavaFX has been initialised, and that is only possible by calling
 * Application.launch.
 *
 * You may guess that I'm really pissed off about the crappy design of this part of JavaFX!!!
 */
class ParaTaskApp : Application() {

    override fun start(stage: Stage) {
        initialTask?.let { task ->
            startPromptTask(task, stage)
            initialTask = null
        }

        initialTool?.let { tool ->
            startOpenTool(tool, initialRun, stage)
            initialTask = null
        }

        initialProjectFiles?.let { files ->
            startOpenProjects(files)
            initialProjectFiles = null
        }

        initialFunction?.let { it() }
    }

    companion object {

        private var started = false

        private var initialTask: Task? = null

        private var initialTool: Tool? = null

        private var initialRun: Boolean = false

        private var initialProjectFiles: List<File>? = null

        private var initialFunction: (() -> Unit)? = null

        fun logAttach(@Suppress("UNUSED_PARAMETER") string: String) {
            // println( string )
        }

        fun startPromptTask(task: Task) {
            if (started) {
                if (task is Tool) {
                    startOpenTool(task, false, Stage())
                } else {
                    startPromptTask(task, Stage())
                }
            } else {
                started = true
                this.initialTask = task
                Application.launch(ParaTaskApp::class.java)
            }
        }

        fun startOpenTool(tool: Tool, run: Boolean) {
            if (started) {
                startOpenTool(tool, run = true, stage = Stage())
            } else {
                started = true
                this.initialRun = run
                this.initialTool = tool
                Application.launch(ParaTaskApp::class.java)
            }
        }

        fun openProjects(projectFiles: List<File>) {
            if (started) {
                openProjects(projectFiles)
            } else {
                started = true
                this.initialProjectFiles = projectFiles
                Application.launch(ParaTaskApp::class.java)
            }
        }

        fun runFunction(func: () -> Unit) {
            if (started) {
                func()
            } else {
                started = true
                this.initialFunction = func
                Application.launch(ParaTaskApp::class.java)
            }
        }


        private fun startOpenTool(tool: Tool, run: Boolean, stage: Stage) {
            val projectWindow = ProjectWindow()
            projectWindow.placeOnStage(stage)
            projectWindow.addTool(tool)
            if (run) {
                tool.taskRunner.run()
            }
        }

        private fun startPromptTask(task: Task, stage: Stage) {
            TaskPrompter(task).placeOnStage(stage)
        }

        private fun startOpenProjects(projectFiles: List<File>) {
            for (file in projectFiles) {
                Project.load(file)
            }

        }
    }
}
