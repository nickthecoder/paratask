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

import javafx.scene.control.TextField
import javafx.scene.control.ToolBar
import org.fxmisc.richtext.CodeArea
import uk.co.nickthecoder.paratask.project.Actions
import uk.co.nickthecoder.paratask.project.ShortcutHelper

class FindBar(val searcher: Searcher, codeArea: CodeArea) : ToolBar() {

    val textField = TextField()

    init {
        val shortcuts = ShortcutHelper("FindBar", codeArea)
        with(items) {
            add(textField)
            add(Actions.EDIT_FIND_PREV.createButton(shortcuts) { searcher.onFindPrev() })
            add(Actions.EDIT_FIND_NEXT.createButton(shortcuts) { searcher.onFindNext() })
        }

        textField.textProperty().bindBidirectional(searcher.searchStringProperty)
    }

}