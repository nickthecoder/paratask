package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.util.uncamel

class Column<R>(
        val name: String,
        val type: Class<*>,
        val label: String = name.uncamel(),
        val width: Int = 100,
        val access: (R) -> Any?
) {
    internal lateinit var parent: Columns<R>

    var tooltipColumn: Column<R>? = null


    fun tooltipFor(columnName: String): Column<R> {
        parent.findColumn(columnName)?.tooltip(name)
        return this
    }

    fun tooltip(columnName: String): Column<R> {
        tooltipColumn = parent.findColumn(columnName)
        return this
    }
}