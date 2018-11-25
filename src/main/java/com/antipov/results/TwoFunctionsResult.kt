package com.antipov.results

data class TwoFunctionsResult(
        val values: ArrayList<Float>,
        val mx: Float,
        val dispersion: Float,
        val skewness: Float,
        val kurtosis: Float
)
