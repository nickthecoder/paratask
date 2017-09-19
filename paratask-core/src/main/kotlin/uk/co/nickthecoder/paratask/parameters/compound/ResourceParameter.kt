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

package uk.co.nickthecoder.paratask.parameters.compound

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.uncamel
import java.net.URL

class ResourceParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Resource? = null,
        val expectFile: Boolean? = null,
        required: Boolean = true)

    : CompoundParameter<Resource?>(
        name = name,
        label = label,
        description = description) {

    val fileP = FileParameter(name + "_file", label = "File", required = required, expectFile = expectFile)
    var file by fileP

    val urlP = StringParameter(name + "_url", label = "URL", required = required)
    var url by urlP

    val fileOrUrlP = OneOfParameter(name + "_fileOrUrl", label = "", value = fileP, choiceLabel = "")
            .addParameters(fileP, urlP)
    var fileOrUrl by fileOrUrlP

    override var value: Resource?
        get() {
            if (fileOrUrl == fileP) {
                return file?.let { Resource(it) }
            } else if (fileOrUrl == urlP) {
                if (url.isNotBlank()) {
                    return Resource(url)
                }
            }
            return null
        }
        set(v) {
            if (v == null) {
                fileOrUrl = fileP
                file = null
                url = ""
            } else {
                if (v.isFileOrDirectory()) {
                    fileOrUrl = fileP
                    file = v.file
                } else {
                    fileOrUrl = urlP
                    url = v.url.toString()
                }
            }
        }

    override val converter = Resource.converter

    init {
        addParameters(fileOrUrlP)
        fileOrUrlP.asHorizontal(LabelPosition.NONE)
        asHorizontal(LabelPosition.NONE)

        this.value = value
    }

    override fun errorMessage(v: Resource?): String? {
        if (fileP.required && v == null) {
            return "Required"
        }
        return null
    }

    override fun errorMessage(): String? {
        if (fileOrUrl == null && fileP.required) {
            return "Required"
        }

        if (fileOrUrl == urlP) {
            try {
                URL(url)
            } catch (e: Exception) {
                return "Invalid URL"
            }
        }

        return null
    }

    override fun toString() = "Resource" + super.toString()

    override fun copy() = ResourceParameter(name = name, label = label, description = description, value = value,
            expectFile = expectFile, required = fileP.required)
}
