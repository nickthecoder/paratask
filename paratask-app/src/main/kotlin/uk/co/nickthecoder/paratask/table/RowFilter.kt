package uk.co.nickthecoder.paratask.table

import groovy.lang.Binding
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.*
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.misc.Wrapped
import uk.co.nickthecoder.paratask.options.GroovyScript
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.compound.IntRangeParameter
import uk.co.nickthecoder.paratask.util.Resource
import java.io.File


class RowFilter<R>(val tool: Tool, val columns: List<Column<R, *>>, val exampleRow: R, val label: String = "Filter")
    : AbstractTask() {

    companion object {

        val nullTest = NullTest()
        val notNullTest = NullTest().opposite()

        val intTests = testOrNotTest(IntEqualsTest(), IntGreaterThan(), IntLessThan(), IntWithin())
        val doubleTests = testOrNotTest(DoubleEqualsTest(), DoubleGreaterThan(), DoubleLessThan())
        val longTests = testOrNotTest(LongEqualsTest(), LongGreaterThan(), LongLessThan())
        val stringTests = testOrNotTest(StringEqualsTest(), StringGreaterThan(), StringLessThan(), StringContains(), StringStartsWith(), StringEndsWith(), StringMatches())
        val charTests = testOrNotTest(StringEqualsTest(), StringGreaterThan(), StringLessThan())
        val booleanTests = testOrNotTest(BooleanEqualsTest())
        val objectTests = testOrNotTest()

        val toStringTests: List<Test> = stringTests.map {
            if (it === nullTest || it === notNullTest) {
                it
            } else {
                ToStringTest(it)
            }
        }

        val fileSpecificTests = testOrNotTest(FileExits(), FileIsFile(), FileIsDirectory())
        val fileTests: List<Test> = fileSpecificTests + toStringTests

        fun testOrNotTest(vararg tests: Test, includeNullTests: Boolean = true): List<Test> {
            val result = mutableListOf<Test>()
            tests.forEach {
                result.add(it)
                result.add(it.opposite())
            }
            if (includeNullTests) {
                result.add(nullTest)
                result.add(notNullTest)
            }
            return result
        }
    }

    override val taskD = TaskDescription("filter", description =
    """Filter Rows.
You can also edit filters by clicking the table columns' headers.""")

    val acceptRejectP = BooleanParameter("acceptReject", label = "", value = true, required = false)

    val groovyScriptP = StringParameter("groovyScript", label = "Groovy Script (return true or false)", required = false, rows = 5, isBoxed = true)

    val conditionsP = MultipleParameter("conditions", isBoxed = true) { Condition() }

    val andP = BooleanParameter("and", label = "", value = true)

    var groovyScript: GroovyScript? = null

    init {
        andP.asComboBox("AND", "OR")

        acceptRejectP.asComboBox("Accept if...", "Reject if...", "Ignore")

        taskD.addParameters(acceptRejectP, conditionsP, andP, groovyScriptP)
        groovyScriptP.listen {
            groovyScript = if (groovyScriptP.value.isBlank()) {
                null
            } else {
                GroovyScript(groovyScriptP.value)
            }
        }
    }

    fun filtersColumn(column: Column<R, *>?): Boolean {
        if (column == null && groovyScriptP.value.isNotBlank()) {
            return true
        }
        return conditionsP.value.filterIsInstance<Condition>().firstOrNull { it.columnP.value === column } != null
    }

    override fun run() {
        tool.toolPane?.parametersPane?.run()
    }

    fun accept(row: R): Boolean {
        if (acceptRejectP.value == null) {
            return true
        }
        val result = testResult(row)
        if (acceptRejectP.value == false) {
            return !result
        } else {
            return result
        }
    }

    fun testResult(row: R): Boolean {

        if (andP.value == true) {
            if (!conditionsResult(row)) {
                return false
            }
        } else if (conditionsResult(row)) {
            return true
        }

        return groovyResult(row)
    }

    fun conditionsResult(row: R): Boolean {

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

    fun groovyResult(row: R): Boolean {
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
        if (column == null) {
            if (exampleRow is Wrapped<*>) {
                return exampleRow.wrapped
            } else {
                return exampleRow
            }
        } else {
            return column.filterGetter(exampleRow)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is RowFilter<*>) {
            return false
        }
        if (groovyScriptP.value != other.groovyScriptP.value) {
            return false
        }
        if (conditionsP.value.size != other.conditionsP.value.size) {
            return false
        }
        conditionsP.value.filterIsInstance<Condition>().forEachIndexed { index, myCondition ->
            val otherCondition = other.conditionsP.value[index]
            if (myCondition != otherCondition) return false
        }
        return true
    }

    fun editColumnFilters(column: Column<R, *>?, onOk: () -> Unit) {
        val task: Task = if (column == null) {
            EditRowFilters(onOk)
        } else {
            EditColumnFilters(column, onOk)
        }
        TaskPrompter(task).placeOnStage(Stage())
    }

    open inner class EditColumnFilters(val column: Column<R, *>?, val onOk: () -> Unit) : AbstractTask() {

        override val taskRunner = UnthreadedTaskRunner(this)

        override val taskD = TaskDescription("editColumnFilters", width = 700)

        val columnAcceptRejectP = BooleanParameter("acceptReject", label = "", value = acceptRejectP.value, required = false)

        val columnInfoP = InformationParameter("info", information = if (column == null) "row" else "Column ${column.name}")

        val columnConditionsP = MultipleParameter("conditions", isBoxed = true) {
            val condition = Condition()
            condition.columnP.value = column
            condition.columnP.hidden = true
            condition
        }

        val columnAndP = BooleanParameter("and", label = "", value = andP.value)

        val clearOtherFiltersP = BooleanParameter("clearOtherFilters", value = false, required = true, labelOnLeft = false)

        init {
            columnAcceptRejectP.asComboBox("Accept if...", "Reject if...", "Ignore")
            columnAndP.asComboBox("AND", "OR")

            taskD.addParameters(columnAcceptRejectP, columnInfoP, columnConditionsP, columnAndP, clearOtherFiltersP)

            conditionsP.value.filterIsInstance<Condition>().filter { it.columnP.value === column }.forEach {
                @Suppress("UNCHECKED_CAST")
                val newValue = columnConditionsP.newValue() as RowFilter<R>.Condition
                newValue.copyValues(it)
            }

            if (columnConditionsP.value.isEmpty()) {
                columnConditionsP.newValue()
            }
        }

        override fun run() {
            acceptRejectP.value = columnAcceptRejectP.value
            andP.value = columnAndP.value
            if (clearOtherFiltersP.value == true) {
                conditionsP.clear()
                groovyScriptP.value = ""
            } else {
                // Remove the conditions for the column
                conditionsP.value = conditionsP.value.filterIsInstance<Condition>().filter { it.columnP.value !== column }
            }
            // Add the new conditions
            columnConditionsP.value.filterIsInstance<Condition>().forEach {
                @Suppress("UNCHECKED_CAST")
                val newValue = conditionsP.newValue() as RowFilter<R>.Condition
                newValue.copyValues(it)
            }

            onOk()
        }
    }

    inner class EditRowFilters(onOk: () -> Unit) : EditColumnFilters(null, onOk) {

        val myGroovyScriptP = StringParameter("groovyScript", label = "Groovy Script (return true or false)", required = false, rows = 5, isBoxed = true, value = groovyScriptP.value)

        init {
            taskD.addParameters(myGroovyScriptP)
        }

        override fun run() {
            groovyScriptP.value = myGroovyScriptP.value
            super.run()
        }
    }

    inner class Condition : CompoundParameter("condition") {

        val columnP = ChoiceParameter<Column<R, *>?>("column", label = "", value = null, required = false)
        val testP = ChoiceParameter<Test?>("test", label = "", value = null, required = true)
        val booleanValueP = BooleanParameter("booleanValue", label = "")
        val intValueP = IntParameter("intValue", label = "")
        val doubleValueP = DoubleParameter("doubleValue", label = "")
        val stringValueP = StringParameter("stringValue", label = "")
        val regexValueP = RegexParameter("regexValue", label = "")
        val intRangeP = IntRangeParameter("intRange", label = "")

        init {
            boxLayout(false)
            addParameters(columnP, testP, booleanValueP, intValueP, doubleValueP, stringValueP, regexValueP, intRangeP)

            booleanValueP.hidden = true
            booleanValueP.asComboBox()
            intValueP.hidden = true
            doubleValueP.hidden = true
            stringValueP.hidden = true
            regexValueP.hidden = true
            intRangeP.hidden = true

            columnP.addChoice("ROW", null, "ROW")
            columns.forEach { column ->
                columnP.addChoice(column.name, column, column.name)
            }

            columnP.listen { columnPChanged() }
            testP.listen { testPChanged() }
            columnPChanged()
        }


        fun copyValues(other: Condition) {
            columnP.value = other.columnP.value
            testP.value = other.testP.value
            booleanValueP.value = other.booleanValueP.value
            intValueP.value = other.intValueP.value
            doubleValueP.value = other.doubleValueP.value
            stringValueP.value = other.stringValueP.value
            regexValueP.value = other.regexValueP.value
            intRangeP.from = other.intRangeP.from
            intRangeP.to = other.intRangeP.to
        }

        fun testChoices(tests: List<Test>) {
            testP.clear()
            tests.forEach { test ->
                testP.addChoice(test.label, test, test.label)
            }

            testP.value = tests.firstOrNull()
        }

        fun columnPChanged() {
            val exampleValue = exampleValue(columnP.value)
            val columnType: Class<*>? = exampleValue?.let { it::class.java }

            when (columnType) {
                java.lang.Boolean::class.java -> testChoices(booleanTests)
                java.lang.Character::class.java -> testChoices(charTests)
                java.lang.Integer::class.java -> testChoices(intTests)
                java.lang.Double::class.java -> testChoices(doubleTests)
                java.lang.Long::class.java -> testChoices(longTests)

                String::class.java -> testChoices(stringTests)
                File::class.java -> testChoices(fileTests)
                Resource::class.java -> testChoices(toStringTests)

                else -> testChoices(objectTests)
            }
        }

        fun testPChanged() {
            val bType = testP.value?.bType
            booleanValueP.hidden = bType != java.lang.Boolean::class.java
            intValueP.hidden = bType != java.lang.Integer::class.java
            doubleValueP.hidden = bType != java.lang.Double::class.java
            stringValueP.hidden = bType != String::class.java
            regexValueP.hidden = bType != Regex::class.java
            intRangeP.hidden = bType != IntRangeParameter::class.java
        }

        fun accept(row: R): Boolean {

            val column = columnP.value
            val a = if (column == null) {
                if (row is Wrapped<*>) {
                    row.wrapped
                } else {
                    row
                }
            } else {
                column.filterGetter(row)
            }

            val bType = testP.value?.bType
            val b: Any? = when (bType) {
                java.lang.Boolean::class.java -> {
                    booleanValueP.value!!
                }
                java.lang.Integer::class.java -> {
                    intValueP.value!!
                }
                java.lang.Double::class.java -> {
                    doubleValueP.value!!
                }
                String::class.java -> {
                    stringValueP.value
                }
                Regex::class.java -> {
                    Regex(regexValueP.value)
                }
                IntRangeParameter::class.java -> {
                    intRangeP
                }

                else -> {
                    null
                }
            }

            return testP.value!!.result(a, b)
        }

        override fun equals(other: Any?): Boolean {
            if (other !is RowFilter<*>.Condition) {
                return false
            }
            if (columnP.value != other.columnP.value || testP.value != other.testP.value) return false

            if ((!booleanValueP.hidden) && booleanValueP.value != other.booleanValueP.value) return false
            if ((!intValueP.hidden) && intValueP.value != other.intValueP.value) return false
            if ((!doubleValueP.hidden) && doubleValueP.value != other.doubleValueP.value) return false
            if ((!stringValueP.hidden) && stringValueP.value != other.stringValueP.value) return false
            if ((!regexValueP.hidden) && regexValueP.value != other.regexValueP.value) return false

            return true
        }
    }


}
