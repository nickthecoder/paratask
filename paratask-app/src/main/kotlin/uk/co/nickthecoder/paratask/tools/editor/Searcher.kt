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

package uk.co.nickthecoder.paratask.tools.editor

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import org.fxmisc.richtext.CodeArea
import uk.co.nickthecoder.paratask.ParaTaskApp
import java.util.regex.Pattern

class Searcher(val codeArea: CodeArea) {

    val MAX_MATCHES = 500

    val searchStringProperty = SimpleStringProperty()

    var searchString: String?
        get() = searchStringProperty.get()
        set(v) {
            searchStringProperty.set(v)
        }

    val matchCaseProperty = SimpleBooleanProperty()

    var matchCase: Boolean
        get() = matchCaseProperty.get()
        set(v) {
            matchCaseProperty.set(v)
        }

    val useRegexProperty = SimpleBooleanProperty()

    var useRegex: Boolean
        get() = useRegexProperty.get()
        set(v) {
            useRegexProperty.set(v)
        }

    private val matches = mutableListOf<Match>()

    val matchPositionProperty = SimpleStringProperty("")

    var matchPosition: String
        set(v) {
            matchPositionProperty.set(v)
        }
        get() = matchPositionProperty.get()

    private var matchNumber = 0

    fun reset() {
        matchNumber = 0
        matches.clear()
        matchPosition = ""
    }

    fun beginFind() {
        matches.clear()
        matchNumber = 0
        if (searchString == "") {
            return
        }

        val text = codeArea.text

        var pattern: Pattern
        try {
            val flags =
                    (if (matchCase) 0 else Pattern.CASE_INSENSITIVE) +
                            if (useRegex) 0 else Pattern.LITERAL
            pattern = Pattern.compile(searchString, flags)
        } catch (e: Exception) {
            pattern = Pattern.compile(searchString, Pattern.LITERAL)
        }
        val matcher = pattern.matcher(text)

        val caretPos = codeArea.caretPosition

        var count = 0
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            matches.add(Match(start, end))
            if (++count >= MAX_MATCHES) {
                break
            }
        }

        if (matches.isEmpty()) {
            matchPosition = "Not found"
        } else {
            for (i in 0..matches.size - 1) {
                val match = matches[i]
                if (match.start >= caretPos) {
                    gotoMatch(i)
                    return
                }
            }
            gotoMatch(0)
        }
    }

    private fun gotoMatch(index: Int) {
        val match = matches[index]
        codeArea.selectRange(match.end, match.start)
        matchNumber = index
        matchPosition = "Match ${matchNumber + 1} of " + if (matches.size >= MAX_MATCHES) "at least $MAX_MATCHES" else "${matches.size}"
        ParaTaskApp.logFocus("Searcher.gotoMatch. codeArea.requestFocus()")
        codeArea.requestFocus()
    }

    fun onFindNext() {
        if (matches.size == 0) return

        if (matchNumber >= matches.size - 1) {
            gotoMatch(0)
        } else {
            gotoMatch(matchNumber + 1)
        }
    }

    fun onFindPrev() {
        if (matches.size == 0) return

        if (matchNumber <= 0) {
            gotoMatch(matches.size - 1)
        } else {
            gotoMatch(matchNumber - 1)
        }
    }

    data class Match(val start: Int, val end: Int)
}
