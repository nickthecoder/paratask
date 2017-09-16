package uk.co.nickthecoder.paratask.parameters.fields

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import uk.co.nickthecoder.paratask.parameters.GroupParameter
import uk.co.nickthecoder.paratask.parameters.ParameterListener


class GridGroupField(
        groupParameter: GroupParameter,
        val labelsAbove: Boolean,
        val columns: Int = groupParameter.children.size,
        isBoxed: Boolean)

    : SingleErrorGroupField(groupParameter, isBoxed = isBoxed), FieldParent, ParameterListener {

    val grid = GridPane()

    val fieldSet = mutableListOf<ParameterField>()
    val containers = mutableListOf<Node>()

    override fun iterator(): Iterator<ParameterField> {
        return fieldSet.iterator()
    }

    override fun createContent(): Node {
        if (labelsAbove) {
            grid.styleClass.add("labels-above-grid-group")
        } else {
            grid.styleClass.add("grid-group")
        }

        val controlCC = ColumnConstraints()
        controlCC.hgrow = Priority.ALWAYS

        val labelCC = ColumnConstraints()

        val labelsAboveCC = ColumnConstraints()
        labelsAboveCC.hgrow = Priority.ALWAYS
        labelsAboveCC.halignment = HPos.CENTER

        val rowC = RowConstraints()
        rowC.valignment = VPos.CENTER

        var column = 0
        var row = 0
        groupParameter.children.forEach { child ->

            val field = child.createField()
            field.fieldParent = this

            if (labelsAbove) {
                field.label.styleClass?.add("small-bottom-pad")
                if (row != groupParameter.children.size / columns - 1) {
                    field.controlContainer?.styleClass?.add("bottom-pad")
                }

                grid.add(field.label, column, row * 2)
                grid.add(field.controlContainer, column, row * 2 + 1)
                if (row == 0) {
                    grid.columnConstraints.add(labelsAboveCC)
                }
            } else {
                if (row == 0) {
                    grid.columnConstraints.add(labelCC)
                    grid.columnConstraints.add(controlCC)
                }

                if (column == 0) {
                    grid.rowConstraints.add(rowC)
                }
                grid.add(field.label, column * 2, row)
                grid.add(field.controlContainer, column * 2 + 1, row)
            }

            fieldSet.add(field)
            //containers.add(container)

            column++
            if (column >= columns) {
                column = 0
                row++
            }
        }

        return grid
    }

    override fun updateField(field: ParameterField) {
        // Do nothing. When fields become visible/hidden, the layout doesn't change.
    }

}
