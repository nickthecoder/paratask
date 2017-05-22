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

package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

object Preferences {

    val optionsPath = mutableListOf<File>()

    var projectsDirectory = homeDirectory.child(".config", "paratask", "projects")

    init {
        optionsPath.add(homeDirectory.child(".config", "paratask", "options"))
    }

    fun createOptionsDirectoryParameter(
            name: String = "directory",
            required: Boolean = false,
            defaultFirst: Boolean = required
    ): ChoiceParameter<File?> {

        val result = ChoiceParameter<File?>(name, value = null, required = required)
        if (!required) {
            result.choice("", null, "<ALL>")
        }

        for (directory in optionsPath) {
            result.choice(directory.name, directory, directory.path)
            if (defaultFirst && result.value == null) {
                result.value = directory
            }
        }
        return result
    }
}