package uk.co.nickthecoder.paratask.gui

class CompoundDragHelper(vararg helpers: DragHelper<*>) {

    private val dragHelpers: List<DragHelper<*>>

    init {
        dragHelpers = helpers.asList<DragHelper<*>>()
    }


}
