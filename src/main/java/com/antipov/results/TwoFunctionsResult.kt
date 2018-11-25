package com.antipov.results

data class TwoFunctionsResult(
        val values: ArrayList<Float>,
        val mx: Float,
        val dispersion: Float,
        val skewness: Float,
        val kurtosis: Float
) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("${javaClass.simpleName}\n")
        sb.append("Mat expectation: $mx\n")
        sb.append("Dispersion: $dispersion\n")
        sb.append("Skewness: $skewness\n")
        sb.append("Kurtosis: $kurtosis\n")
        sb.append("\nVector values:\n")
        values.forEach {
            sb.append(it)
            sb.append("\n")
        }
        sb.append("-------------------")
        sb.append("\n")
        return sb.toString()
    }
}
