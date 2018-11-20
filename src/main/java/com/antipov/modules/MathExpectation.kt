package com.antipov.modules

class MathExpectation {
    fun calculate(y: ArrayList<Float>): Float {
        var sum = 0.0f
        y.forEach {
            sum += it
        }

        return sum / y.size
    }
}