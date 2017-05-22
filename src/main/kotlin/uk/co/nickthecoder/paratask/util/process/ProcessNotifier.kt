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

package uk.co.nickthecoder.paratask.util.process

class ProcessNotifier {

    private val listeners = mutableListOf<ProcessListener>()

    private var ended: Boolean = false

    private lateinit var process: Process

    fun start(process: Process) {
        this.process = process

        val thread = Thread {
            process.waitFor()
            ended = true
            listeners.forEach {
                listener ->
                try {
                    listener.finished(process)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        thread.name = "ProcessNotifier"
        thread.isDaemon = true
        thread.start()
    }

    fun add(listener: ProcessListener) {
        listeners.add(listener)
        if (ended) {
            listener.finished(process)
        }
    }

    fun remove(listener: ProcessListener) {
        listeners.remove(listener)
    }
}
