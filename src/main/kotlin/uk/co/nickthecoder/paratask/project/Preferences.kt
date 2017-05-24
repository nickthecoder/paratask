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

import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

object Preferences {

    val optionsPath = mutableListOf<Resource>()

    var projectsDirectory = homeDirectory.child(".config", "paratask", "projects")

    init {
        optionsPath.add(Resource(homeDirectory.child(".config", "paratask", "options")))
        optionsPath.add(Resource(ParaTaskApp.javaClass.getResource("options")))
    }

    fun createOptionsResourceParameter(
            name: String = "directory",
            required: Boolean = false,
            defaultFirst: Boolean = required,
            onlyDirectories :Boolean = false

    ): ChoiceParameter<Resource?> {

        val result = ChoiceParameter<Resource?>(name, value = null, required = required)
        if (!required) {
            result.choice("", null, "<ALL>")
        }

        optionsPath.forEach { resource ->
            if ( resource.isFile() || ! onlyDirectories ) {
                val str = resource.toString()
                val name = resource.directoryName

                result.choice(str, resource, name)
                if (defaultFirst && result.value == null) {
                    result.value = resource
                }
            }
        }
        return result
    }
}