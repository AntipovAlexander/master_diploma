package com.antipov.modules

import kotlin.math.pow
import kotlin.math.sqrt

class StandartDeviation {

    fun calculate(mx: Float, x: ArrayList<Float>): Float {
        var sum = 0.0f
        x.forEach {
            sum += (it - mx).pow(2)
        }

        return sqrt(sum / (x.size - 1).toFloat())
    }
}