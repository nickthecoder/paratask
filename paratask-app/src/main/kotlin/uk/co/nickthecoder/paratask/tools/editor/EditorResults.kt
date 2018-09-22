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
import javafx.scene.control.ToolBar
import javafx.scene.input.DataFormat
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.CompoundDropHelper
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.gui.SimpleDropHelper
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.project.ParataskActions
import uk.co.nickthecoder.paratask.project.ResultsTab
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.tedi.BetterUndoRedo
import uk.co.nickthecoder.tedi.TediArea
import uk.co.nickthecoder.tedi.ui.FindBar
import uk.co.nickthecoder.tedi.ui.RemoveHiddenChildren
import uk.co.nickthecoder.tedi.ui.ReplaceBar
import uk.co.nickthecoder.tedi.ui.TextInputControlMatcher
import java.io.File

class EditorResults(
        override val tool: EditorTool,
        val file: File?)

    : AbstractResults(tool, file?.name ?: "New File", canClose = true) {

    val toolBar = ToolBar()

    val tediArea = TediArea()

    private val matcher = TextInputControlMatcher(tediArea)

    private val findBar = FindBar(matcher)

    private val replaceBar = ReplaceBar(matcher)

    private val borderPane = BorderPane()

    private val searchAndReplace = VBox()

    private val toggleFind = findBar.createToggleButton()

    private val toggleReplace = replaceBar.createToggleButton()

    override val node = borderPane

    val dirtyProperty = SimpleBooleanProperty()

    var dirty: Boolean
        get() = dirtyProperty.get()
        set(value) {
            dirtyProperty.set(value)
            label = (if (value) "*" else "") + (file?.name ?: "New File")
        }


    val textDropHelper = SimpleDropHelper<String>(DataFormat.PLAIN_TEXT, arrayOf(TransferMode.COPY)) { _, text ->
        insertText(text)
    }
    val filesDropHelper = DropFiles(arrayOf(TransferMode.COPY)) { _, files ->
        val text = files.map { it.path }.joinToString(separator = "\n")
        insertText(text)
    }
    val compoundDropHelper = CompoundDropHelper(filesDropHelper, textDropHelper)

    init {

        RemoveHiddenChildren(searchAndReplace.children)
        with(searchAndReplace) {
            children.addAll(findBar.toolBar, replaceBar.toolBar)
        }
        matcher.inUse = false

        with(borderPane) {
            center = tediArea
            top = searchAndReplace
        }

        with(tediArea) {
            undoRedo = BetterUndoRedo(tediArea)
            displayLineNumbers = true
            styleClass.add("code")
        }

        compoundDropHelper.applyTo(tediArea)

        val shortcuts = ShortcutHelper("EditorTool", node)

        shortcuts.add(ParataskActions.EDIT_CUT) { onCut() }
        shortcuts.add(ParataskActions.EDIT_COPY) { onCopy() }
        shortcuts.add(ParataskActions.EDIT_PASTE) { onPaste() }
        shortcuts.add(ParataskActions.EDIT_FIND) { matcher.inUse = true }
        shortcuts.add(ParataskActions.EDIT_REPLACE) { onReplace() }
        shortcuts.add(ParataskActions.ESCAPE) { onEscape() }

        toolBar.styleClass.add("bottom")
        with(toolBar.items)
        {
            val save = ParataskActions.FILE_SAVE.createButton(shortcuts) { onSave() }
            val undo = ParataskActions.EDIT_UNDO.createButton(shortcuts) { onUndo() }
            val redo = ParataskActions.EDIT_REDO.createButton(shortcuts) { onRedo() }

            undo.disableProperty().bind(tediArea.undoRedo.undoableProperty.not())
            redo.disableProperty().bind(tediArea.undoRedo.redoableProperty.not())
            save.disableProperty().bind(dirtyProperty.not())

            addAll(save, undo, redo, toggleFind, toggleReplace)
        }

        file?.let { load(it) }

        tediArea.textProperty().addListener { _, _, _ -> dirty = true }
    }

    constructor(tool: EditorTool, text: String) : this(tool, null) {
        load(text)
    }

    override fun selected() {
        super.selected()
        tool.toolPane?.halfTab?.toolBars?.left = toolBar
        file?.path?.let { tool.longTitle = "Editor $it" }
    }

    override fun focus() {
        ParaTaskApp.logFocus("EditorResults.focus. tediArea.requestFocus()")
        tediArea.requestFocus()
    }

    override fun attached(resultsTab: ResultsTab, toolPane: ToolPane) {
        super.attached(resultsTab, toolPane)
    }

    override fun closed() {
        super.closed()
        tool.filesP.remove(file)
    }

    fun load(text: String) {
        tediArea.replaceText(0, tediArea.length, text)
        tediArea.selectRange(0, 0)
        tediArea.positionCaret(0)
    }

    fun load(file: File) {
        load(file.readText())
        tediArea.undoRedo.clear()
        dirty = false
    }

    fun onSave() {
        dirty = false
        file?.writeText(tediArea.text)
    }

    fun onUndo() {
        tediArea.undoRedo.undo()
    }

    fun onRedo() {
        tediArea.undoRedo.redo()
    }

    fun onCopy() {
        tediArea.copy()
    }

    fun onPaste() {
        tediArea.paste()
    }

    fun onCut() {
        tediArea.cut()
    }

    fun onReplace() {
        val wasInUse = matcher.inUse
        replaceBar.toolBar.isVisible = true
        if (wasInUse) {
            replaceBar.requestFocus()
        }
    }

    fun onEscape() {
        matcher.inUse = false
    }

    fun insertText(text: String) {
        tediArea.insertText(tediArea.caretPosition, text)
    }
}
