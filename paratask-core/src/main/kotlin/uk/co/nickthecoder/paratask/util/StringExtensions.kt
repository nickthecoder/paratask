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

package uk.co.nickthecoder.paratask.util


/**
 * Convert a camel-case string to words separated by spaces. The first word can optionally be
 * capitalised.
 */
fun String.uncamel(space: String = " ", upperFirst: Boolean = true): String {

    val result = StringBuilder()
    var wasUpper: Boolean = false

    var first: Boolean = upperFirst
    for (i in 0..length - 1) {
        val c = get(i)
        if (first) {
            result.append(Character.toUpperCase(c))
            first = false
        } else {
            if (Character.isUpperCase(c) && !wasUpper) {
                result.append(space)
            }
            result.append(c)
        }
        wasUpper = Character.isUpperCase(c)
    }
    return result.toString()
}

fun String.escapeNL(): String {
    return this.replace("\\", "\\\\").replace("\n", "\\n")
}

fun String.unescapeNL(): String {
    val builder = StringBuilder(this.length)
    var foundSlash = false
    for (i in 0..this.length - 1) {
        val c = this.get(i)

        if (foundSlash) {
            if (c == 'n') {
                builder.append("\n")
            } else {
                builder.append(c)
            }
            foundSlash = false
        } else {
            if (c == '\\') {
                foundSlash = true
            } else {
                builder.append(c)
            }
        }
    }
    return builder.toString()
}
