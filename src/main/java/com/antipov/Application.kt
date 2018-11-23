package com.antipov

import com.antipov.modules.*
import com.antipov.utils.printTable
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import java.io.BufferedWriter
import java.io.FileWriter
import kotlin.math.sqrt

class Application {
    companion object {
        private const val PLOTS_FILENAME = "plots.tsv"
        private const val AUTOCORRELATION_FILENAME = "autocorrelation.tsv"
        private const val MUTUALCORRELATION_FILENAME = "mutualcorrelation.tsv"
        private const val SPECTRALDENSITY_FILENAME = "spectraldensity.tsv"
        private const val PERIODOGRAMMS_FILENAME = "periodogramms.tsv"
        private const val NDIST_FILENAME = "normaldist.tsv"

        private const val RED_NAME = "Ток. АТК"
        private const val BLACK_NAME = "Скор. АЗЦК"
        private const val BLUE_NAME = "ЭДС"

        private lateinit var writer: BufferedWriter
        private lateinit var fWriter: FileWriter

        private val timeline = arrayListOf<Int>() // 1, 2, 3, ..., n

        private val i = arrayListOf<Float>() // means Ток. АТК
        private val j = arrayListOf<Float>() // means Скор. АЗЦК
        private val k = arrayListOf<Float>() // means ЭДС

        // math expectations
        private var mxI: Float = 0.0f
        private var mxJ: Float = 0.0f
        private var mxK: Float = 0.0f

        // dispersions
        private var dispI: Float = 0.0f
        private var dispJ: Float = 0.0f
        private var dispK: Float = 0.0f

        // auto correlation vectors
        private val autoI = arrayListOf<Float>()
        private val autoJ = arrayListOf<Float>()
        private val autoK = arrayListOf<Float>()

        // mutual correlation vectors
        private val mutualJtoI = arrayListOf<Float>()
        private val mutualJtoK = arrayListOf<Float>()

        private val mutualItoJ = arrayListOf<Float>()
        private val mutualItoK = arrayListOf<Float>()

        private val mutualKtoJ = arrayListOf<Float>()
        private val mutualKtoI = arrayListOf<Float>()

        // spectral density
        private val spectralDensityI = arrayListOf<Float>()
        private val spectralDensityJ = arrayListOf<Float>()
        private val spectralDensityK = arrayListOf<Float>()

        // periodogram
        private val periodI = arrayListOf<Float>()
        private val periodJ = arrayListOf<Float>()
        private val periodK = arrayListOf<Float>()

        // normal distribution
        private val normalI = arrayListOf<Float>()
        private val normalJ = arrayListOf<Float>()
        private val normalK = arrayListOf<Float>()

        @JvmStatic
        fun main(args: Array<String>) {
            parsePlots()
            writePlotsToResults()

            initTimeLine()
            calculateMathExpectation()
            calculateDispersion()
            calculateAutoCorrelation()
            writeAutoCorrelationToResults()

            calculateMutualCorrelation()
            writeMutualCorrelationToResults()

            calculateSpectralDensity()
            writeSpectralDensityToResults()

            calculatePeriodogramms()
            writePeriodogrammsToResults()

            calculateNormalDistributions()
            writeNormalDistributionsToResults()

            closeFileWriter()
        }

        private fun calculateNormalDistributions() {
            i.forEach {
                val dist = NormalDistribution(mxI.toDouble(), sqrt(dispI).toDouble())
                normalI.add(dist.density(it.toDouble()).toFloat())
            }

            j.forEach {
                val dist = NormalDistribution(mxJ.toDouble(), sqrt(dispJ).toDouble())
                normalJ.add(dist.density(it.toDouble()).toFloat())
            }


            k.forEach {
                val dist = NormalDistribution(mxK.toDouble(), sqrt(dispK).toDouble())
                normalK.add(dist.density(it.toDouble()).toFloat())
            }
        }

        private fun writeNormalDistributionsToResults() {
            initFileWriter(NDIST_FILENAME)
            // assuming that vectors lengths are equal
            writer.write("Normal distribution\n")
            writer.printTable(RED_NAME, BLACK_NAME, BLUE_NAME)
            normalI.forEachIndexed { index, normalI ->
                writer.printTable(normalI, normalJ[index], normalK[index])
            }
        }

        private fun calculatePeriodogramms() {
            val doubleArray = DoubleArray(512)
            val fft = FastFourierTransformer(DftNormalization.STANDARD)

            for (i in 0 until 512) {
                doubleArray[i] = this.i[i].toDouble()
            }

            periodI.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until 512) {
                doubleArray[i] = this.j[i].toDouble()
            }

            periodJ.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until 512) {
                doubleArray[i] = this.k[i].toDouble()
            }

