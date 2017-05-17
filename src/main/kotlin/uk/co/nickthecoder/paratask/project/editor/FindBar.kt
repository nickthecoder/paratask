package uk.co.nickthecoder.paratask.project.editor

import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.control.ToolBar
import org.fxmisc.richtext.CodeArea
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.gui.ShortcutHelper

class FindBar(val searcher: Searcher, val codeArea: CodeArea) : ToolBar() {

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