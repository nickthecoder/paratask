package uk.co.nickthecoder.paratask.project.table

import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.project.AbstractTool

abstract class AbstractTableTool<R : Any>() : AbstractTool(), TableTool<R> {

    override val columns = mutableListOf<Column<R, *>>()

    var list = mutableListOf<R>()

    fun resultsName(): String = "Results"

    abstract fun createColumns()

    override open fun createResults(): List<Results> {
        columns.clear()
        createColumns()
        return listOf<TableResults<R>>(TableResults<R>(this, list, resultsName()))
    }

}