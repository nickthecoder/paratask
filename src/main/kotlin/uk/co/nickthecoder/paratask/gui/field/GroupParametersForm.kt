package uk.co.nickthecoder.paratask.gui.field

import javafx.scene.Node
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.Parameter
import uk.co.nickthecoder.paratask.gui.field.WrappableField

/**
 * This is the Field created by GroupParameter and is also use in TaskForm to prompt a whole Task.
 */
open class GroupParametersForm(var groupParameter: GroupParameter)
    : ParametersForm(groupParameter) {


}
