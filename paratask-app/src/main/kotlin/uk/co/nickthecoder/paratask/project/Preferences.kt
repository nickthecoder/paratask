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
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.applicationDirectory
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory

object Preferences {

    val optionsPath = mutableListOf<Resource>()

    var configDirectory = homeDirectory.child(".config", "paratask")

    var projectsDirectory = configDirectory.child("projects")

    init {
        val appOptionsDir = applicationDirectory.child("options")
        if (appOptionsDir.exists() && appOptionsDir.isDirectory) {
            optionsPath.add(Resource(appOptionsDir))
        }
        val configOptionsDir = configDirectory.child("options")
        if (configOptionsDir.exists() && configOptionsDir.isDirectory) {
            optionsPath.add(Resource(configOptionsDir))
        }
    }

    fun createOptionsResourceParameter(
            name: String = "directory",
            required: Boolean = false,
            defaultFirst: Boolean = required,
            onlyDirectories: Boolean = false

    ): ChoiceParameter<Resource?> {

        val value = if (required) optionsPath.first() else null
        val result = ChoiceParameter<Resource?>(name, required = required, value = value)
        if (!required) {
            result.choice("", null, "<ALL>")
        }

        optionsPath.forEach { resource ->
            if (resource.isFileOrDirectory() || !onlyDirectories) {

                result.choice(resource.path, resource, resource.shortPath())
                if (defaultFirst && result.value == null) {
                    result.value = resource
                }
            }
        }
        return result
    }
}