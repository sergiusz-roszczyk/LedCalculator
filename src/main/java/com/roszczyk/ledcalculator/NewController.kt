package com.roszczyk.ledcalculator

import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.util.converter.NumberStringConverter

const val R = 0
const val G = 1
const val B = 2

class NewController {

    @FXML
    private lateinit var inputLevelsTF: TextField
    @FXML
    private lateinit var gammaRTF: TextField
    @FXML
    private lateinit var outputLevelRTF: TextField
    @FXML
    private lateinit var gammaGTF: TextField
    @FXML
    private lateinit var outputLevelGTF: TextField
    @FXML
    private lateinit var gammaBTF: TextField
    @FXML
    private lateinit var outputLevelBTF: TextField
    @FXML
    private lateinit var calculateBT: Button
    @FXML
    private lateinit var outputChart: LineChart<String, Number>
    @FXML
    private lateinit var dataTableTA: TextArea

    private val rSeries: XYChart.Series<String, Number> = XYChart.Series()
    private val gSeries: XYChart.Series<String, Number> = XYChart.Series()
    private val bSeries: XYChart.Series<String, Number> = XYChart.Series()
    private val linearSeries: XYChart.Series<String, Number> = XYChart.Series()

    private val inputLevels: IntegerProperty = SimpleIntegerProperty(15)
    private val gamma: Array<DoubleProperty> = arrayOf(SimpleDoubleProperty(2.3), SimpleDoubleProperty(2.2), SimpleDoubleProperty(2.1))
    private val maxOutputLevel = arrayOf<IntegerProperty>(SimpleIntegerProperty(255), SimpleIntegerProperty(235), SimpleIntegerProperty(180))

    fun initialize(): Unit {
        inputLevelsTF.textProperty().bindBidirectional(inputLevels, NumberStringConverter())
        gammaRTF.textProperty().bindBidirectional(gamma[R], NumberStringConverter())
        gammaGTF.textProperty().bindBidirectional(gamma[G], NumberStringConverter())
        gammaBTF.textProperty().bindBidirectional(gamma[B], NumberStringConverter())
        outputLevelRTF.textProperty().bindBidirectional(maxOutputLevel[R], NumberStringConverter())
        outputLevelGTF.textProperty().bindBidirectional(maxOutputLevel[G], NumberStringConverter())
        outputLevelBTF.textProperty().bindBidirectional(maxOutputLevel[B], NumberStringConverter())
        calculateBT.onAction = EventHandler<ActionEvent> { this.handleCalculate() }
        outputChart.data.add(rSeries)
        outputChart.data.add(linearSeries)
        outputChart.data.add(gSeries)
        outputChart.data.add(bSeries)
        rSeries.name = "R"
        gSeries.name = "G"
        bSeries.name = "B"
        linearSeries.name = "liniowy"
    }

    private fun handleCalculate() {

        val valuesR = calculateOutputLevels(inputLevels.get(), maxOutputLevel[R].get(), gamma[R].doubleValue())
        val valuesG = calculateOutputLevels(inputLevels.get(), maxOutputLevel[G].get(), gamma[G].doubleValue())
        val valuesB = calculateOutputLevels(inputLevels.get(), maxOutputLevel[B].get(), gamma[B].doubleValue())

        val sb = StringBuilder()
        sb.append(String.format("// inLevels=%d, gammaR=%s, gammaB=%s, gammaG=%s, maxR=%s, maxG=%s, maxB=%s\n", inputLevels.get(), gamma[R].get(), gamma[G].get(), gamma[B].get(), maxOutputLevel[R].get(), maxOutputLevel[G].get(), maxOutputLevel[B].get()))
        sb.append(printArduinoCode("gammaR", valuesR))
        sb.append(printArduinoCode("gammaG", valuesG))
        sb.append(printArduinoCode("gammaB", valuesB))
        dataTableTA.text = sb.toString()

        outputChart.yAxis.isAutoRanging = false
        outputChart.yAxis.animated = false
        outputChart.xAxis.animated = false
        outputChart.xAxis.label = "Poziom wejściowy"
        outputChart.animated = false
        outputChart.isLegendVisible = false
        val yAxis = outputChart.yAxis as NumberAxis
        yAxis.lowerBound = 0.0
        // int totalMaxOutputLevel = Stream.of( maxOutputLevel ).map( IntegerProperty::get ).max( Integer::compare ).orElse( 255 );
        val totalMaxOutputLevel: Int = maxOutputLevel.asList().map(IntegerProperty::get).max() ?: 255

        yAxis.upperBound = totalMaxOutputLevel.toDouble()
        rSeries.data.setAll(valuesR)
        gSeries.data.setAll(valuesG)
        bSeries.data.setAll(valuesB)
        linearSeries.data.setAll(calculateOutputLevels(inputLevels.get(), totalMaxOutputLevel, 1.0))

    }

    private fun calculateOutputLevels(inLevels: Int, maxOutLevel: Int, gammaValue: Double): List<XYChart.Data<String, Number>> {
        val result = (0..inLevels).map { XYChart.Data<String, Number>(it.toString(), (Math.pow((it.toFloat() / inLevels.toFloat()).toDouble(), gammaValue) * maxOutLevel + 0.5).toInt()) }
        return result
    }

    private fun printArduinoCode(varName: String, values: List<XYChart.Data<String, Number>>): String {
        val sb = StringBuilder()
        sb.append(String.format("const uint8_t %s[] = {", varName))
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
}