package uk.co.nickthecoder.paratask.util

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

interface Sink : Runnable {

    fun setStream(stream: InputStream)
}