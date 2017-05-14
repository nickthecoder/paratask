package uk.co.nickthecoder.paratask.project.table

import uk.co.nickthecoder.paratask.project.Tool

interface TableTool<R> : Tool {

    val columns : List<Column<R, *>>

}