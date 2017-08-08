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

import javafx.scene.control.*
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.gui.defaultWhileFocusWithin
import uk.co.nickthecoder.paratask.project.ParataskActions

class FindBar(val searcher: Searcher, val editorResults: EditorResults) : ToolBar() {

    val searchTextField = TextField()

    val goButton: Button

    val matchPositionLabel = Label()

    val regexCheck = CheckBox("Regex")

    val caseCheck = CheckBox("Match Case")

    init {
        val shortcuts = ShortcutHelper("FindBar", this)

        shortcuts.add(ParataskActions.ESCAPE, { editorResults.onEscape() })

        goButton = ParataskActions.EDIT_FIND_GO.createButton { searcher.beginFind() }

        items.addAll(searchTextField,
                goButton,
                ParataskActions.EDIT_FIND_PREV.createButton(shortcuts) { searcher.onFindPrev() },
                ParataskActions.EDIT_FIND_NEXT.createButton(shortcuts) { searcher.onFindNext() },
                matchPositionLabel,
                regexCheck,
                caseCheck)

        matchPositionLabel.textProperty().bind(searcher.matchPositionProperty)
        regexCheck.selectedProperty().bindBidirectional(searcher.useRegexProperty)
        caseCheck.selectedProperty().bindBidirectional(searcher.matchCaseProperty)

        searchTextField.textProperty().bindBidirectional(searcher.searchStringProperty)
    }

    fun attached() {
        focus()
        //goButton.defaultWhileFocusWithin(this, "FindBar Go", scene = editorResults.codeArea.scene)
        goButton.defaultWhileFocusWithin(this, "FindBar Go")
    }

    fun focus() {
        if (searchTextField.scene == null) {
            // The text field isn't part of the scene yet, so request focus when it is...
            goButton.sceneProperty().addListener { _, _, newValue ->
                if (newValue != null) {
                    searchTextField.requestFocus()
                }
            }
        } else {
            searchTextField.requestFocus()
        }
    }

    fun detaching() {
    }

}
