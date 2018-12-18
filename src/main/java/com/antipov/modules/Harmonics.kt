package com.antipov.modules

import org.apache.commons.math3.complex.Complex
import kotlin.math.PI

class Harmonics {


    public fun calculate(complex: Array<Complex>): Pair<FloatArray, FloatArray> {
        val N = complex.size
        val nSamplesPerSec = 374 // 1122 / 3
        val nMax = (N + 1) / 2
        val freq = DoubleArray(nMax)
        val amp = DoubleArray(nMax)
        val phase = DoubleArray(nMax)
        var j = 0 // harmonic index
        var i = 0
        val limit = 0.00001
        val abs2min = limit * limit * N * N

        if (complex[i].real >= limit) {
            amp[j] = complex[i].real / N
            freq[j] = 0.0
            phase[j] = 0.0
            ++j
        }
        ++i

        for (z in 1 until nMax) {
            val re = complex[z].real
            val im = complex[z].imaginary

            //это квадрат модуля комплексного числа arr[i]
            val abs2 = re * re + im * im

            //отбрасываем слишком слабые гармоники
            if (abs2 < abs2min)
                continue

            //вычисляем апмлитуду. 2.0 - для устранения зеркального эффекта
            amp[j] = 2.0 * Math.sqrt(abs2) / N

            //вычисляем фазу косинуса в радианах
            phase[j] = Math.atan2(im, re);

            //преобразуем косинус в синус. M_PI2 = пи/2, M_PI = пи
            //в результате фаза будет в диапазоне от -пи/2 до +пи/2
            phase[j] += PI / 2
            if (phase[j] > PI)
                phase[j] -= PI / 2

            //можно еще преобразовать радианы в градусы
            phase[j] = phase[j] * 180.0 / PI;

            //получаем частоту
            freq[j] = (nSamplesPerSec * z.toDouble()) / N.toDouble()

            ++j

        }

        return Pair(freq.map { it.toFloat() }.toFloatArray(), amp.map { it.toFloat() }.toFloatArray())
    }
}