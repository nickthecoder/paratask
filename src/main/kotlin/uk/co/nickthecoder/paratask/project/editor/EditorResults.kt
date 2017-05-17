package uk.co.nickthecoder.paratask.project.editor

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.gui.project.AbstractResults
import java.io.File

class EditorResults(override val tool: EditorTool, val file: File?)

    : AbstractResults(tool, file?.name ?: "New File") {

    override val node = BorderPane()

    val toolBar = ToolBar()

    val codeArea = CodeArea()

    val searcher = Searcher(codeArea)

    val findBar = FindBar(searcher, codeArea)

    val dirtyProperty = SimpleBooleanProperty()

    var dirty: Boolean
        get() = dirtyProperty.get()
        set(value) {
            dirtyProperty.set(value)
            label = (if (value) "*" else "") + (file?.name ?: "New File")
        }

    val dummyParent = StackPane()

    init {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea))

        node.bottom = toolBar
        node.center = codeArea

        val shortcuts = ShortcutHelper("EditorTool", node)

        shortcuts.add(Actions.EDIT_CUT, { onCut() })
        shortcuts.add(Actions.EDIT_COPY, { onCopy() })
        shortcuts.add(Actions.EDIT_PASTE, { onPaste() })
        shortcuts.add(Actions.ESCAPE, { onEscape() })

        with(toolBar.getItems())
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

    }

    override fun selected() {
        codeArea.requestFocus()
        tool.toolPane?.halfTab?.toolBars?.left = toolBar
    }

    override fun deselected() {
        hide(toolBar, findBar)
    }

    fun hide(vararg nodes: Node) {
        nodes.forEach { dummyParent.children.add(it) }
    }

    fun showFindBar() {
        tool.toolPane?.halfTab?.toolBars?.bottom = findBar
    }

    fun load(file: File) {
        codeArea.replaceText(0, codeArea.length, file.readText())
        codeArea.undoManager.forgetHistory()
        dirty = false
    }

    fun onSave() {
        dirty = false
        file?.writeText(codeArea.text)
    }

    fun onUndo() {
        if (codeArea.isUndoAvailable()) {
            codeArea.undo()
        }
    }

    fun onRedo() {
        if (codeArea.isRedoAvailable()) {
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
        hide(findBar)
    }

    fun onFind() {
        showFindBar()
        findBar.textField.requestFocus()
    }
}
