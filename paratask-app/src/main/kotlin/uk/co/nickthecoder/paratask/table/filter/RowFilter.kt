package uk.co.nickthecoder.paratask.table.filter

import groovy.lang.Binding
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.*
import uk.co.nickthecoder.paratask.gui.ScriptVariables
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.misc.Wrapped
import uk.co.nickthecoder.paratask.options.GroovyScript
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.util.Resource
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime


class RowFilter<R : Any>(val tool: Tool, val columns: List<Column<R, *>>, val exampleRow: R, val label: String = "Filter")
    : AbstractTask() {

    companion object {

        private val nullTest = NullTest()
        private val notNullTest = NullTest().opposite()

        private val intTests = testOrNotTest(IntEqualsTest(), IntGreaterThan(), IntLessThan(), IntWithin())
        private val doubleTests = testOrNotTest(DoubleEqualsTest(), DoubleGreaterThan(), DoubleLessThan())
        private val longTests = testOrNotTest(LongEqualsTest(), LongGreaterThan(), LongLessThan())

        private val stringTests = testOrNotTest(StringEqualsTest(), StringGreaterThan(), StringLessThan(),
                StringContains(), StringStartsWith(), StringEndsWith(), StringMatches())

        private val localDateTests = testOrNotTest(LocalDateBefore(), LocalDateAfter())
        private val localDateTimeTests = testOrNotTest(LocalDateTimeEarlierThan(), LocalDateTimeBefore(), LocalDateTimeOn())

        private val charTests = testOrNotTest(StringEqualsTest(), StringGreaterThan(), StringLessThan())
        private val booleanTests = testOrNotTest(BooleanEqualsTest())
        private val objectTests = testOrNotTest()

        private val toStringTests: List<Test> = stringTests.map {
            if (it === nullTest || it === notNullTest) {
                it
            } else {
                ToStringTest(it)
            }
        }

        private val fileSpecificTests = testOrNotTest(FileExits(), FileIsFile(), FileIsDirectory())
        private val fileTests: MutableList<Test> = (fileSpecificTests + toStringTests).toMutableList()

        private val resourceSpecificTests = testOrNotTest(ResourceIsFile(), ResourceIsDirectory(), ResourceIsFileOrDirectory())
        private val resourceTests: MutableList<Test> = (resourceSpecificTests + toStringTests).toMutableList()


        val bTypes = mutableListOf<BType>(BooleanBType(), IntBType(), DoubleBType(), StringBType(),
                LocalDateBType(), TemporalAmountBType(),
                IntRangeBType())

        val testMap = mutableMapOf<Class<*>, MutableList<Test>>(
                java.lang.Boolean::class.java to booleanTests,
                java.lang.Integer::class.java to intTests,
                java.lang.Long::class.java to longTests,
                java.lang.Double::class.java to doubleTests,
                java.lang.Character::class.java to charTests,
                String::class.java to stringTests,
                File::class.java to fileTests,
                Resource::class.java to resourceTests,
                LocalDate::class.java to localDateTests,
                LocalDateTime::class.java to localDateTimeTests
        )

        fun testOrNotTest(vararg tests: Test, includeNullTests: Boolean = true): MutableList<Test> {
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

    val programmableProperties = ScriptVariables()


    override val taskD = TaskDescription("filter", description =
    """Filter Rows.
You can also edit filters by clicking the table columns' headers.""")

    val acceptRejectP = BooleanParameter("acceptReject", label = "", value = true, required = false)

    val groovyScriptP = ScriptParameter("groovyScript", programmableProperties, label = "Groovy Script (return true or false)", required = false, rows = 5)

    val conditionsP = MultipleParameter("conditions", isBoxed = true) { Condition() }

    val andP = BooleanParameter("and", label = "", value = true)

    var groovyScript: GroovyScript? = null

    init {
        programmableProperties.add("row", exampleRow.javaClass)
        programmableProperties.add("tool", tool.javaClass)

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
                bindings.setVariable("tool", tool)
                val result = script.run(bindings)
                if (result == false) {
                    return false
                } else if (result == true) {
                    return true
                }
                // If a non-boolean value is returned, then ignore it.
            }
        } catch (e: Exception) {
            println("Groovy Row Filter failed : $e")
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

        final override val taskD = TaskDescription("editColumnFilters", width = 700)

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

        val myGroovyScriptP = ScriptParameter("groovyScript", programmableProperties, label = "Groovy Script (return true or false)", required = false, rows = 5, value = groovyScriptP.value)

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

        val bTypeParameters = mutableMapOf<BType, Parameter>()

        init {
            boxLayout(false)
            addParameters(columnP, testP)
            bTypes.forEach {
                val parameter = it.createParameter()
                parameter.hidden = true
                addParameters(parameter)
                bTypeParameters[it] = parameter
            }

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

            bTypeParameters.forEach { bType, parameter ->
                bType.copyValue(other.bTypeParameters[bType]!!, parameter)
            }
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

            val tests = testMap[columnType]
            if (tests == null) {
                testChoices(objectTests)
            } else {
                testChoices(tests)
            }
        }

        fun testPChanged() {
            val bClass = testP.value?.bClass
            bTypeParameters.forEach { key, parameter ->
                parameter.hidden = bClass != key.klass
            }
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

            val klass = testP.value?.bClass
            val bType = bTypes.firstOrNull { it.klass === klass }
            val parameter = bTypeParameters[bType]
            val b = parameter?.let { bType?.getValue(parameter) }

            return testP.value!!.result(a, b)
        }

    }

}
