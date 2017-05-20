package uk.co.nickthecoder.paratask.project

interface Results : uk.co.nickthecoder.paratask.util.Labelled {

    val tool: uk.co.nickthecoder.paratask.Tool

    val node: javafx.scene.Node

    val labelProperty: javafx.beans.property.StringProperty

    fun attached(toolPane: ToolPane)

    fun detaching()

    fun selected()

    fun deselected()
}
