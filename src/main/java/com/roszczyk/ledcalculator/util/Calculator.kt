package com.roszczyk.ledcalculator.util

fun calculateOutputLevels(inLevels: Int, maxOutLevel: Int, gammaValue: Double): List<Pair<String, Number>> {
    return (0..inLevels).map { Pair(it.toString(), Math.ceil(Math.pow((it.div(inLevels.toDouble())), gammaValue).times(maxOutLevel)).toInt()) }
}