            periodK.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })
        }

        private fun writePeriodogrammsToResults() {
            initFileWriter(PERIODOGRAMMS_FILENAME)
            // assuming that vectors lengths are equal
            writer.write("Periodogramms\n")
            writer.printTable(RED_NAME, BLACK_NAME, BLUE_NAME)
            periodI.forEachIndexed { index, periodI ->
                writer.printTable(periodI, periodJ[index], periodK[index])
            }
        }

        private fun writeSpectralDensityToResults() {
            initFileWriter(SPECTRALDENSITY_FILENAME)
            // assuming that vectors lengths are equal
            writer.write("Spectral density\n")
            writer.printTable(RED_NAME, BLACK_NAME, BLUE_NAME)
            spectralDensityI.forEachIndexed { index, spectralDensityI ->
                writer.printTable(spectralDensityI, spectralDensityJ[index], spectralDensityK[index])
            }
        }

        private fun calculateSpectralDensity() {
            val doubleArray = DoubleArray(512)
            val fft = FastFourierTransformer(DftNormalization.STANDARD)

            for (i in 0 until 512) {
                doubleArray[i] = this.autoI[i].toDouble()
            }

            spectralDensityI.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until 512) {
                doubleArray[i] = this.autoJ[i].toDouble()
            }

            spectralDensityJ.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until 512) {
                doubleArray[i] = this.autoK[i].toDouble()
            }

            spectralDensityK.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })
        }

        private fun writeMutualCorrelationToResults() {
            initFileWriter(MUTUALCORRELATION_FILENAME)
            // assuming that vectors lengths are equal
            writer.write("Mutual Correlation\n")
            writer.printTable(BLACK_NAME, BLACK_NAME, RED_NAME, RED_NAME, BLUE_NAME, BLUE_NAME)
            writer.printTable(RED_NAME, BLUE_NAME, BLACK_NAME, BLUE_NAME, BLACK_NAME, RED_NAME)
            mutualJtoI.forEachIndexed { index, mutualJtoI ->
                writer.printTable(mutualJtoI, mutualJtoK[index], mutualItoJ[index], mutualItoK[index], mutualKtoJ[index], mutualKtoI[index])
            }
        }

        private fun calculateMutualCorrelation() {
            val mc = Mutualcorrelation()

            mutualJtoI.addAll(mc.calculate(mxJ, mxI, j, i))
            mutualJtoK.addAll(mc.calculate(mxJ, mxK, j, k))

            mutualItoJ.addAll(mc.calculate(mxI, mxJ, i, j))
            mutualItoK.addAll(mc.calculate(mxI, mxK, i, k))

            mutualKtoJ.addAll(mc.calculate(mxK, mxJ, k, j))
            mutualKtoI.addAll(mc.calculate(mxK, mxI, k, i))
        }


        private fun writeAutoCorrelationToResults() {
            initFileWriter(AUTOCORRELATION_FILENAME)
            // assuming that vectors lengths are equal
            writer.write("Auto Correlation\n")
            writer.printTable(RED_NAME, BLACK_NAME, BLUE_NAME)
            autoI.forEachIndexed { index, it ->
                writer.printTable(it, autoJ[index], autoK[index])
            }
        }

        private fun calculateDispersion() {
            dispI = Dispersion()
                    .calculate(mxI, i)
            dispJ = Dispersion().calculate(mxJ, j)
            dispK = Dispersion().calculate(mxK, k)
        }

        private fun calculateMathExpectation() {
            mxI = MathExpectation().calculate(i)
            mxJ = MathExpectation().calculate(j)
            mxK = MathExpectation().calculate(k)
        }

        private fun calculateAutoCorrelation() {
            autoI.addAll(Autocorrelation().calculate(mxI, dispI, i))
            autoJ.addAll(Autocorrelation().calculate(mxJ, dispJ, j))
            autoK.addAll(Autocorrelation().calculate(mxK, dispK, k))
        }

        private fun closeFileWriter() {
            writer.close()
            fWriter.close()
        }

        private fun writePlotsToResults() {
            initFileWriter(PLOTS_FILENAME)
            // assuming that vectors lengths are equal
            writer.write("Parsed plots\n")
            writer.printTable(RED_NAME, BLACK_NAME, BLUE_NAME)
            i.forEachIndexed { index, it ->
                writer.printTable(it, j[index], k[index])
            }
        }

        private fun initFileWriter(name: String) {
            fWriter = FileWriter(name, false)
            writer = BufferedWriter(fWriter)
        }

        private fun initTimeLine() {
            // assuming that vectors lengths are equal
            i.forEachIndexed { index, _ ->
                timeline.add(index + 1)
            }
        }

        private fun parsePlots() {
            i.addAll(PlotParser().parse("red.bmp"))
            j.addAll(PlotParser().parse("black.bmp"))
            k.addAll(PlotParser().parse("blue.bmp"))
        }
    }
}

