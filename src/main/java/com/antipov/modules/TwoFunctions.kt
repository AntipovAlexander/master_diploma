package com.antipov.modules

import com.antipov.results.TwoFunctionsResult
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis
import org.apache.commons.math3.stat.descriptive.moment.Skewness
import java.util.HashSet


class TwoFunctions {

    fun calculate(x: ArrayList<Float>, y: ArrayList<Float>, printDebug: Boolean = false): ArrayList<TwoFunctionsResult> {
        val valsX = HashSet<Float>(x)
        val results = arrayListOf<TwoFunctionsResult>()
        val mxCalculator = MathExpectation()
        val dispersionCalculator = Dispersion()
        val skewnessCalculator = Skewness()
        val kurtosisCalculator = Kurtosis()

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

            val mx = mxCalculator.calculate(valuesY)

            val result = TwoFunctionsResult(
                    valuesY,
                    mx,
                    dispersionCalculator.calculate(mx, valuesY),
                    skewnessCalculator.evaluate(valuesY.map { it.toDouble() }.toDoubleArray(), 0, valuesY.size).toFloat(),
                    kurtosisCalculator.evaluate(valuesY.map { it.toDouble() }.toDoubleArray(), 0, valuesY.size).toFloat()
            )

            results.add(result)
        }
        return results
    }
}
