package uk.co.nickthecoder.paratask.project

/**
 * R is the type of a single row of data.
 */
class Columns<R> {

    val columns = mutableListOf<Column<R>>()

    fun add(column: Column<R>): Column<R> {
        columns.add(column)
        column.parent = this
        return column
    }

    fun findColumn(name: String): Column<R>? {
        columns.forEach {
            if (it.name == name) {
                return it
            }
        }
        return null
    }
}