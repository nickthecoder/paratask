package uk.co.nickthecoder.paratask.gui

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.parameter.Parameter

class Boxed(
        title: String,
        content: Node)

    : StackPane() {

    init {

        val label = Label(title);
        label.getStyleClass().add("boxed-title");
        StackPane.setAlignment(label, Pos.TOP_LEFT);

        val contentPane = StackPane()
        content.getStyleClass().add("boxed-content")
        contentPane.getChildren().add(content);

        getStyleClass().add("boxed-border");
        getChildren().addAll(label, contentPane);
    }
}