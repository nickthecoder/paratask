package uk.co.nickthecoder.paratask.gui

/**
 * Used by the ScriptEditor, to show the user the properties that can be used within the script.
 * For example, when editing a RowFilter, the script has access to the "row" and the "tool", so in this case,
 * the map will contain two items.
 * Reflection can be used to show the user properties of those two object, as well as the object's methods.
 */
class ScriptVariables {

    val map = mutableMapOf<String, Class<*>>()

    fun add(name: String, type: Class<*>) {
        map[name] = type
    }

    fun add(name: String, className: String) {
        try {
            map[name] = Class.forName(className)
        } catch (e: Exception) {
            map.remove(name)
        }
    }
}
