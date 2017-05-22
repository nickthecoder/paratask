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

import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableIntegerValue
import org.fxmisc.richtext.CodeArea
import java.util.regex.Pattern

class Searcher(val codeArea: CodeArea) {

    val searchStringProperty = SimpleStringProperty()

    var searchString: String
        get() = searchStringProperty.get()
        set(v) {
            searchStringProperty.set(v)
        }

    private val matches = mutableListOf<Match>()

    val matchCountValue: ObservableIntegerValue = object : IntegerBinding() {
        override fun computeValue() = matches.size
    }

    val foundValue: ObservableBooleanValue = object : BooleanBinding() {
        override fun computeValue() = matches.size > 0
    }

    private var findStartCaretPosition = 0

    val matchNumberValue: ObservableIntegerValue = object : IntegerBinding() {
        override fun computeValue() = matchNumber + 1
    }

    private var matchNumber = 0

    fun beginFind() {
        matches.clear()
        matchNumber = 0
        if (searchString == "") {
            return
        }

        val text = codeArea.text

        val pattern: Pattern
        try {
            pattern = Pattern.compile(searchString)
        } catch (e: Exception) {
            // TODO Feedback that the pattern is invalid
            return
        }
        val matcher = pattern.matcher(text)

        findStartCaretPosition = codeArea.caretPosition

        var count = 0
        var repositioned = false
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            if (!repositioned && start > findStartCaretPosition) {
                gotoMatch(count)
                repositioned = true
            }
            matches.add(Match(start, end))
            if (count++ > 500) break
        }
    }

    private fun gotoMatch(index: Int) {
        val match = matches[index]
        codeArea.selectRange(match.end, match.start)
        matchNumber = index
    }

    fun onFindNext() {
        // TODO Implement find next
    }

    fun onFindPrev() {
        // TODO Implement find prev
    }

    data class Match(val start: Int, val end: Int)
}
