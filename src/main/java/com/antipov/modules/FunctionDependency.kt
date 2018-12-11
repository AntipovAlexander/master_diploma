package com.antipov.modules

import java.util.*


class FunctionDependency {

    fun calculate(x: ArrayList<Float>, y: ArrayList<Float>, printDebug: Boolean = false): SortedMap<Float, LinkedHashSet<Float>> {
        val valsX = LinkedHashSet<Float>(x)
        val results = LinkedHashMap<Float, LinkedHashSet<Float>>()

        valsX.forEach { value ->
            x.forEachIndexed { argument, it ->
                if (value == it) {
                    if (!results.containsKey(it)) {
                        results[it] = linkedSetOf()
                    }
                    results[it]?.add(y[argument])
                }
            }
        }
        return results.toSortedMap()
    }
}
