package uk.co.nickthecoder.paratask.options

abstract class AbstractOption(
        override var code: String = "",
        override var aliases: MutableList<String> = mutableListOf<String>(),
        override var label: String = "",
        override var isRow: Boolean = true,
        override var isMultiple: Boolean = false,
        override var newTab: Boolean = false,
        override var prompt: Boolean = false,
        override var refresh: Boolean = false
) : Option {

    protected fun copyTo(result: AbstractOption) {
        result.code = code
        result.aliases = ArrayList<String>(aliases)
        result.label = label
        result.isRow = isRow
        result.isMultiple = isMultiple
        result.newTab = newTab
        result.prompt = prompt
        result.refresh = refresh
    }
}
