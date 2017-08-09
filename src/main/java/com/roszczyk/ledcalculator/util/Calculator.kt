package com.roszczyk.ledcalculator.util

fun calculateOutputLevels(inLevels: Int, maxOutLevel: Int, gammaValue: Double): List<Pair<String, Number>> {
    return (0..inLevels).map { Pair(it.toString(), (Math.pow((it.toFloat() / inLevels.toFloat()).toDouble(), gammaValue) * maxOutLevel + 0.5).toInt()) }
}
