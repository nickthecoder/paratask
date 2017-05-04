package uk.co.nickthecoder.paratask.project.table

import javafx.scene.control.TableView
import uk.co.nickthecoder.paratask.gui.project.Results

interface TableResults<R> : Results {

    val tableView: TableView<WrappedRow<R>>

    val columns: List<Column<R,*>>

}
