package com.antipov.modules

import kotlin.math.pow
import kotlin.math.sqrt

class AutocorrelationCoefficient(private val y: ArrayList<Int>) {

    fun calculate(): ArrayList<Float> {
        val lag: Int = y.size / 4
        val coefficientsList = arrayListOf<Float>()

        for (i in 0..lag) {
            val mean1 = calculateMean1(i)
            val mean2 = calculateMean2(i)
            val coefficient = calculateCoefficient(i, mean1, mean2)

            coefficientsList.add(coefficient)
        }

        return coefficientsList
    }

    private fun calculateMean1(index: Int): Float {
        var mean1 = 0.0f

        for (i in index + 1 until y.size) {
            mean1 += y[i]
        }

        mean1 /= (y.size - (index + 1))

        return mean1
    }

    private fun calculateMean2(index: Int): Float {
        var mean2 = 0.0f

        for (i in index + 1 until y.size) {
            mean2 += y[i - (index + 1)]
        }

        mean2 /= (y.size - (index + 1))

        return mean2
    }

    private fun calculateCoefficient(index: Int, mean1: Float, mean2: Float): Float {


        var enumerator = 0.0f
        var denumerator = 0.0f

        for (i in index + 1 until y.size) {
            enumerator += (y[i] - mean1) * (y[i - (index + 1)] - mean2)
        }


        var sum1 = 0.0f
        for (i in index + 1 until y.size) {
            sum1 += (y[i] - mean1).pow(2)
        }


        var sum2 = 0.0f
        for (i in index + 1 until y.size) {
            sum2 += (y[i - (index + 1)] - mean2).pow(2)
        }


        denumerator = sqrt(sum1 * sum2)


        return enumerator / denumerator
    }
}