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

package uk.co.nickthecoder.paratask.options

import uk.co.nickthecoder.paratask.util.Resource

object OptionsManager {

    private val topLevelMap = mutableMapOf<String, TopLevelOptions>()

    fun getTopLevelOptions(optionsName: String): TopLevelOptions {
        val found = topLevelMap[optionsName]
        if (found == null) {
            val newTL = TopLevelOptions(optionsName)
            topLevelMap.put(optionsName, newTL)
            return newTL
        } else {
            return found
        }
    }

    fun findNonRowOption(code: String, optionsName: String): Option? {
        return getTopLevelOptions(optionsName).findNonRowOption(code)
    }

    fun findOptionForRow(code: String, optionsName: String, row: Any): Option? {
        return getTopLevelOptions(optionsName).findOptionForRow(code, row)
    }

    private val pathMap = mutableMapOf<Resource, OptionsPath>()

    private fun getOptionsPath(directoryResource: Resource): OptionsPath {
        val found = pathMap[directoryResource]
        if (found == null) {
            val newOP = OptionsPath(directoryResource)
            pathMap.put(directoryResource, newOP)
            return newOP
        } else {
            return found
        }
    }

    fun getFileOptions(optionsName: String, directoryResource: Resource): FileOptions {
        return getOptionsPath(directoryResource).getFileOptions(optionsName)
    }
}

data class OptionsPath(val directoryResource: Resource) {

    val fileOptionsMap = mutableMapOf<String, FileOptions>()

    fun getFileOptions(optionsName: String): FileOptions {
        val found = fileOptionsMap[optionsName]
        if (found == null) {
            val result = FileOptions(Resource(directoryResource, optionsName + ".json"))
            fileOptionsMap.put(optionsName, result)
            return result
        } else {
            return found
        }
    }
}
