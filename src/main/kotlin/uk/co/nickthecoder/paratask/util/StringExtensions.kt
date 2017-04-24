package uk.co.nickthecoder.paratask.util


/**
 * Convert a camel-case string to words separated by spaces. The first word can optionally be
 * capitalised.
 */
fun String.uncamel(space: String = " ", upperFirst: Boolean = true): String {

    val result = StringBuilder()
    var wasUpper: Boolean = false

    var first: Boolean = upperFirst
    for (i in 0..length-1) {
        val c = get(i)
        if (first) {
            result.append(Character.toUpperCase(c))
            first = false;
        } else {
            if (Character.isUpperCase(c) && !wasUpper) {
                result.append(space);
            }
            result.append(c);
        }
        wasUpper = Character.isUpperCase(c);
    }
    return result.toString()
}
