package uk.co.nickthecoder.paratask.table.filter

import uk.co.nickthecoder.paratask.table.filter.RowFilter

interface Filtered {

    val rowFilters: Map<String, RowFilter<*>>
        get() = emptyMap()

}