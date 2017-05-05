package uk.co.nickthecoder.paratask.util

import java.io.File

val homeDirectory: File = File(System.getProperty("user.home"))

fun File.child(vararg names: String): File {
    var f = this
    for (name in names) {
        f = File(f, name)
    }
    return f
}

fun File.nameWithoutExtension(): String {
    val lastDot = name.lastIndexOf('.')
    if (lastDot > 0) {
        return name.substring(0, lastDot)
    } else {
        return name
    }
}