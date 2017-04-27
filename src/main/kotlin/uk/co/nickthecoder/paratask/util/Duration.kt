package uk.co.nickthecoder.paratask.util

import java.util.concurrent.TimeUnit

class Duration(val time: Long, val unit: TimeUnit) {

    val millis = unit.toMillis(time)

}