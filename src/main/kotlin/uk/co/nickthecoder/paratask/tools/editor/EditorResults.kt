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

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.project.Actions
import uk.co.nickthecoder.paratask.project.ShortcutHelper
import java.io.File

class EditorResults(override val tool: EditorTool, val file: File?)

    : AbstractResults(tool, file?.name ?: "New File") {

    override val node = BorderPane()

    val toolBar = ToolBar()

    val codeArea = CodeArea()

    val searcher = Searcher(codeArea)

    val findBar = FindBar(searcher, this)

    val dirtyProperty = SimpleBooleanProperty()

    var dirty: Boolean
        get() = dirtyProperty.get()
        set(value) {
            dirtyProperty.set(value)
            label = (if (value) "*" else "") + (file?.name ?: "New File")
        }

    val dummyParent = StackPane()

    init {
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        node.bottom = toolBar
        node.center = codeArea

        val shortcuts = ShortcutHelper("EditorTool", node)

        shortcuts.add(Actions.EDIT_FIND_PREV) { searcher.onFindPrev() }
        shortcuts.add(Actions.EDIT_FIND_NEXT) { searcher.onFindNext() }

        shortcuts.add(Actions.EDIT_CUT) { onCut() }
        shortcuts.add(Actions.EDIT_COPY) { onCopy() }
        shortcuts.add(Actions.EDIT_PASTE) { onPaste() }
        shortcuts.add(Actions.ESCAPE) { onEscape() }

        with(toolBar.items)
        {
            val save = Actions.FILE_SAVE.createButton(shortcuts) { onSave() }
            val undo = Actions.EDIT_UNDO.createButton(shortcuts) { onUndo() }
            val redo = Actions.EDIT_REDO.createButton(shortcuts) { onRedo() }
            val find = Actions.EDIT_FIND.createButton(shortcuts) { onFind() }

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
        tool.toolPane?.halfTab?.toolBars?.left = toolBar
        file?.path?.let { tool.longTitle = "Editor $it" }
    }

    override fun deselected() {
        hideToolBar()
        dummyParent.children.add(toolBar)
    }

    override fun focus() {
        codeArea.requestFocus()
    }

    fun hideToolBar() {
        findBar.detaching()
        findBar.isVisible = false
        tool.toolPane?.halfTab?.toolBars?.bottom = null
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
        println("Copy")
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
