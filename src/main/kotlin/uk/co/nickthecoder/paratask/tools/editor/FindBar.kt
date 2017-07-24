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

import javafx.application.Platform
import javafx.scene.control.*
import uk.co.nickthecoder.paratask.gui.FocusHelper
import uk.co.nickthecoder.paratask.gui.FocusListener
import uk.co.nickthecoder.paratask.project.ParataskActions
import uk.co.nickthecoder.paratask.gui.ShortcutHelper

class FindBar(val searcher: Searcher, val editorResults: EditorResults) : ToolBar(), FocusListener {

    val codeArea = editorResults.codeArea

    val searchTextField = TextField()

    val goButton: Button

    private var focusHelper: FocusHelper? = null

    val matchPositionLabel = Label()

    val regexCheck = CheckBox("Regex")

    val caseCheck = CheckBox("Match Case")

    init {
        val shortcuts = ShortcutHelper("FindBar", this)

        shortcuts.add(ParataskActions.ESCAPE, { editorResults.onEscape() })

        goButton = ParataskActions.EDIT_FIND_GO.createButton { searcher.beginFind() }

        with(items) {
            add(searchTextField)
            add(goButton)
            add(ParataskActions.EDIT_FIND_PREV.createButton(shortcuts) { searcher.onFindPrev() })
            add(ParataskActions.EDIT_FIND_NEXT.createButton(shortcuts) { searcher.onFindNext() })
            add(matchPositionLabel)
            add(regexCheck)
            add(caseCheck)
        }

        matchPositionLabel.textProperty().bind(searcher.matchPositionProperty)
        regexCheck.selectedProperty().bindBidirectional(searcher.useRegexProperty)
        caseCheck.selectedProperty().bindBidirectional(searcher.matchCaseProperty)

        searchTextField.textProperty().bindBidirectional(searcher.searchStringProperty)
    }


    // Part of the "bodge" in focus() method. See below for details.
    private var focusDone: Boolean = false

    fun attached() {
        focus()
    }

    fun focus() {
        // The very first time I press ctrl+F, searchTextField.scene returns null, which causes requestFocsus to fail.
        // After way too much time trying to debug this (with intermittent results), I decided this was the "easiest"
        // way to fix the problem.
        // While spawning a thread is a big hammer to crack a tiny nut, it WORKS!
        // So far, it *always* succeeds on the 2nd iteration of the loop
        // Subsequent ctrl+F's don't require the Thread.
        // PS, a simple "Platform.runLater" only worked INTERMITTENTLY, so don't replace this code, unless you are sure
        // that the replacement ALWAYS works!
        fun innerFocus() {
            Platform.runLater {
                if (focusDone && searchTextField.scene != null) {
                    if ( focusHelper == null) {
                        focusHelper = FocusHelper(this, this, name = "FindBar")
                    }
                    searchTextField.requestFocus()
                    focusDone = false
                }
            }
        }

        if (searchTextField.scene == null) {
            focusDone = true
            // Try 5 times, for upto 1/2 second, and then give up.
            Thread(Runnable {
                for (i in 0..4) {
                    innerFocus()
                    if (!focusDone) {
                        break
                    }
                    Thread.sleep(100)
                }
            }).start()
        } else {
            focusDone = true
            innerFocus()
        }
    }

    fun detaching() {
        focusHelper?.remove()
        focusHelper = null
    }

    override fun focusChanged(gained: Boolean) {
        goButton.setDefaultButton(gained)
    }

}
