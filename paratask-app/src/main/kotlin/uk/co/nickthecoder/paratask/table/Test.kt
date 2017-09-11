package uk.co.nickthecoder.paratask.table

import uk.co.nickthecoder.paratask.parameters.compound.IntRangeParameter
import java.io.File


interface Test {
    val label: String
    val bType: Class<*>?
    fun result(a: Any?, b: Any?): Boolean
    fun opposite(): Test
}

abstract class AbstractTest(override val label: String, val oppositeLabel: String) : Test {
    override fun opposite(): Test = NotTest(this, oppositeLabel)
}

class NullTest : AbstractTest("is null", "is not null") {
    override val bType = null
    override fun result(a: Any?, b: Any?) = a == null
}

class NotTest(val inner: Test, override val label: String) : Test {
    override val bType = inner.bType
    override fun result(a: Any?, b: Any?) = !inner.result(a, b)
    override fun opposite(): Test = inner
}

/**
 * Converts the A value to a string, then performs a string test upon it.
 */
class ToStringTest(val test: Test) : Test {
    override val label = test.label
    override val bType = test.bType
    override fun result(a: Any?, b: Any?) = test.result(a?.toString(), b)
    override fun opposite() = ToStringTest(test.opposite())
}


abstract class SafeTest<A, B>(label: String, oppositeLabel: String, val aClass: Class<*>, val bClass: Class<*>)
    : AbstractTest(label, oppositeLabel) {

    override val bType: Class<*>? = bClass

    override fun result(a: Any?, b: Any?): Boolean {
        if (a == null) {
            return false
        }
        if (b == null && bType !== Unit::class.java) {
            return false
        }

        if ((a::class.java === aClass) && (b == null || (b::class.java === bClass))) {
            @Suppress("UNCHECKED_CAST")
            return testResult(a as A, b as B)
        } else {
            return false
        }
    }

    override fun opposite(): Test = NotTest(this, oppositeLabel)

    abstract fun testResult(a: A, b: B): Boolean
}

/* --- BOOLEAN --- */

class BooleanEqualsTest : AbstractTest("==", "!=") {
    override val bType = java.lang.Boolean::class.java
    override fun result(a: Any?, b: Any?) = a == b
}

/* --- INT --- */

abstract class IntTest<B>(label: String, oppositeLabel: String, bClass: Class<*>)
    : SafeTest<Int, B>(label, oppositeLabel, java.lang.Integer::class.java, bClass)


class IntEqualsTest : IntTest<Int>("==", "!=", Int::class.java) {
    override fun testResult(a: Int, b: Int) = a == b
}

class IntLessThan : IntTest<Int>("<", ">=", Integer::class.java) {
    override fun testResult(a: Int, b: Int) = a < b
}

class IntGreaterThan : IntTest<Int>(">", "<=", Integer::class.java) {
    override fun testResult(a: Int, b: Int) = a > b
}

class IntWithin : IntTest<IntRangeParameter>("within range", "outside range", IntRangeParameter::class.java) {
    override fun testResult(a: Int, b: IntRangeParameter) = b.contains(a)
}

/* --- DOUBLE --- */

abstract class DoubleTest<B>(label: String, oppositeLabel: String, bClass: Class<*>)
    : SafeTest<Double, B>(label, oppositeLabel, java.lang.Double::class.java, bClass)


class DoubleEqualsTest : DoubleTest<Double>("==", "!=", java.lang.Double::class.java) {
    override fun testResult(a: Double, b: Double) = a == b
}

class DoubleLessThan : DoubleTest<Double>("<", ">=", java.lang.Double::class.java) {
    override fun testResult(a: Double, b: Double) = a < b
}

class DoubleGreaterThan : DoubleTest<Double>(">", "<=", java.lang.Double::class.java) {
    override fun testResult(a: Double, b: Double) = a > b
}

/* --- LONG --- */
// Note. We use a Double for the b value, because we don't have a LongParameter

abstract class LongTest<B>(label: String, oppositeLabel: String, bClass: Class<*>)
    : SafeTest<Long, B>(label, oppositeLabel, java.lang.Long::class.java, bClass)


class LongEqualsTest : LongTest<Double>("==", "!=", java.lang.Double::class.java) {
    override fun testResult(a: Long, b: Double) = a.toDouble() == b
}

class LongLessThan : LongTest<Double>("<", ">=", java.lang.Double::class.java) {
    override fun testResult(a: Long, b: Double) = a < b
}

class LongGreaterThan : LongTest<Double>(">", "<=", java.lang.Double::class.java) {
    override fun testResult(a: Long, b: Double) = a > b
}

/* --- STRING --- */

abstract class StringTest<B>(label: String, oppositeLabel: String, bClass: Class<*>)
    : SafeTest<String, B>(label, oppositeLabel, String::class.java, bClass)


class StringEqualsTest : StringTest<String>("==", "!=", String::class.java) {
    override fun testResult(a: String, b: String) = a == b
}

class StringLessThan : StringTest<String>("<", ">=", String::class.java) {
    override fun testResult(a: String, b: String) = a < b
}

class StringGreaterThan : StringTest<String>(">", "<=", String::class.java) {
    override fun testResult(a: String, b: String) = a > b
}

class StringContains : StringTest<String>("contains", "does not contain", String::class.java) {
    override fun testResult(a: String, b: String) = a.toLowerCase().contains(b.toLowerCase())
}

class StringStartsWith : StringTest<String>("starts with", "does not start with", String::class.java) {
    override fun testResult(a: String, b: String) = a.toLowerCase().startsWith(b.toLowerCase())
}

class StringEndsWith : StringTest<String>("ends with", "does not end with", String::class.java) {
    override fun testResult(a: String, b: String) = a.toLowerCase().endsWith(b.toLowerCase())
}

class StringMatches : StringTest<Regex>("matches", "does not match", Regex::class.java) {
    override fun testResult(a: String, b: Regex) = b.matches(a)
}

/* --- FILE --- */

abstract class FileTest<B>(label: String, oppositeLabel: String, bClass: Class<*>)
    : SafeTest<File, B>(label, oppositeLabel, File::class.java, bClass)

class FileExits : FileTest<Any?>("file exists", "file does not exist", Unit::class.java) {
    override fun testResult(a: File, b: Any?) = a.exists()
}

class FileIsFile : FileTest<Any?>("is a file", "is not a file", Unit::class.java) {
    override fun testResult(a: File, b: Any?) = a.isFile()
}

class FileIsDirectory : FileTest<Any?>("is a directory", "is not a directory", Unit::class.java) {
    override fun testResult(a: File, b: Any?) = a.isDirectory()
}

