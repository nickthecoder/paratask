/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.paratask.table.filter

import uk.co.nickthecoder.paratask.parameters.compound.IntRangeParameter
import uk.co.nickthecoder.paratask.util.Resource
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAmount


interface Test {
    val bClass: Class<*>?
    val label: String
    fun result(a: Any?, b: Any?): Boolean
    fun opposite(): Test
}

abstract class AbstractTest(override val label: String, val oppositeLabel: String) : Test {
    override fun opposite(): Test = NotTest(this, oppositeLabel)
}

class NullTest : AbstractTest("is null", "is not null") {
    override val bClass = null
    override fun result(a: Any?, b: Any?) = a == null
}

class NotTest(val inner: Test, override val label: String) : Test {
    override val bClass
        get() = inner.bClass

    override fun result(a: Any?, b: Any?) = !inner.result(a, b)
    override fun opposite(): Test = inner
}

/**
 * Converts the A value to a string, then performs a string test upon it.
 */
class ToStringTest(val test: Test) : Test {
    override val bClass = String::class.java
    override val label = test.label
    override fun result(a: Any?, b: Any?) = test.result(a?.toString(), b)
    override fun opposite() = ToStringTest(test.opposite())
}


abstract class SafeTest<in A, in B>(label: String, oppositeLabel: String, val aClass: Class<*>, override val bClass: Class<*>)
    : AbstractTest(label, oppositeLabel) {

    override fun result(a: Any?, b: Any?): Boolean {

        if (a == null || b == null) {
            //println("Null. a = $a b = $b")
            return false
        }

        if (!aClass.isInstance(a)) {
            //println("Wrong A type ${a::class.java} vs $aClass")
            return false
        }

        if (!bClass.isInstance(b)) {
            //println("Wrong B type ${b::class.java} vs $bClass")
        }

        @Suppress("UNCHECKED_CAST")
        return testResult(a as A, b as B)
    }

    abstract fun testResult(a: A, b: B): Boolean
}

abstract class UnarySafeTest<in A>(label: String, oppositeLabel: String, val aClass: Class<*>)
    : AbstractTest(label, oppositeLabel) {

    override val bClass = null

    override fun result(a: Any?, b: Any?): Boolean {

        if (a == null) {
            //println("Null. a = $a")
            return false
        }
        if (b != null) {
            //println("Expected b to be null. Ignoring $b")
        }

        if (!aClass.isInstance(a)) {
            //println("Wrong A type ${a::class.java} vs $aClass")
            return false
        }

        @Suppress("UNCHECKED_CAST")
        return testResult(a as A)
    }

    abstract fun testResult(a: A): Boolean
}

/* --- BOOLEAN --- */

class BooleanEqualsTest : AbstractTest("==", "!=") {
    override val bClass = java.lang.Boolean::class.java
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

abstract class FileUnaryTest(label: String, oppositeLabel: String)
    : UnarySafeTest<File>(label, oppositeLabel, File::class.java)

class FileExits : FileUnaryTest("file exists", "file does not exist") {
    override fun testResult(a: File) = a.exists()
}

class FileIsFile : FileUnaryTest("is a file", "not a file") {
    override fun testResult(a: File) = a.isFile
}

class FileIsDirectory : FileUnaryTest("is a directory", "not a directory") {
    override fun testResult(a: File) = a.isDirectory
}

/* --- RESOURCE --- */

abstract class ResourceUnaryTest(label: String, oppositeLabel: String)
    : UnarySafeTest<Resource>(label, oppositeLabel, Resource::class.java)

class ResourceIsFile : ResourceUnaryTest("is a file", "not a file") {
    override fun testResult(a: Resource) = a.isFile()
}

class ResourceIsDirectory : ResourceUnaryTest("is a directory", "not a directory") {
    override fun testResult(a: Resource) = a.isDirectory()
}

class ResourceIsFileOrDirectory : ResourceUnaryTest("is a file or directory", "not a file nor directory") {
    override fun testResult(a: Resource) = a.isFileOrDirectory()
}


/* --- LOCALDATE --- */

abstract class LocalDateTest<B>(label: String, oppositeLabel: String, bClass: Class<*>)
    : SafeTest<LocalDate, B>(label, oppositeLabel, LocalDate::class.java, bClass)

class LocalDateEqualsTest : LocalDateTest<LocalDate>("same date", "!=", LocalDate::class.java) {
    override fun testResult(a: LocalDate, b: LocalDate) = a == b
}

class LocalDateBefore : LocalDateTest<LocalDate>("before", "not before", LocalDate::class.java) {
    override fun testResult(a: LocalDate, b: LocalDate) = a < b
}

class LocalDateAfter : LocalDateTest<LocalDate>("after", "not after", LocalDate::class.java) {
    override fun testResult(a: LocalDate, b: LocalDate) = a > b
}


/* --- LOCALDATETIME --- */

abstract class LocalDateTimeTest<B>(label: String, oppositeLabel: String, bClass: Class<*>)
    : SafeTest<LocalDateTime, B>(label, oppositeLabel, LocalDateTime::class.java, bClass)

class LocalDateTimeEarlierThan : LocalDateTimeTest<TemporalAmount>("earlier than … ago", "later than … ago", TemporalAmount::class.java) {
    override fun testResult(a: LocalDateTime, b: TemporalAmount) = a < LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()) - b
}

class LocalDateTimeBefore : LocalDateTimeTest<LocalDate>("on or after", "before", LocalDate::class.java) {
    override fun testResult(a: LocalDateTime, b: LocalDate) = a >= b.atStartOfDay()
}

class LocalDateTimeOn : LocalDateTimeTest<LocalDate>("date ==", "date !=", LocalDate::class.java) {
    override fun testResult(a: LocalDateTime, b: LocalDate) = a.toLocalDate() == b
}
