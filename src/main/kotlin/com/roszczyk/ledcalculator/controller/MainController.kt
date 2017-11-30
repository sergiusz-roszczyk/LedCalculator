package com.roszczyk.ledcalculator.controller

import com.roszczyk.ledcalculator.util.calculateOutputLevels
import com.roszczyk.ledcalculator.util.formatConstArrays
import javafx.beans.property.*
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.util.converter.NumberStringConverter

const val R = 0
const val G = 1
const val B = 2
const val R_SERIES = 0
const val G_SERIES = 2
const val B_SERIES = 3
/* Seria liniowa jest na miejscu 1 ze względu na domyślne kolorowanie JavaFX - czerwony, zielony, żółty, niebieski */
const val L_SERIES = 1

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
    @FXML
    private lateinit var progmemCB: CheckBox

    private val series: Array<XYChart.Series<String, Number>> = arrayOf(XYChart.Series(), XYChart.Series(), XYChart.Series(), XYChart.Series())
    private val inputLevels: IntegerProperty = SimpleIntegerProperty(15)
    private val gamma: Array<DoubleProperty> = arrayOf(SimpleDoubleProperty(2.3), SimpleDoubleProperty(2.2), SimpleDoubleProperty(2.1))
    private val maxOutputLevel = arrayOf<IntegerProperty>(SimpleIntegerProperty(255), SimpleIntegerProperty(235), SimpleIntegerProperty(180))
    private val progmem: BooleanProperty = SimpleBooleanProperty(false)

    fun initialize(): Unit {
        outputChart.yAxis.isAutoRanging = false
        outputChart.yAxis.animated = false
        outputChart.xAxis.animated = false
        outputChart.xAxis.label = "Poziom wejściowy"
        outputChart.animated = false
        outputChart.isLegendVisible = false
        outputChart.data.addAll(series)
        inputLevelsTF.textProperty().bindBidirectional(inputLevels, NumberStringConverter())
        gammaRTF.textProperty().bindBidirectional(gamma[R], NumberStringConverter())
        gammaGTF.textProperty().bindBidirectional(gamma[G], NumberStringConverter())
        gammaBTF.textProperty().bindBidirectional(gamma[B], NumberStringConverter())
        outputLevelRTF.textProperty().bindBidirectional(maxOutputLevel[R], NumberStringConverter())
        outputLevelGTF.textProperty().bindBidirectional(maxOutputLevel[G], NumberStringConverter())
        outputLevelBTF.textProperty().bindBidirectional(maxOutputLevel[B], NumberStringConverter())
        progmemCB.selectedProperty().bindBidirectional(progmem)
        calculateBT.onAction = EventHandler<ActionEvent> { this.handleCalculate() }
    }

    private fun handleCalculate() {
        val values = calculateRgbValues()
        val totalMaxOutputLevel = maxOutputLevel.asList().map(IntegerProperty::get).max() ?: 255

        dataTableTA.text = formatConstArrays(progmem.get(), inputLevels.get(), gamma, maxOutputLevel, values)
        updateAxisBounds(totalMaxOutputLevel)
        updateSeries(values, totalMaxOutputLevel)
    }

    private fun calculateRgbValues(): Array<List<XYChart.Data<String, Number>>> {
        return (0 until gamma.size).map {
            calculateOutputLevels(inputLevels.get(), maxOutputLevel[it].get(), gamma[it].doubleValue()).map {
                XYChart.Data(it.first, it.second)
            }
        }.toTypedArray()
    }

    private fun updateAxisBounds(totalMaxOutputLevel: Int) {
        val yAxis = outputChart.yAxis as NumberAxis
        yAxis.lowerBound = 0.0
        yAxis.upperBound = totalMaxOutputLevel.toDouble()
    }

    private fun updateSeries(values: Array<List<XYChart.Data<String, Number>>>, totalMaxOutputLevel: Int) {
        series[R_SERIES].data.setAll(values[R])
        series[G_SERIES].data.setAll(values[G])
        series[B_SERIES].data.setAll(values[B])
        series[L_SERIES].data.setAll(calculateOutputLevels(inputLevels.get(), totalMaxOutputLevel, 1.0).map { XYChart.Data(it.first, it.second) })
    }

}
