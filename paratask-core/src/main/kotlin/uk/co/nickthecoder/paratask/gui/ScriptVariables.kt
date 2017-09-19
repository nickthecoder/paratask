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
package uk.co.nickthecoder.paratask.gui

/**
 * Used by the ScriptEditor, to show the user the properties that can be used within the script.
 * For example, when editing a RowFilter, the script has access to the "row" and the "tool", so in this case,
 * the map will contain two items.
 * Reflection can be used to show the user properties of those two object, as well as the object's methods.
 */
class ScriptVariables {

    val map = mutableMapOf<String, Class<*>>()

    fun add(name: String, type: Class<*>) {
        map[name] = type
    }

    fun add(name: String, className: String) {
        try {
            map[name] = Class.forName(className)
        } catch (e: Exception) {
            map.remove(name)
        }
    }
}
