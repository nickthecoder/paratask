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

package uk.co.nickthecoder.paratask.tools.places

import javafx.scene.input.DataFormat
import uk.co.nickthecoder.paratask.misc.FileTest
import uk.co.nickthecoder.paratask.util.Resource
import java.io.Serializable

open class Place(
        val resource: Resource,
        val label: String)

    : FileTest, Serializable {

    val name: String
        get() = if (resource.isFileOrDirectory()) resource.file!!.name else resource.toString()

    val path: String
        get() = if (resource.isFileOrDirectory()) resource.file!!.path else resource.toString()

    override val file
        get() = resource.file

    val url
        get() = resource.url

    fun isURL() = !resource.isFileOrDirectory()

    override fun toString() = "$resource $label"

    override fun equals(other: Any?): Boolean {
        if (other is Place) {
            return label == other.label && resource == other.resource
        }
        return false
    }

    override fun hashCode(): Int {
        return label.hashCode() + 31 * resource.hashCode()
    }

    companion object {
        val dataFormat = DataFormat("application/x-java-paratask-place-list")
    }
}

