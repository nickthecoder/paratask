package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node

interface DropHelper {

    fun applyTo(target: Node): AbstractDropHelper

    fun unapplyTo(target: Node)

    fun exclude(node: Node): AbstractDropHelper

    fun cancel()

}
