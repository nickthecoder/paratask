package uk.co.nickthecoder.paratask.project

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.tools.places.MountTool

class MountPointButton(projectWindow: ProjectWindow, val mountPoint: MountTool.MountPoint) : PlaceButton(projectWindow, mountPoint) {

    init {
        graphic = ImageView(mountPoint.icon)
    }
}
