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

import uk.co.nickthecoder.paratask.project.Preferences

class TopLevelOptions(val optionsName: String) {

    // NOTE, we could cache this list, and whenever a FileOptions is saved, throw the cache away
    // 
    fun listFileOptions(): List<FileOptions> {

        val fileOptionsList = mutableListOf<FileOptions>()

        val addedOptionsNames = mutableSetOf<String>()

        fun add(optionsName: String) {

            if (addedOptionsNames.contains(optionsName)) {
                return
            }
            addedOptionsNames.add(optionsName)

            for (directory in Preferences.optionsPath) {
                val fileOptions: FileOptions = OptionsManager.getFileOptions(optionsName, directory)
                fileOptionsList.add(fileOptions)

                for (include in fileOptions.listIncludes()) {
                    add(include)
                }
            }
        }

        add(optionsName)
        add("global")

        return fileOptionsList
    }

    fun find(code: String): Option? {

        // First we build a list of all the FileOptions and then iterate over the list to find the Option
        val fileOptionsList = listFileOptions()

        return fileOptionsList.map { it.find(code) }.firstOrNull { it != null }
    }
}
