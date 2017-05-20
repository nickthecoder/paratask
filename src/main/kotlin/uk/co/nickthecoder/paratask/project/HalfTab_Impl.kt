package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.gui.ButtonGroup

class HalfTab_Impl(override var toolPane: ToolPane)

    : javafx.scene.layout.BorderPane(), HalfTab {

    override val toolBars = javafx.scene.layout.BorderPane()

    private val toolBar = javafx.scene.control.ToolBar()

    private val shortcuts = ShortcutHelper("HalfTab", this)

    override val optionsField = javafx.scene.control.TextField()

    override lateinit var projectTab: ProjectTab

    val stopButton: javafx.scene.control.Button

    val runButton: javafx.scene.control.Button

    private val history = uk.co.nickthecoder.paratask.project.History(this)

    val optionsContextMenu = javafx.scene.control.ContextMenu()

    init {
        toolBars.center = toolBar

        center = toolPane as javafx.scene.Node
        bottom = toolBars

        with(optionsField) {
            prefColumnCount = 6
            addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, { optionsFieldKeyPressed(it) })
            addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, { optionFieldMouse(it) })
            addEventHandler(javafx.scene.input.MouseEvent.MOUSE_RELEASED, { optionFieldMouse(it) })
            setContextMenu(optionsContextMenu)
        }

        val historyGroup = ButtonGroup()
        val backButton = Actions.HISTORY_BACK.createButton(shortcuts) { history.undo() }
        val forwardButton = Actions.HISTORY_FORWARD.createButton(shortcuts) { history.redo() }
        backButton.disableProperty().bind(history.canUndoProperty.not())
        forwardButton.disableProperty().bind(history.canRedoProperty.not())
        historyGroup.children.addAll(backButton, forwardButton)

        val runStopStack = javafx.scene.layout.StackPane()
        stopButton = Actions.TOOL_STOP.createButton(shortcuts) { onStop() }
        runButton = Actions.TOOL_RUN.createButton(shortcuts) { onRun() }
        runStopStack.children.addAll(stopButton, runButton)

        with(toolBar.getItems()) {
            add(optionsField)
            add(runStopStack)
            add(Actions.TOOL_SELECT.createToolButton(shortcuts) { tool -> onSelectTool(tool) })
            add(Actions.SPLIT_TOOL_TOGGLE.createButton(shortcuts) { toolPane.toggleParameters() })
            add(historyGroup)
            add(Actions.TOOL_CLOSE.createButton(shortcuts) { onClose() })
        }

        bindButtons()
    }

    override fun attached(projectTab: ProjectTab) {
        this.projectTab = projectTab

        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("HalfTab.attaching ToolPane")
        toolPane.attached(this)
        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("HalfTab.attached ToolPane")
    }

    override fun detaching() {
        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("HalfTab.detaching ToolPane")
        toolPane.detaching()
        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("HalfTab.detached ToolPane")
    }

    fun bindButtons() {
        runButton.disableProperty().bind(toolPane.tool.taskRunner.disableRunProperty)
        runButton.visibleProperty().bind(toolPane.tool.taskRunner.showRunProperty)
        stopButton.visibleProperty().bind(toolPane.tool.taskRunner.showStopProperty)
    }

    override fun changeTool(tool: uk.co.nickthecoder.paratask.Tool) {
        toolPane.detaching()
        children.remove(toolPane as javafx.scene.Node)

        toolPane = ToolPane_Impl(tool)

        center = toolPane as javafx.scene.Node
        toolPane.attached(this)

        projectTab.changed()

        bindButtons()
        history.push(tool)
    }

    fun onStop() {
        val tool = toolPane.tool
        if (tool is uk.co.nickthecoder.paratask.util.Stoppable) {
            tool.stop()
        }
    }

    fun onRun() {
        toolPane.parametersPane.run()
    }

    fun onSelectTool(tool: uk.co.nickthecoder.paratask.Tool) {
        changeTool(tool.copy())
    }

    fun onClose() {
        projectTab.remove(toolPane)
    }

    override fun pushHistory() {
        history.push(toolPane.tool)
    }

    override fun pushHistory(tool: uk.co.nickthecoder.paratask.Tool) {
        history.push(tool)
    }

    fun optionsFieldKeyPressed(event: javafx.scene.input.KeyEvent) {
        var done = false
        val tool = toolPane.resultsTool()
        val runner = tool.optionsRunner

        if (Actions.OPTION_RUN.match(event)) {
            done = runner.runNonRow(optionsField.text, prompt = false, newTab = event.isShiftDown)
        } else if (Actions.OPTION_PROMPT.match(event)) {
            done = runner.runNonRow(optionsField.text, prompt = true, newTab = event.isShiftDown)
        } else if (Actions.CONTEXT_MENU.match(event)) {
            onOptionsContextMenu()
            event.consume()
        }

        if (done) {
            optionsField.text = ""
        }
    }

    fun optionFieldMouse(event: javafx.scene.input.MouseEvent) {
        if (event.isPopupTrigger) {
            onOptionsContextMenu()
            event.consume()
        }
    }

    private fun onOptionsContextMenu() {
        val tool = toolPane.resultsTool()
        tool.optionsRunner.createNonRowOptionsMenu(optionsContextMenu)
        optionsContextMenu.show(optionsField, javafx.geometry.Side.BOTTOM, 0.0, 0.0)
    }
}
