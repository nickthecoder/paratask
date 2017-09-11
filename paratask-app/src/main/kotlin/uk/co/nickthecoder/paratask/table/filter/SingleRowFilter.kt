package uk.co.nickthecoder.paratask.table.filter

import uk.co.nickthecoder.paratask.table.TableTool

interface SingleRowFilter<R : Any> : TableTool<R> {

    val rowFilter: RowFilter<R>

    override val rowFilters
        get() = mapOf(Pair("filter", rowFilter))
}
