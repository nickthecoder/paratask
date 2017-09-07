package uk.co.nickthecoder.paratask.table

interface Filtered {

    val rowFilters: Map<String, RowFilter<*>>
        get() = emptyMap()

}