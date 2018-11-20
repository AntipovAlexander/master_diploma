package com.antipov.modules

import kotlin.math.pow

class Dispersion {
    fun calculate(mx: Float, y: ArrayList<Float>): Float {
        var sum = 0.0f

        y.forEach { it ->
            sum += (it - mx).pow(2)
        }

        return (1.0f / y.size) * sum
    }
}