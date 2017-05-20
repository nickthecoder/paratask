package uk.co.nickthecoder.paratask.util.process

import java.io.InputStream

interface Sink : Runnable {

    fun setStream(stream: InputStream)
}