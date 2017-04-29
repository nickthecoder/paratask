package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node
import uk.co.nickthecoder.paratask.gui.ParameterField

interface WrappableField {

    fun wrap(parameterField: ParameterField): Node

}