package com.antipov.modules

import org.apache.commons.math3.distribution.NormalDistribution
import kotlin.math.sqrt

class NormalDistribution {

    fun calculate(mx: Float, d: Float, i: ArrayList<Float>): ArrayList<Float> {
        val array = arrayListOf<Float>()
        val dist = NormalDistribution(mx.toDouble(), sqrt(d).toDouble())
        var min = mx - mx
        val max = mx + mx
        val step = 1000.0f
        val increment = (max - min) / step

        while (min <= max) {
            array.add(dist.density(min.toDouble()).toFloat())
            min += increment
        }
        return array
    }
}