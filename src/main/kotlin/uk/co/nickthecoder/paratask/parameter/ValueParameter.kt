package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import uk.co.nickthecoder.paratask.ParameterException

/**
 * The base class for all Parameters, which can hold a value.
 * A ValueParameter can be locked, which prevents its values from being changed.
 * If you attempt to change its value while locked, a ParameterException is thrown.
 * <p>
 * Locking is useful to ensure that a Task's parameters do not change after they have been checked, and before the
 * the Task has finished using them. This issue only occurs in a multi-threading environment, but even a simple
 * command line tool is multi-threaded when the Task is prompted. (The GUI will run in a separate thread from the Task's).
 * </p>
 */
abstract class ValueParameter<T : Value<*>>(name: String, var required: Boolean = false)
    : AbstractParameter<T>(name) {

}
