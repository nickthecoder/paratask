package uk.co.nickthecoder.paratask.project.option

import uk.co.nickthecoder.paratask.project.Tool

interface Option {

    var code: String

    var label: String

    var isRow: Boolean

    var isMultiple: Boolean

    var refresh: Boolean

    var newTab: Boolean

    var prompt: Boolean

    var script: String

    fun run(tool: Tool, row: Any): Any?

    fun runNonRow(tool: Tool): Any?

    fun runMultiple(tool: Tool, list: List<Any>): Any?
}