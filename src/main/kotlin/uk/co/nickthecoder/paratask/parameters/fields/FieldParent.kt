package uk.co.nickthecoder.paratask.parameters.fields

interface FieldParent {

    val spacing : Double

    val columns : List<FieldColumn>

    fun calculateColumnPreferences()

    fun calculateColumnWidths()
}