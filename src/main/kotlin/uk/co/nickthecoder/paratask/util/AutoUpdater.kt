package uk.co.nickthecoder.paratask.util

class AutoUpdater(val name: String, val action: () -> Unit) : Updater {

    init {
        UpdateManager.add(this)
        UpdateManager.ensureStarted()
    }

    override fun update(): Boolean {
        try {
            action()
            return true;
        } catch (e: Exception) {
            return false;
        }
    }

    override fun toString(): String = "AutoUpdater : $name"
}
