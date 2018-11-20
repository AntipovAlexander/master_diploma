package com.antipov.modules

class Mutualcorrelation {
    fun calculate(mx: Float, my: Float, x: ArrayList<Float>, y: ArrayList<Float>): ArrayList<Float> {

        val n = x.size
        val limit = (x.size * 2) / 3

        val mutualcorrelation = arrayListOf<Float>()

        for (t in 0 until limit) {
            var sum = 0.0f
            for (i in 0 until n - t) {
                sum += (x[i] - mx) * (y[i + t] - my)
            }
            sum *= (1.0f / ((n - t).toFloat()))
            mutualcorrelation.add(sum)
        }
        return mutualcorrelation
    }
}