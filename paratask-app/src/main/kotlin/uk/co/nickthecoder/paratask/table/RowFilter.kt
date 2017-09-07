package uk.co.nickthecoder.paratask.table

import groovy.lang.Binding
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.options.GroovyScript
import uk.co.nickthecoder.paratask.parameters.*


class RowFilter<R>(val tool: Tool, val columns: List<Column<R, *>>, val exampleRow: R, val label: String = "Filter")
    : AbstractTask() {

    companion object {
        val intTests: List<Test> = listOf(IntEqualsTest(), IntGreaterThan(), IntLessThan())
        val doubleTests: List<Test> = listOf(DoubleEqualsTest(), DoubleGreaterThan(), DoubleLessThan())
        val stringTests: List<Test> = listOf(StringEqualsTest(), StringGreaterThan(), StringLessThan(), StringContains(), StringStartsWith(), StringEndsWith())
        val charTests: List<Test> = listOf(StringEqualsTest(), StringGreaterThan(), StringLessThan())
        val booleanTests: List<Test> = listOf(BooleanEqualsTest())

        val nullTest = NullTest()
        val notNullTest = NullTest().opposite()
    }

    override val taskD = TaskDescription("filter", description = "Filter Rows")

    val ignoreFiltersP = BooleanParameter("ignoreFilters", value = false)

    val groovyScriptP = StringParameter("groovyScript", required = false, rows = 5)

    val andP = BooleanParameter("and", label = "", value = true)

    val conditionsP = MultipleParameter("conditions") { Condition() }


    var groovyScript: GroovyScript? = null

    init {
        andP.asComboBox("AND", "OR")

        taskD.addParameters(ignoreFiltersP, groovyScriptP, andP, conditionsP)
        groovyScriptP.listen {
            groovyScript = if (groovyScriptP.value.isBlank()) {
                null
            } else {
                GroovyScript(groovyScriptP.value)
            }
        }
    }

    override fun run() {
        tool.toolPane?.parametersPane?.run()
    }

    fun accept(row: R): Boolean {
        if (ignoreFiltersP.value == true) {
            return true
        }

        if (andP.value == true) {
            if (acceptConditions(row) == false) {
                return false
            }
        } else if (acceptConditions(row) == true) {
            return true
        }

        return acceptGroovyScript(row)
    }

    fun acceptConditions(row: R): Boolean {

        if (conditionsP.value.isEmpty()) {
            return true
        } else {
            conditionsP.innerParameters.filterIsInstance<Condition>().forEach { condition ->

                if (andP.value == true) {
                    if (!condition.accept(row)) {
                        return false
                    }
                } else if (andP.value == false) {
                    if (condition.accept(row)) {
                        return true
                    }
                }
            }
            // We have tested all conditions, and the none of them have returned,.
            // If we are AND-ing, then none returned false, so return true.
            // If we are OR-ing, then none returned true, so return false.
            return andP.value == true
        }
    }

    fun acceptGroovyScript(row: R): Boolean {
        try {
            groovyScript?.let {
                script ->
                val bindings = Binding()
                bindings.setVariable("row", row)
                val result = script.run(bindings)
                if (result == false) {
                    return false
                } else if (result == true) {
                    return true
                }
                // If a non-boolean value is returned, then ignore it.
            }
        } catch (e: Exception) {
            println("Groovy Row Filter faild : $e")
        }
        return andP.value == true
    }

    fun exampleValue(column: Column<R, *>?): Any? {
        column ?: return null
        return column.getter(exampleRow)
    }

    inner class Condition : CompoundParameter("condition") {

        val columnP = ChoiceParameter<Column<R, *>?>("column", value = null, required = true)
        val testP = ChoiceParameter<Test?>("test", value = null, required = true)
        val booleanValueP = BooleanParameter("booleanValue", label = "Value", required = false)
        val intValueP = IntParameter("intValue", label = "Value")
        val doubleValueP = DoubleParameter("doubleValue", label = "Value")
        val stringValueP = StringParameter("stringValue", label = "Value")

        init {
            addParameters(columnP, testP, booleanValueP, intValueP, doubleValueP, stringValueP)

            booleanValueP.hidden = true
            booleanValueP.asComboBox()
            intValueP.hidden = true
            doubleValueP.hidden = true
            stringValueP.hidden = true

            columns.forEach { column ->
                columnP.addChoice(column.name, column, column.name)
            }

            columnP.listen { columnPChanged() }
            testP.listen { testPChanged() }
        }

        fun testChoices(tests: List<Test>) {
            testP.clear()
            tests.forEach { test ->
                testP.addChoice(test.label, test, test.label)
                val opposite = test.opposite()
                testP.addChoice(opposite.label, opposite, opposite.label)
            }
            testP.addChoice(nullTest.label, nullTest, nullTest.label)
            testP.addChoice(notNullTest.label, notNullTest, notNullTest.label)

            if (!testP.choiceValues().contains(testP.value)) {
                testP.value = tests.firstOrNull()
            }
        }

        fun columnPChanged() {
            val exampleValue = exampleValue(columnP.value)
            val columnType: Class<*>? = exampleValue?.javaClass

            when (columnType) {
            // Make sure that "java.lang.Boolean" AND "boolean" are both handled.
                Boolean::class.java -> testChoices(booleanTests)
                java.lang.Boolean::class.java -> testChoices(booleanTests)

                Char::class.java -> testChoices(charTests)
                java.lang.Character::class.java -> testChoices(charTests)

            // We don't seem to have the same problem with "java.lang.Integer" and "int". Not sure why!
                Int::class.java -> testChoices(intTests)
                Double::class.java -> testChoices(doubleTests)
                String::class.java -> testChoices(stringTests)
                else -> testChoices(listOf())
            }
        }

        fun testPChanged() {
            val bType = testP.value?.bType
            booleanValueP.hidden = (bType != Boolean::class.java) && (bType != java.lang.Boolean::class.java)
            intValueP.hidden = bType != Int::class.java
            doubleValueP.hidden = bType != Double::class.java
            stringValueP.hidden = bType != String::class.java
        }

        fun accept(row: R): Boolean {
            val a = columnP.value?.let { it.getter(row) }
            val bType = testP.value?.bType
            val b: Any? = when (bType) {
                Boolean::class.java -> {
                    booleanValueP.value!!
                }
                Int::class.java -> {
                    intValueP.value!!
                }
                Double::class.java -> {
                    doubleValueP.value!!
                }
                String::class.java -> {
                    stringValueP.value
                }

                else -> {
                    null
                }
            }

            return testP.value!!.accept(a, b)
        }
    }


    interface Test {
        val label: String
        val bType: Class<*>?
        fun accept(a: Any?, b: Any?): Boolean
        fun opposite(): Test
    }

    interface BooleanBTest : Test {
        override val bType
            get() = Boolean::class.java
    }

    interface IntBTest : Test {
        override val bType
            get() = Int::class.java
    }

    interface DoubleBTest : Test {
        override val bType
            get() = Double::class.java
    }

    interface StringBTest : Test {
        override val bType
            get() = String::class.java
    }

    abstract class EqualsTest : Test {
        override val label = "=="
        override fun accept(a: Any?, b: Any?): Boolean {
            if (a is Char && b is String) {
                return a.toString() == b
            }
            return a == b
        }

        override fun opposite(): Test = NotTest(this, "!=")
    }

    class NullTest : Test {
        override val label = "is null"
        override val bType = null
        override fun accept(a: Any?, b: Any?): Boolean = a == null
        override fun opposite(): Test = NotTest(this, "is not null")
    }

    class NotTest(val inner: Test, override val label: String) : Test {
        override val bType = inner.bType
        override fun accept(a: Any?, b: Any?) = !inner.accept(a, b)
        override fun opposite(): Test = inner
    }

    class BooleanEqualsTest : EqualsTest(), BooleanBTest

    class IntEqualsTest : EqualsTest(), IntBTest

    class DoubleEqualsTest : EqualsTest(), DoubleBTest

    class StringEqualsTest : EqualsTest(), StringBTest

    class IntGreaterThan : IntBTest {
        override val label = ">"
        override fun accept(a: Any?, b: Any?): Boolean {
            if (a is Int && b is Int) {
                return a > b
            }
            return false
        }

        override fun opposite(): Test = NotTest(this, "<=")
    }

    class IntLessThan : IntBTest {
        override val label = "<"
        override fun accept(a: Any?, b: Any?): Boolean {
            if (a is Int && b is Int) {
                return a < b
            }
            return false
        }

        override fun opposite(): Test = NotTest(this, ">=")
    }


    class DoubleGreaterThan : DoubleBTest {
        override val label = ">"
        override fun accept(a: Any?, b: Any?): Boolean {
            if (a is Double && b is Double) {
                return a > b
            }
            return false
        }

        override fun opposite(): Test = NotTest(this, "<=")
    }

    class DoubleLessThan : DoubleBTest {
        override val label = "<"
        override fun accept(a: Any?, b: Any?): Boolean {
            if (a is Double && b is Double) {
                return a < b
            }
            return false
        }

        override fun opposite(): Test = NotTest(this, ">=")
    }

    class StringLessThan : StringBTest {
        override val label = "<"
        override fun accept(a: Any?, b: Any?): Boolean {
            if (a is String && b is String) {
                return a < b
            }
            if (a is Char && b is String) {
                return a.toString() < b
            }
            return false
        }

        override fun opposite(): Test = NotTest(this, ">=")
    }

    class StringGreaterThan : StringBTest {
        override val label = "<"
        override fun accept(a: Any?, b: Any?): Boolean {
            if (a is String && b is String) {
                return a > b
            }
            if (a is Char && b is String) {
                return a.toString() > b
            }

            return false
        }

        override fun opposite(): Test = NotTest(this, ">=")
    }

    class StringContains : StringBTest {
        override val label = "contains"
        override fun accept(a: Any?, b: Any?): Boolean {
            if (a is String && b is String) {
                return a.toLowerCase().contains(b.toLowerCase())
            }
            return false
        }

        override fun opposite(): Test = NotTest(this, "does not contain")
    }

    class StringStartsWith : StringBTest {
        override val label = "starts with"
        override fun accept(a: Any?, b: Any?): Boolean {
            if (a is String && b is String) {
                return a.toLowerCase().startsWith(b.toLowerCase())
            }
            return false
        }

        override fun opposite(): Test = NotTest(this, "does not start with")
    }

    class StringEndsWith : StringBTest {
        override val label = "ends with"
        override fun accept(a: Any?, b: Any?): Boolean {
            if (a is String && b is String) {
                return a.toLowerCase().endsWith(b.toLowerCase())
            }
            return false
        }

        override fun opposite(): Test = NotTest(this, "does not end with")
    }

}
