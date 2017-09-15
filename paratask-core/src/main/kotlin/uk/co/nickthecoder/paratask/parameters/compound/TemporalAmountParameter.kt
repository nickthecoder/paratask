package uk.co.nickthecoder.paratask.parameters.compound

import uk.co.nickthecoder.paratask.parameters.AbstractGroupParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.enumChoices
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel
import java.time.Duration
import java.time.Period
import java.time.temporal.TemporalAmount
import kotlin.reflect.KProperty

class TemporalAmountParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        required: Boolean = true)

    : AbstractGroupParameter(name, label, description) {

    val amountP = IntParameter("amount", label = "", required = required)
    var amount by amountP

    val unitsP = ChoiceParameter("units", label = "", required = required, value = TimeUnit.DAYS).enumChoices()
    var units by unitsP

    override fun saveChildren(): Boolean = true

    val value: TemporalAmount?
        get() {
            val a = amount
            if (a == null) {
                return null
            }
            return when (units) {
                TimeUnit.MILLISECONDS -> Duration.ofMillis(a.toLong())
                TimeUnit.SECONDS -> Duration.ofSeconds(a.toLong())
                TimeUnit.MINUTES -> Duration.ofMinutes(a.toLong())
                TimeUnit.HOURS -> Duration.ofHours(a.toLong())
                TimeUnit.DAYS -> Period.ofDays(a)
                TimeUnit.WEEKS -> Period.ofDays(a)
                TimeUnit.MONTHS -> Period.ofMonths(a)
                TimeUnit.YEARS -> Period.ofYears(a)
                null -> null
            }
        }


    operator fun getValue(thisRef: Any?, property: KProperty<*>): TemporalAmount? {
        return value
    }

    init {
        horizontalLayout(false)
        addParameters(amountP, unitsP)
    }

    override fun errorMessage(): String? {
        return null
    }

    override fun copy(): TemporalAmountParameter {
        val copy = TemporalAmountParameter(name, label, description, required = amountP.required)
        copy.amount = amount
        copy.units = units
        copyAbstractAttributes(copy)
        return copy
    }


    enum class TimeUnit(override val label: String) : Labelled {
        MILLISECONDS("Milliseconds"),
        SECONDS("Seconds"),
        MINUTES("Minutes"),
        HOURS("Hours"),
        DAYS("Days"),
        WEEKS("Weeks"),
        MONTHS("Months"),
        YEARS("Years");
    }
}

