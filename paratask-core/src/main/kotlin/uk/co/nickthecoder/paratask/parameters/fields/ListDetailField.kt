package uk.co.nickthecoder.paratask.parameters.fields

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.parameters.*

class ListDetailField<T>(
        val multipleParameter: MultipleParameter<T>,
        val info: MultipleParameter.ListDetailsInfo<T>)

    : ParameterField(multipleParameter), FieldParent {

    val borderPane = BorderPane()

    val list = ListView<ListData>()

    val detailsContainer = StackPane()

    val buttons = FlowPane()

    val addButton = Button("Add")

    val removeButton = Button("Remove")

    val upButton = Button("Move Up")

    val downButton = Button("Move Down")

    override fun createControl(): Node {

        with(buttons) {
            styleClass.addAll("buttons", "left")
            children.addAll(addButton, removeButton)
            if (info.allowReordering) {
                children.addAll(upButton, downButton)
            }
        }

        with(detailsContainer) {
            styleClass.add("multiple-details")
        }

        addButton.onAction = EventHandler { onAdd() }
        removeButton.onAction = EventHandler { onRemove() }
        upButton.onAction = EventHandler { onMoveUp() }
        downButton.onAction = EventHandler { onMoveDown() }

        buildList()

        with(list) {
            selectionModel.selectionMode = SelectionMode.MULTIPLE
            prefHeight = info.height.toDouble()
            selectionModel.selectedItemProperty().addListener { _, _, selectedData ->
                selectionChanged(selectedData)
            }
        }
        selectionChanged(list.selectionModel.selectedItem)

        with(borderPane) {
            left = list
            center = detailsContainer
            bottom = buttons
        }

        return borderPane
    }

    fun buildList() {
        list.items.clear()
        multipleParameter.innerParameters.forEach { innerP ->
            addItemToList(innerP)
        }
        list.items.firstOrNull()?.let {
            list.selectionModel.select(it)
        }
    }

    fun addItemToList(innerP: ValueParameter<T>): ListData {
        // TODO Consider caching fields, so that rebuilding the list doesn't create new fields.
        val field = innerP.createField()
        field.fieldParent = this
        val data = ListData(field)
        list.items.add(data)
        return data
    }

    fun selectionChanged(selectedItem: ListData?) {
        detailsContainer.children.clear()
        val index = list.selectionModel.selectedIndex
        if (selectedItem != null) {
            detailsContainer.children.add(selectedItem.field.controlContainer)
        }
        removeButton.isDisable = selectedItem == null
    }

    override fun updateError(field: ParameterField) {
        // I don't THINK I need to do anything here.
    }

    override fun updateField(field: ParameterField) {
        // I don't THINK I need to do anything here.
    }

    override fun iterator(): Iterator<ParameterField> {
        return list.items.map { it.field }.iterator()
    }

    /**
     * Prevent us from rebuilding the list when a structural event is fired because of this.
     */
    var ignoreStructuralChanges: Boolean = false

    fun onAdd() {
        ignoreStructuralChanges = true
        try {
            val innerP = multipleParameter.newValue()
            list.selectionModel.select(addItemToList(innerP))
        } finally {
            ignoreStructuralChanges = false
        }
    }

    fun onRemove() {
        ignoreStructuralChanges = true
        try {
            var index = list.selectionModel.selectedIndex
            while (index >= 0) {
                list.selectionModel.clearSelection(index)
                println("Removing item @ $index")
                multipleParameter.removeAt(index)
                list.items.removeAt(index)
                index = list.selectionModel.selectedIndex
            }
        } finally {
            ignoreStructuralChanges = false
        }
    }

    fun onMoveUp() {
        // We use this to prevent one item "overtaking" another when moving multiple items up.
        var previousPosition = -1

        val newSelection = mutableListOf<Int>()

        // NOTE. we need to create a NEW list, because otherwise we get exceptions when removing items.
        val indicies = list.selectionModel.selectedIndices.toMutableList().sorted()
        indicies.forEach { position ->
            if (position > previousPosition + 1) {
                val item = list.items[position]
                list.items.removeAt(position)
                list.items.add(position - 1, item)
                previousPosition = position - 1
                newSelection.add(position - 1)
            } else {
                previousPosition = position
                newSelection.add(position)
            }
            list.selectionModel.clearSelection()
            newSelection.forEach { list.selectionModel.select(it) }
        }
    }

    fun onMoveDown() {
        // We use this to prevent one item "overtaking" another when moving multiple items up.
        var previousPosition = list.items.size

        val newSelection = mutableListOf<Int>()

        // NOTE. we need to create a NEW list, because otherwise we get exceptions when removing items.
        val indicies = list.selectionModel.selectedIndices.toMutableList().sortedDescending()
        indicies.forEach { position ->
            if (position < previousPosition - 1) {
                val item = list.items[position]
                list.items.removeAt(position)
                list.items.add(position + 1, item)
                previousPosition = position + 1
                newSelection.add(position + 1)
            } else {
                previousPosition = position
                newSelection.add(position)
            }
            list.selectionModel.clearSelection()
            newSelection.forEach { list.selectionModel.select(it) }
        }
    }

    override fun parameterChanged(event: ParameterEvent) {
        super.parameterChanged(event)
        if (!ignoreStructuralChanges && event.type == ParameterEventType.STRUCTURAL) {
            buildList()
        }
    }


    inner class ListData(val field: ParameterField) : ParameterListener {

        var label = label()

        init {
            field.parameter.parameterListeners.add(this)
        }

        fun label(): String {
            @Suppress("UNCHECKED_CAST")
            return info.labelFactory((field.parameter as ValueParameter<T>).value)
        }

        /**
         * Listen for changes, and if the text in the list needs updating, then
         * remove the item from the list and add it back again.
         * Make sure the item is selected if it was selected before this operation.
         */
        override fun parameterChanged(event: ParameterEvent) {

            if (event.type == ParameterEventType.INNER || event.type == ParameterEventType.VALUE) {
                val newLabel = label()
                if (newLabel != label) {
                    label = newLabel

                    val index = list.items.indexOf(this)
                    val wasSelected = list.selectionModel.isSelected(index)
                    list.items.removeAt(index)
                    list.items.add(index, this)
                    if (wasSelected) {
                        list.selectionModel.select(index)
                    }
                }
            }
        }

        override fun toString() = label
    }
}

