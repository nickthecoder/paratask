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

package uk.co.nickthecoder.paratask.parameters

import uk.co.nickthecoder.paratask.ParameterResolver
import uk.co.nickthecoder.paratask.PlainDirectoryResolver
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.Labelled

interface Parameter : Labelled {

    val name: String

    val description: String

    var parent: Parameter?

    val parameterListeners: ParameterListeners

    var hidden: Boolean

    fun listen(listener: (event: ParameterEvent) -> Unit)

    fun isStretchy(): Boolean

    fun errorMessage(): String?

    fun createField(): ParameterField

    fun findRoot(): RootParameter? {
        return parent?.findRoot()
    }

    fun resolver() : ParameterResolver = findRoot()?.taskD?.resolver ?: PlainDirectoryResolver.instance

    fun findTaskD(): TaskDescription? = parent?.findTaskD()

    fun isProgrammingMode(): Boolean = findTaskD()?.programmingMode ?: false

    fun copy() : Parameter
}
