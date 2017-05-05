package uk.co.nickthecoder.paratask.project.option

import uk.co.nickthecoder.paratask.project.Tool

interface Option {

    val code: String

    val label: String

    var isRow: Boolean

    var isMultiple: Boolean

    var refresh: Boolean

    var newTab: Boolean

    var prompt: Boolean

    fun run(tool: Tool, row: Any): Any?

    fun runNonRow(tool: Tool): Any?

    // fun runMultiple(tool: Tool, list: List<Any?>): Any?
}