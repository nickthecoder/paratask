package uk.co.nickthecoder.paratask.util

import javafx.application.Platform
import java.lang.ref.WeakReference
import java.util.Timer
import java.util.TimerTask

object UpdateManager : TimerTask() {

    val period: Long = 500

    private val updaters = mutableSetOf<WeakReference<Updater>>()

    private val pendingRemoval = mutableSetOf<Updater>()

    private val pendingAdditions = mutableSetOf<Updater>()

    private val weakPendingRemoval = mutableSetOf<WeakReference<Updater>>()

    private var timer: Timer? = null

    override fun run() {

        pendingAdditions.forEach { updater ->
            updaters.add(WeakReference <Updater>(updater));
        }
        pendingAdditions.clear();

        updaters.forEach { weakUpdater ->
            val updater = weakUpdater.get()

            if (updater == null) {
                weakPendingRemoval.add(weakUpdater)
            } else {
                if (pendingRemoval.contains(updater)) {
                    pendingRemoval.remove(updater)
                    weakPendingRemoval.add(weakUpdater)
                } else {
                    try {
                        updater.update()
                    } catch (e: Exception) {
                        weakPendingRemoval.add(weakUpdater)
                    }
                }
            }
        }
        updaters.removeAll(weakPendingRemoval)
        weakPendingRemoval.clear();
    }

    fun ensureStarted() {
        if (timer == null) {
            start()
        }
    }

    fun start() {
        if (timer != null) {
            throw RuntimeException("Timer already started");
        }

        timer = Timer()
        timer?.schedule(this, 0L, period)
    }

    private fun stop() {
        timer?.cancel()
        timer = null
    }

    fun add(updater: Updater) {
        pendingAdditions.add(updater);
    }

    fun remove(updater: Updater) {
        pendingRemoval.add(updater);
    }

    fun dump() {
        println("ComponentUpdateManager Dump");
        updaters.forEach { weakUpdater ->
            val updater = weakUpdater.get();
            if (updater != null) {
                println(updater);
            }
        }

    }

}