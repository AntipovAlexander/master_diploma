package com.antipov.modules

class Autocorrelation {
    fun calculate(mx: Float, disp: Float, x: ArrayList<Float>): ArrayList<Float> {

        val n = x.size
        val limit = (x.size * 4) / 5

        val autocorrelation = arrayListOf<Float>()

        for (t in 0 until limit) {
            var sum = 0.0f
            for (i in 0 until n - t) {
                sum += (x[i] - mx) * (x[i + t] - mx)
            }
            sum *= (1.0f / ((n - t).toFloat()))
            autocorrelation.add(sum / disp)
        }
        return autocorrelation
    }
}