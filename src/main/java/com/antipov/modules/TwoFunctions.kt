package com.antipov.modules

import java.util.HashSet


class TwoFunctions {

    fun calculate(x: ArrayList<Float>, y: ArrayList<Float>, printDebug: Boolean = false): ArrayList<ArrayList<Float>> {
        val valsX = HashSet<Float>(x)
        val result = arrayListOf<ArrayList<Float>>()
        valsX.forEach { value ->

            val argumentsX = arrayListOf<Int>()

            x.forEachIndexed { argument, it ->
                if (value == it) {
                    argumentsX.add(argument)
                }
            }

            if (printDebug) {
                argumentsX.forEach {
                    println("$it\t${y[it]}")
                }

                println("=============")
            }

            val valuesY = arrayListOf<Float>()

            argumentsX.forEach {
                valuesY.add(y[it])
            }

            result.add(valuesY)
        }
        return result
    }
}
