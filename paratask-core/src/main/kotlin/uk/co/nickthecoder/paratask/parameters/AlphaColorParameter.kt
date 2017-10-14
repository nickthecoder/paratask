package uk.co.nickthecoder.paratask.parameters

import javafx.scene.paint.Color
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.util.uncamel

class AlphaColorParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Color = Color.WHITE)

    : CompoundParameter<Color>(name = name,
        label = label,
        description = description) {


    val rgbP = ColorParameter(name + "_rgb", label = "")
    val alphaP = DoubleParameter(name + "_alpha", label = "Alpha", value = 1.0, minValue = 0.0, maxValue = 1.0)
            .asSlider(DoubleParameter.SliderInfo(blockIncrement = 0.1, majorTickUnit = 0.5, minorTickCount = 5, snapToTicks = false))

    override var value: Color
        get() {
            val rgb = rgbP.value
            return Color(rgb.red, rgb.green, rgb.blue, alphaP.value ?: 1.0)
        }
        set(v) {
            rgbP.value = v
            alphaP.value = v.opacity
        }


    override val converter = object : StringConverter<Color>() {
        override fun fromString(str: String): Color {
            val trimmed = str.trim()

            try {
                val rgb = trimmed.substring(0, 7)
                val alpha = trimmed.substring(7, 9)
                return Color.web(rgb, Integer.parseInt(alpha, 16) / 255.0)
            } catch (e: Exception) {
                throw ParameterException(this@AlphaColorParameter, "Not a Color")
            }
        }

        override fun toString(obj: Color): String {
            val r = Math.round(obj.red * 255.0).toInt();
            val g = Math.round(obj.green * 255.0).toInt()
            val b = Math.round(obj.blue * 255.0).toInt()
            val a = Math.round(obj.opacity * 255.0).toInt()
            return String.format("#%02x%02x%02x%02x", r, g, b, a)
        }

    }

    init {
        addParameters(rgbP, alphaP)
        asHorizontal(labelPosition = LabelPosition.TOP)
    }

    override fun toString() = "AlphaColor" + super.toString()

    override fun copy() = AlphaColorParameter(name = name, label = label, description = description, value = value)

}
