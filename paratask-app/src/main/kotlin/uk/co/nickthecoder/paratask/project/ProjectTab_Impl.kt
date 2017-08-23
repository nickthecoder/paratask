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

package uk.co.nickthecoder.paratask.project

import com.sun.javafx.stage.StageHelper
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.*
import uk.co.nickthecoder.paratask.parameters.ShortcutParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import java.text.MessageFormat

class ProjectTab_Impl(override val tabs: ProjectTabs, toolPane: ToolPane)

    : ProjectTab, MyTab("Tab") {

    override lateinit var projectTabs: ProjectTabs

    override var left: HalfTab = HalfTab_Impl(toolPane)

    override var right: HalfTab? = null

    val splitPane = MySplitPane()

    val titleListener = TitleListener()

    val tabProperties = TabProperties()

    override var tabTemplate by tabProperties.tabTemplateP

    override var tabShortcut: KeyCodeCombination?
        get() = tabProperties.shortcutP.keyCodeCombination
        set(v) {
            tabProperties.shortcutP.keyCodeCombination = v
        }

    private val shortcuts = ShortcutHelper("ProjectTab", splitPane)

    private var tabDropHelperProperty: ObjectProperty<DropHelper?>? = null

    private var tabDropHelper: DropHelper? = null
        set(v) {
            field?.unapplyTo(this)
            field = v
            v?.applyTo(this)
        }

    private var tabDropHelperListener: ChangeListener<DropHelper?>? = null

    init {
        // Select the tab when a drag is over it. This will allow the item(s) to be dropped on nodes in the tab's contents
        // which would otherwise be unavailable (because another tab's content was visible).
        setOnDragEntered { event ->
            isSelected = true
        }

        content = splitPane
        splitPane.left = left as Node

        tabTemplate = "{0}"

        updateTab()
        updateTabDropHelper()

        val menu = ContextMenu()
        val properties = ParataskActions.TAB_PROPERTIES.createMenuItem { onEditTabProperties() }
        val duplicate = ParataskActions.DUPLICATE_TAB.createMenuItem { projectTabs.duplicateTab() }
        val close = ParataskActions.CLOSE_TAB.createMenuItem(shortcuts, { close() })
        menu.items.addAll(properties, duplicate, close)

        label.contextMenu = menu
    }

    private fun updateTabDropHelper() {
        tabDropHelperProperty?.removeListener(tabDropHelperListener)

        tabDropHelperProperty = left.toolPane.tool.tabDropHelperProperty
        tabDropHelper = tabDropHelperProperty?.get()
        tabDropHelperListener = ChangeListener<DropHelper?> { _, _, newValue ->
            tabDropHelper = newValue
        }
        tabDropHelperProperty?.addListener(tabDropHelperListener!!)
    }

    private fun updateTab() {
        titleListener.listen(left.toolPane.tool.shortTitleProperty)
        val imageView = left.toolPane.tool.icon?.let { ImageView(it) }
        graphic = imageView
    }

    override fun attached(projectTabs: ProjectTabs) {
        tabDropHelper = left.toolPane.tool.tabDropHelper

        this.projectTabs = projectTabs

        ParaTaskApp.logAttach("ProjectTab.attaching HalfTab")
        left.attached(this)
        ParaTaskApp.logAttach("ProjectTab.attached HalfTab")
    }

    override fun detaching() {
        tabDropHelper = null
        this.projectTabs = projectTabs

        ParaTaskApp.logAttach("ProjectTab.detaching left HalfTab")
        left.detaching()
        ParaTaskApp.logAttach("ProjectTab.detached left HalfTab")

        right?.let {
            ParaTaskApp.logAttach("ProjectTab.detaching right HalfTab")
            it.detaching()
            ParaTaskApp.logAttach("ProjectTab.detached right HalfTab")
        }
        splitPane.left = null
        splitPane.right = null
    }

    override fun close() {
        projectTabs.removeTab(this)
    }

    internal fun selected() {
        left.toolPane.selected()
    }

    internal fun deselected() {

    }

    override fun add(tool: Tool) {
        assert(right == null)

        val r = HalfTab_Impl(ToolPane_Impl(tool))

        splitPane.right = r
        r.attached(this)
        right = r
    }

    override fun remove(toolPane: ToolPane) {
        when (toolPane) {

            left.toolPane -> {
                if (right == null) {
                    projectTabs.removeTab(this)
                    return
                } else {
                    left = right!! // Must be in the JavaFX thread, so this can never fail
                    tabDropHelper = left.toolPane.tool.tabDropHelper
                }
            }
            right?.toolPane -> {
                if (right == null) {
                    throw RuntimeException("Attempted to remove a toolPane not belonging to this tab")
                }
            }
            else -> {
                throw RuntimeException("Attempt to remove a toolPane not belonging to this tab.")
            }
        }
        splitPane.right = null
        splitPane.left = left as Node
        right = null
        updateTab()
    }

    override fun split(tool: Tool, run: Boolean) {
        if (right == null) {
            add(tool)
        }
        if (run) {
            try {
                tool.check()
                tool.taskRunner.run()
            } catch (e: Exception) {
            }
        }
    }

    override fun split() {
        split(left.toolPane.tool.copy())
    }

    override fun splitToggle() {

        right?.let {
            remove(it.toolPane)
            return
        }

        split()
    }

    override fun duplicateTab() {
        val newTab = tabs.addTool(left.toolPane.tool.copy())

        right?.let { newTab.add(it.toolPane.tool.copy()) }
    }

    override fun changed() {
        updateTab()
        projectTabs.projectWindow.toolChanged(left.toolPane.tool)
    }

    override fun tearOffTab(event: MouseEvent) {

        val screenX = event.screenX
        val screenY = event.screenY

        var projectWindow: ProjectWindow? = null

        for (stage in StageHelper.getStages()) {
            val window = stage.scene.window
            if (stage.scene.userData is ProjectWindow) {
                if (screenX >= window.x && screenY >= window.y && screenX <= window.x + window.width && screenY <= window.y + window.height) {
                    projectWindow = stage.scene.userData as ProjectWindow
                    stage.toFront()
                    break
                }
            }
        }
        if (projectWindow == null) {
            projectWindow = ProjectWindow()
            projectWindow.placeOnStage(Stage())
        }
        val projectTab = projectWindow.addTool(left.toolPane.tool.copy())
        right?.let {
            projectTab.split(it.toolPane.tool.copy())
        }

        close()
    }

    fun onEditTabProperties() {
        TaskPrompter(tabProperties).placeOnStage(Stage())
    }

    inner class TabProperties : AbstractTask() {
        override val taskD = TaskDescription("tabProperties")

        val tabTemplateP = StringParameter("tabTemplate")
        val shortcutP = ShortcutParameter("shortcut")

        init {
            taskD.addParameters(tabTemplateP, shortcutP)
            tabTemplateP.listen { titleListener.update() }
        }

        override fun run() {
        }
    }

    /**
     * Listens to a StringProperty, and changes the tab's title based on the titleTemplate.
     */
    inner class TitleListener : ChangeListener<String> {
        var prop: StringProperty? = null

        override fun changed(observable: ObservableValue<out String>?, oldValue: String?, newValue: String?) {
            update()
        }

        fun update() {
            prop?.get()?.let {
                text = MessageFormat.format(tabTemplate, it)
            }
        }

        fun listen(property: StringProperty) {
            prop?.removeListener(this)
            prop = property
            property.addListener(this)
            update()
        }
    }
}
