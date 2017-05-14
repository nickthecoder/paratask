package uk.co.nickthecoder.paratask.project.table

import javafx.scene.control.TableRow
import uk.co.nickthecoder.paratask.project.Tool

interface TableTool<R> : Tool {

    val columns: List<Column<R, *>>

    fun createRow(): TableRow<WrappedRow<R>>
}