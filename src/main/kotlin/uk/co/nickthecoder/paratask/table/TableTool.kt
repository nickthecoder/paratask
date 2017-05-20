package uk.co.nickthecoder.paratask.table

import javafx.scene.control.TableRow
import uk.co.nickthecoder.paratask.Tool

interface TableTool<R> : Tool {

    val columns: List<Column<R, *>>

    fun createRow(): TableRow<WrappedRow<R>>
}