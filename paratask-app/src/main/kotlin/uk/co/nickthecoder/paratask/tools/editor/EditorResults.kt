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
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.ToolBar
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.project.ParataskActions
import uk.co.nickthecoder.paratask.project.ToolPane
import java.io.File

class EditorResults(
        override val tool: EditorTool,
        val file: File?)

    : AbstractResults(tool, file?.name ?: "New File") {

    val toolBar = ToolBar()

    val codeArea = CodeArea()

    override val node = codeArea

    val searcher = Searcher(codeArea)

    val findBar = FindBar(searcher, this)

    val dirtyProperty = SimpleBooleanProperty()

    var dirty: Boolean
        get() = dirtyProperty.get()
        set(value) {
            dirtyProperty.set(value)
            label = (if (value) "*" else "") + (file?.name ?: "New File")
        }

    init {
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        val shortcuts = ShortcutHelper("EditorTool", node)

        shortcuts.add(ParataskActions.EDIT_FIND_PREV) { searcher.onFindPrev() }
        shortcuts.add(ParataskActions.EDIT_FIND_NEXT) { searcher.onFindNext() }

        shortcuts.add(ParataskActions.EDIT_CUT) { onCut() }
        shortcuts.add(ParataskActions.EDIT_COPY) { onCopy() }
        shortcuts.add(ParataskActions.EDIT_PASTE) { onPaste() }
        shortcuts.add(ParataskActions.ESCAPE) { onEscape() }

        with(toolBar.items)
        {
            val save = ParataskActions.FILE_SAVE.createButton(shortcuts) { onSave() }
            val undo = ParataskActions.EDIT_UNDO.createButton(shortcuts) { onUndo() }
            val redo = ParataskActions.EDIT_REDO.createButton(shortcuts) { onRedo() }
            val find = ParataskActions.EDIT_FIND.createButton(shortcuts) { onFind() }

            undo.disableProperty().bind(Bindings.not(codeArea.undoAvailableProperty()))
            redo.disableProperty().bind(Bindings.not(codeArea.redoAvailableProperty()))
            save.disableProperty().bind(Bindings.not(dirtyProperty))

            addAll(save, undo, redo, find)
        }

        file?.let { load(it) }

        codeArea.plainTextChanges().addObserver { dirty = true }
        findBar.isVisible = false
    }

    constructor(tool: EditorTool, text: String) : this(tool, null) {
        load(text)
    }

    override fun selected() {
        super.selected()
        tool.toolPane?.halfTab?.toolBars?.left = toolBar
        file?.path?.let { tool.longTitle = "Editor $it" }
    }

    override fun deselected() {
        hideToolBar()
    }

    // Bodge. See focus().
    private var isAttached: Boolean = false

    override fun focus() {
        // This is a bodge - The half tab wasn't rendering if I just called requestFocus without the if and the runLater.
        // Problem only happened during initial load when the editor was not the last tab.
        // Don't know why, but I just needed something quick and dirty to fix it temporarily.
        // TODO Try to remove this bodge!
        if (isAttached) {
            Platform.runLater {
                codeArea.requestFocus()
            }
        }
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        isAttached = true

        tool.goToLineP.value?.let {
            codeArea.positionCaret(codeArea.position(it - 1, 0).toOffset())
        }

        if (tool.findTextP.value != "") {
            searcher.searchString = tool.findTextP.value
            searcher.matchCase = tool.matchCaseP.value == true
            searcher.useRegex = tool.useRegexP.value == true

            Platform.runLater {
                // Without the run later, the selection stays off screen.
                searcher.beginFind()
                showFindBar()
            }
        }
    }

    override fun detaching() {
        super.detaching()
        isAttached = false
        hideToolBar()
    }

    fun hideToolBar() {
        findBar.detaching()
        findBar.isVisible = false
        tool.toolPane?.halfTab?.toolBars?.bottom = null
        tool.toolPane?.halfTab?.toolBars?.left = null
        codeArea.requestFocus()
    }

    fun showFindBar() {
        if (findBar.isVisible) {
            findBar.focus()
        } else {
            findBar.isVisible = true
            tool.toolPane?.halfTab?.toolBars?.bottom = findBar
            findBar.attached()
        }
    }

    fun load(text: String) {
        codeArea.replaceText(0, codeArea.length, text)
        codeArea.selectRange(0, 0)
        codeArea.positionCaret(0)
    }

    fun load(file: File) {
        load(file.readText())
        codeArea.undoManager.forgetHistory()
        dirty = false
    }

    fun onSave() {
        dirty = false
        file?.writeText(codeArea.text)
    }

    fun onUndo() {
        if (codeArea.isUndoAvailable) {
            codeArea.undo()
        }
    }

    fun onRedo() {
        if (codeArea.isRedoAvailable) {
            codeArea.redo()
        }
    }

    fun onCopy() {
        codeArea.copy()
    }

    fun onPaste() {
        codeArea.paste()
    }

    fun onCut() {
        codeArea.cut()
    }

    fun onEscape() {
        hideToolBar()
        searcher.reset()
    }

    fun onFind() {
        showFindBar()
    }
}
