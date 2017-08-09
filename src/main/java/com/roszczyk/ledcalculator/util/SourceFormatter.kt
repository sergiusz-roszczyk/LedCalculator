package com.roszczyk.ledcalculator.util

import com.roszczyk.ledcalculator.controller.B
import com.roszczyk.ledcalculator.controller.G
import com.roszczyk.ledcalculator.controller.R
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.scene.chart.XYChart


fun formatConstArrays(progmem: Boolean, inputLevels: Int, gamma: Array<DoubleProperty>, maxOutputLevel: Array<IntegerProperty>, values: Array<List<XYChart.Data<String, Number>>>): String {
    val sb = StringBuilder()
    sb.append(String.format("// inLevels=%d, gammaR=%s, gammaB=%s, gammaG=%s, maxR=%s, maxG=%s, maxB=%s\n", inputLevels, gamma[R].get(), gamma[G].get(), gamma[B].get(), maxOutputLevel[R].get(), maxOutputLevel[G].get(), maxOutputLevel[B].get()))
    sb.append(printArduinoCode("gammaR", values[R], progmem))
    sb.append(printArduinoCode("gammaG", values[G], progmem))
    sb.append(printArduinoCode("gammaB", values[B], progmem))
    return sb.toString()
}


fun printArduinoCode(varName: String, values: List<XYChart.Data<String, Number>>, progmem: Boolean): String {
    val sb = StringBuilder()
    sb.append(String.format("const uint8_t %s[] %s = {", varName, if (progmem) "PROGMEM" else ""))
    for (i in 0..values.size - 1) {
        if (i > 0) {
            sb.append(',')
        }
        if (i and 15 == 0) {
            sb.append("\n  ")
        }
        sb.append(String.format("%3d", values[i].yValue.toInt()))
    }
    sb.append("\n};\n")
    return sb.toString()
}
