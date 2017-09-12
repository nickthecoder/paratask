package uk.co.nickthecoder.paratask.gui

import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.util.AutoExit
import java.lang.reflect.Field
import java.lang.reflect.Method


class VariablePrompter(val sourceText: TextInputControl, val scriptVariables: ScriptVariables) {

    val borderPane = BorderPane()

    val instructions = Label(
            """Click an item to copy it to the clipboard.
Double click to close the window.
Then use Ctrl+V to paste it into your script.""")

    val variablesTree = TreeView<TreeData>()

    val code = TextField()

    lateinit var stage: Stage

    fun build() {


        instructions.styleClass.add("instructions")

        with(borderPane) {
            styleClass.add("variable-prompter")
            top = instructions
            center = variablesTree
            bottom = code
        }

        with(variablesTree) {
            root = MyTreeItem(RootData())
            isShowRoot = false

            selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                selectedItem(newValue)
            }
            root.children.firstOrNull()?.isExpanded = true

            addEventFilter(MouseEvent.MOUSE_CLICKED) { event ->
                if (event.clickCount == 2) {
                    stage.hide()
                    event.consume()
                }
            }
        }

    }


    fun show() {
        val scene = Scene(borderPane)
        ParaTask.style(scene)

        stage = Stage()
        stage.width = 500.0
        stage.title = "Variables"
        stage.scene = scene
        AutoExit.show(stage)
    }

    fun selectedItem(item: TreeItem<TreeData>) {
        if (item is MyTreeItem) {
            code.text = item.treeData.fullCode()

            val clipboard = Clipboard.getSystemClipboard()
            val content = ClipboardContent()
            content.putString(code.text)
            clipboard.setContent(content)
        }
    }


    fun acceptField(field: Field): Boolean {
        if (excludeFieldNames.contains(field.name)) {
            return false
        }
        return true
    }

    fun acceptMethod(method: Method): Boolean {
        if (excludeMethodNames.contains(method.name)) {
            return false
        }
        return true
    }


    companion object {
        val excludeFieldNames = hashSetOf(
                "Companion", "class", "rowFilter", "rowFilters", "tabDropHelper", "tabDropHelperProperty", "taskD", "taskRunner",
                "toolPane", "createHeader", "createFooter", "createHeaderRows", "createResults", "createRow", "customCheck", "detatching",
                "ewnsureToolPane", "evaluateTask", "focusOnParameter", "loadProblem", "updateResults", "updateRow", "updateTitle",
                "valueParameters")

        val excludeMethodNames = hashSetOf("notify", "notifyAll", "wait", "equals", "hashCode", "toString")
    }


    class MyTreeItem(val treeData: TreeData) : TreeItem<TreeData>(treeData) {

        override fun isLeaf() = treeData.isLeaf()

        var firstTimeChildren = true

        override fun getChildren(): ObservableList<TreeItem<TreeData>> {
            val superChildren = super.getChildren()

            if (firstTimeChildren) {
                firstTimeChildren = false
                treeData.listChildren().forEach {
                    superChildren.add(MyTreeItem(it))
                }
            }

            return superChildren
        }
    }

    interface TreeData {
        fun listChildren(): List<TreeData>
        fun isLeaf(): Boolean
        fun fullCode(): String
    }

    inner class RootData : TreeData {
        override fun listChildren(): List<TreeData> {
            return scriptVariables.map.map { (name, type) -> PropertyItem(this, name, type) }
        }

        override fun isLeaf() = false
        override fun fullCode(): String = ""
    }

    inner abstract class PropertyOrMethod(val parent: TreeData, val name: String, val type: Class<*>?) : TreeData {

        override fun isLeaf(): Boolean = type == null || type === Unit.javaClass

        override fun listChildren(): List<PropertyOrMethod> {
            if (type == null) {
                return emptyList()
            }
            val fields = mutableSetOf<PropertyOrMethod>()
            val methods = mutableSetOf<PropertyOrMethod>()

            fields.addAll(type.fields.filter { acceptField(it) }.map { PropertyItem(this, it.name, it.type) })

            type.methods.filter { acceptMethod(it) }.forEach { method ->
                if (method.name.startsWith("get") && method.name.length > 3 && method.parameterCount == 0 && method.returnType != Unit.javaClass) {
                    // Getter
                    fields.add(PropertyItem(this, method.name.substring(3, 4).toLowerCase() + method.name.substring(4), method.returnType))
                } else if (method.name.startsWith("set") && method.name.length > 3 && method.parameterCount == 1) {
                    // Setter Do nothing
                } else {
                    // Regular method
                    methods.add(MethodItem(this, method))
                }
            }

            return fields.sortedBy { it.name } + methods.sortedBy { it.name }

        }

        override fun fullCode(): String {
            val pCode = parent.fullCode()
            if (pCode.isEmpty()) {
                return code()
            } else {
                return "$pCode.${code()}"
            }
        }

        abstract fun code(): String

        override fun equals(obj: Any?): Boolean {
            return obj is PropertyOrMethod && obj.code() == this.code()
        }

        override fun hashCode(): Int = code().hashCode()
    }

    inner class PropertyItem(parent: TreeData, name: String, type: Class<*>) : PropertyOrMethod(parent, name, type) {

        override fun toString() = name
        override fun code(): String = name
    }

    inner class MethodItem(parent: TreeData, val method: Method) : PropertyOrMethod(parent, method.name, method.returnType) {
        override fun toString(): String {
            val params = method.parameterCount
            val paramsString = if (params == 0) "" else params.toString()

            return "$name(${paramsString})"
        }

        override fun code(): String {
            val params = method.parameterTypes.map { it.simpleName.decapitalize() }.joinToString(separator = ",")
            return "$name($params)"
        }
    }

}
