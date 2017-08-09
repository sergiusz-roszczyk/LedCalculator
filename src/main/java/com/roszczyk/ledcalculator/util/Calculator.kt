package com.roszczyk.ledcalculator.util

import javafx.scene.chart.XYChart


fun calculateOutputLevels(inLevels: Int, maxOutLevel: Int, gammaValue: Double): List<XYChart.Data<String, Number>> {
    val result = (0..inLevels).map { XYChart.Data<String, Number>(it.toString(), (Math.pow((it.toFloat() / inLevels.toFloat()).toDouble(), gammaValue) * maxOutLevel + 0.5).toInt()) }
    return result
}
