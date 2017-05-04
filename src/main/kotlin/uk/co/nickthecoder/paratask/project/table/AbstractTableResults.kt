package uk.co.nickthecoder.paratask.project.table

import com.sun.javafx.collections.ImmutableObservableList
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import uk.co.nickthecoder.paratask.gui.project.ToolPane

abstract class AbstractTableResults<R>(val list: List<R>) : TableResults<R> {

    val items = WrappedList<R>(list)

    override val tableView: TableView<WrappedRow<R>> = TableView()

    override val columns = mutableListOf<Column<R, *>>()

    override val node = tableView

    override fun attached(toolPane: ToolPane) {

        val optionColumn = TableColumn<WrappedRow<R>, String>("")
        optionColumn.setCellValueFactory { p -> p.getValue().optionProperty }
        tableView.getColumns().add(optionColumn)

        for (column in columns) {
            tableView.getColumns().add(column)
        }

        tableView.setItems(items)
    }

    override fun detaching() {}

}

class WrappedList<R>(list: List<R>) :
        ImmutableObservableList<WrappedRow<R>>() {

    val list = list.map { row: R -> WrappedRow(row) }

    override fun get(index: Int): WrappedRow<R> = list[index]

    override val size = list.size
}
