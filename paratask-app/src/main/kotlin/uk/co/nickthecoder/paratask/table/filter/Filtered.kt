package uk.co.nickthecoder.paratask.table.filter

interface Filtered {

    val rowFilters: Map<String, RowFilter<*>>
        get() = emptyMap()

}