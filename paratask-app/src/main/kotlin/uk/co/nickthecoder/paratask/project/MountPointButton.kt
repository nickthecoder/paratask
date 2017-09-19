package uk.co.nickthecoder.paratask.project

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.tools.places.MountTool

class MountPointButton(
        projectWindow: ProjectWindow,
        val mountPoint: MountTool.MountPoint,
        labelOption: LabelOption)

    : PlaceButton(projectWindow, mountPoint) {

    enum class LabelOption { DIRECTORY_PATH, DIRECTORY_NAME, LABEL }

    init {
        graphic = ImageView(mountPoint.icon)
        when (labelOption) {
            LabelOption.DIRECTORY_PATH -> text = mountPoint.directory.path
            LabelOption.DIRECTORY_NAME -> text = mountPoint.directory.name
            LabelOption.LABEL -> text = mountPoint.label
        }
    }
}
