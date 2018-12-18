package com.antipov

import com.antipov.modules.*
import com.antipov.utils.closestPowerOfTwo
import com.antipov.utils.printTable
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis
import org.apache.commons.math3.stat.descriptive.moment.Skewness
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import java.io.BufferedWriter
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList
import java.text.DecimalFormat



class Application {
    companion object {
        private const val PLOTS_FILENAME = "plots.tsv"
        private const val AUTOCORRELATION_FILENAME = "autocorrelation.tsv"
        private const val MUTUALCORRELATION_FILENAME = "mutualcorrelation.tsv"
        private const val SPECTRALDENSITY_FILENAME = "spectraldensity.tsv"
        private const val MUTUALSPECTRAL_FILENAME = "mutualspectraldensity.tsv"
        private const val PERIODOGRAMMS_FILENAME = "periodogramms.tsv"
        private const val NDIST_FILENAME = "normaldist.tsv"

        private const val DEPENDENCY_FILENAME_I_J = "dependecy_i_j.tsv"
        private const val DEPENDENCY_FILENAME_I_K = "dependecy_i_k.tsv"
        private const val DEPENDENCY_FILENAME_J_I = "dependecy_j_i.tsv"
        private const val DEPENDENCY_FILENAME_J_K = "dependecy_j_k.tsv"
        private const val DEPENDENCY_FILENAME_K_J = "dependecy_k_j.tsv"
        private const val DEPENDENCY_FILENAME_K_I = "dependecy_k_i.tsv"

        private const val DEPENDENCY_STATS_FILENAME_I_J = "dependecy_stats_i_j.tsv"
        private const val DEPENDENCY_STATS_FILENAME_I_K = "dependecy_stats_i_k.tsv"
        private const val DEPENDENCY_STATS_FILENAME_J_I = "dependecy_stats_j_i.tsv"
        private const val DEPENDENCY_STATS_FILENAME_J_K = "dependecy_stats_j_k.tsv"
        private const val DEPENDENCY_STATS_FILENAME_K_J = "dependecy_stats_k_j.tsv"
        private const val DEPENDENCY_STATS_FILENAME_K_I = "dependecy_stats_k_i.tsv"


        private const val RED_NAME = "Ток. АТК"
        private const val BLACK_NAME = "Скор. АЗЦК"
        private const val BLUE_NAME = "ЭДС"

        private var DFT_SIZE = 1024

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
        private lateinit var spectralDensityI : Pair<FloatArray, FloatArray>
        private lateinit var spectralDensityJ : Pair<FloatArray, FloatArray>
        private lateinit var spectralDensityK : Pair<FloatArray, FloatArray>

        // spectral density mutual correlation
        private val mutualSpectralDensityJtoI = arrayListOf<Float>()
        private val mutualSpectralDensityJtoK = arrayListOf<Float>()

        private val mutualSpectralDensityItoJ = arrayListOf<Float>()
        private val mutualSpectralDensityItoK = arrayListOf<Float>()

        private val mutualSpectralDensityKtoJ = arrayListOf<Float>()
        private val mutualSpectralDensityKtoI = arrayListOf<Float>()

        // periodogram
        private val periodI = arrayListOf<Float>()
        private val periodJ = arrayListOf<Float>()
        private val periodK = arrayListOf<Float>()

        // normal distribution
        private val normalI = arrayListOf<Float>()
        private val normalJ = arrayListOf<Float>()
        private val normalK = arrayListOf<Float>()

        // functions dependency
        private var dependency = sortedMapOf<Float, LinkedHashSet<Float>>()

        private var functionalDependencyItoJ = sortedMapOf<Float, LinkedHashSet<Float>>()
        private var functionalDependencyItoK = sortedMapOf<Float, LinkedHashSet<Float>>()
        private var functionalDependencyJtoI = sortedMapOf<Float, LinkedHashSet<Float>>()
        private var functionalDependencyJtoK = sortedMapOf<Float, LinkedHashSet<Float>>()
        private var functionalDependencyKtoJ = sortedMapOf<Float, LinkedHashSet<Float>>()
        private var functionalDependencyKtoI = sortedMapOf<Float, LinkedHashSet<Float>>()


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

            calculateSpectralDensityForMutualCorrelation()
            writeSpectralDensityForMutualCorrelationToResults()

            calculatePeriodogramms()
            writePeriodogrammsToResults()

            calculateNormalDistributions()
            writeNormalDistributionsToResults()

            calculateFunctionalDependencies()
            writeFunctionDependenciesToFile(i, j, DEPENDENCY_FILENAME_I_J)
            writeFunctionDependenciesToFile(i, k, DEPENDENCY_FILENAME_I_K)
            writeFunctionDependenciesToFile(j, i, DEPENDENCY_FILENAME_J_I)
            writeFunctionDependenciesToFile(j, k, DEPENDENCY_FILENAME_J_K)
            writeFunctionDependenciesToFile(k, j, DEPENDENCY_FILENAME_K_J)
            writeFunctionDependenciesToFile(k, i, DEPENDENCY_FILENAME_K_I)

            writeFunctionDependenciesMxDispStatisticToFile(functionalDependencyItoJ, DEPENDENCY_STATS_FILENAME_I_J)
            writeFunctionDependenciesMxDispStatisticToFile(functionalDependencyItoK, DEPENDENCY_STATS_FILENAME_I_K)
            writeFunctionDependenciesMxDispStatisticToFile(functionalDependencyJtoI, DEPENDENCY_STATS_FILENAME_J_I)
            writeFunctionDependenciesMxDispStatisticToFile(functionalDependencyJtoK, DEPENDENCY_STATS_FILENAME_J_K)
            writeFunctionDependenciesMxDispStatisticToFile(functionalDependencyKtoJ, DEPENDENCY_STATS_FILENAME_K_J)
            writeFunctionDependenciesMxDispStatisticToFile(functionalDependencyKtoI, DEPENDENCY_STATS_FILENAME_K_I)

        }

        private fun writeFunctionDependenciesMxDispStatisticToFile(dataset: SortedMap<Float, LinkedHashSet<Float>>, filename: String) {
            val calculatorMX = MathExpectation()
            val calculatorDisp = Dispersion()
            val calculatorSkewness = Skewness()
            val calculatorKurtosis = Kurtosis()
            val mxResults = arrayListOf<Float>()
            val dispResults = arrayListOf<Float>()
            val skewness = arrayListOf<Float>()
            val kurtosis = arrayListOf<Float>()
            dataset.keys.forEachIndexed { index, it ->
                mxResults.add(calculatorMX.calculate(ArrayList(dataset[it])))
                dispResults.add(calculatorDisp.calculate(mxResults[index], ArrayList(dataset[it])))
                dataset[it]?.let {
                    val array = DoubleArray(it.size)
                    it.forEachIndexed { index, value ->
                        array[index] = value.toDouble()
                    }
                    val res = calculatorSkewness.evaluate(array)
                    skewness.add(res.toFloat())
                }
                dataset[it]?.let {
                    val array = DoubleArray(it.size)
                    it.forEachIndexed { index, value ->
                        array[index] = value.toDouble()
                    }
                    val res = calculatorKurtosis.evaluate(array)
                    kurtosis.add(res.toFloat())
                }
            }
            initFileWriter(filename)
            writer.write("Math expectation\tDispersion\tSkewness\tKurtosis")
            writer.newLine()
            mxResults.forEachIndexed { index, result ->
                synchronized(this) {
                    val skewnessValue = if (skewness[index].isNaN()) 0f else skewness[index]
                    val kurtosisValue = if (kurtosis[index].isNaN()) 0f else kurtosis[index]
                    writer.write("${result.toString().replace(".", ",")}\t" +
                            "${dispResults[index].toString().replace(".", ",")}\t" +
                            "${kurtosisValue.toString().replace(".", ",")}\t" +
                            skewnessValue.toString().replace(".", ","))
                    writer.newLine()
                }
            }
            closeFileWriter()

        }

        private fun writeFunctionDependenciesToFile(i: ArrayList<Float>, j: ArrayList<Float>, filename: String) {
            dependency = FunctionDependency().calculate(i, j)
            initFileWriter(filename)
            dependency.keys.forEach {
                Companion.writer.write("${it.toString().replace(".", ",")}\t")
                dependency[it]?.forEach { value ->
                    Companion.writer.write("${value.toString().replace(".", ",")}\t")
                }
                Companion.writer.write("\n")
            }
            closeFileWriter()
        }

        private fun calculateFunctionalDependencies() {
            val calculator = FunctionDependency()
            functionalDependencyItoJ = calculator.calculate(i, j)
            functionalDependencyItoK = calculator.calculate(i, k)
            functionalDependencyJtoI = calculator.calculate(j, i)
            functionalDependencyJtoK = calculator.calculate(j, k)
            functionalDependencyKtoJ = calculator.calculate(k, j)
            functionalDependencyKtoI = calculator.calculate(k, i)
        }

        private fun writeSpectralDensityForMutualCorrelationToResults() {
            initFileWriter(MUTUALSPECTRAL_FILENAME)
            // assuming that vectors lengths are equal
            writer.write("Mutual spectral density\n")
            writer.printTable(BLACK_NAME, BLACK_NAME, RED_NAME, RED_NAME, BLUE_NAME, BLUE_NAME)
            writer.printTable(RED_NAME, BLUE_NAME, BLACK_NAME, BLUE_NAME, BLACK_NAME, RED_NAME)
            mutualSpectralDensityJtoI.forEachIndexed { index, mutualSpectralDensityJtoI ->
                writer.printTable(mutualSpectralDensityJtoI, mutualSpectralDensityJtoK[index], mutualSpectralDensityItoJ[index], mutualSpectralDensityItoK[index], mutualSpectralDensityKtoJ[index], mutualSpectralDensityKtoI[index])
            }
            closeFileWriter()
        }

        private fun calculateSpectralDensityForMutualCorrelation() {
            DFT_SIZE = autoI.size.closestPowerOfTwo()
            val doubleArray = DoubleArray(DFT_SIZE)
            val fft = FastFourierTransformer(DftNormalization.STANDARD)

            for (i in 0 until DFT_SIZE) {
                doubleArray[i] = this.mutualJtoI[i].toDouble()
            }

            mutualSpectralDensityJtoI.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until DFT_SIZE) {
                doubleArray[i] = this.mutualJtoK[i].toDouble()
            }

            mutualSpectralDensityJtoK.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until DFT_SIZE) {
                doubleArray[i] = this.mutualItoJ[i].toDouble()
            }

            mutualSpectralDensityItoJ.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until DFT_SIZE) {
                doubleArray[i] = this.mutualItoK[i].toDouble()
            }

            mutualSpectralDensityItoK.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until DFT_SIZE) {
                doubleArray[i] = this.mutualKtoJ[i].toDouble()
            }

            mutualSpectralDensityKtoJ.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until DFT_SIZE) {
                doubleArray[i] = this.mutualKtoI[i].toDouble()
            }

            mutualSpectralDensityKtoI.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })
        }

        private fun calculateNormalDistributions() {
            normalI.addAll(NormalDistribution().calculate(mxI, dispI, i))
            normalJ.addAll(NormalDistribution().calculate(mxJ, dispJ, j))
            normalK.addAll(NormalDistribution().calculate(mxK, dispK, k))
        }

        private fun writeNormalDistributionsToResults() {
            initFileWriter(NDIST_FILENAME)
            // assuming that vectors lengths are equal
            writer.write("Normal distribution\n")
            writer.printTable(RED_NAME, BLACK_NAME, BLUE_NAME)
            normalI.forEachIndexed { index, normalI ->
                writer.printTable(normalI, normalJ[index], normalK[index])
            }
            closeFileWriter()
        }

        private fun calculatePeriodogramms() {
            DFT_SIZE = i.size.closestPowerOfTwo()

            val doubleArray = DoubleArray(DFT_SIZE)
            val fft = FastFourierTransformer(DftNormalization.STANDARD)

            for (i in 0 until DFT_SIZE) {
                doubleArray[i] = this.i[i].toDouble()
            }

            periodI.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until 512) {
                doubleArray[i] = this.j[i].toDouble()
            }

            periodJ.addAll(fft.transform(doubleArray, TransformType.FORWARD).map { it.real.toFloat() })

            for (i in 0 until DFT_SIZE) {
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
            closeFileWriter()
        }

        private fun writeSpectralDensityToResults() {
            initFileWriter(SPECTRALDENSITY_FILENAME)
            // assuming that vectors lengths are equal
            writer.write("Spectral density\n")
            writer.printTable(RED_NAME, BLACK_NAME, BLUE_NAME)
            writer.write("$RED_NAME - freq\t$RED_NAME - amp\t$BLACK_NAME- freq\t$BLACK_NAME- amp\t$BLUE_NAME - freq\t$BLUE_NAME-amp\n")
            spectralDensityI.first.forEachIndexed { index, scI ->
                writer.write(
                        scI.toString().replace(".", ",") +
                        "\t${spectralDensityI.second[index].toString().replace(".", ",")}" +
                        "\t${spectralDensityJ.first[index].toString().replace(".", ",")}" +
                        "\t${spectralDensityJ.second[index].toString().replace(".", ",")}" +
                        "\t${spectralDensityK.first[index].toString().replace(".", ",")}" +
                        "\t${spectralDensityK.second[index].toString().replace(".", ",")}\n")
            }
            closeFileWriter()
        }

        private fun calculateSpectralDensity() {
            DFT_SIZE = autoI.size.closestPowerOfTwo()
            val harmonicCalculator = Harmonics()

            val doubleArray = DoubleArray(DFT_SIZE)
            val fft = FastFourierTransformer(DftNormalization.STANDARD)

            for (i in 0 until DFT_SIZE) {
                doubleArray[i] = this.autoI[i].toDouble()
            }


            spectralDensityI = harmonicCalculator.calculate(fft.transform(doubleArray, TransformType.FORWARD))

            for (i in 0 until DFT_SIZE) {
                doubleArray[i] = this.autoJ[i].toDouble()
            }

            spectralDensityJ = harmonicCalculator.calculate(fft.transform(doubleArray, TransformType.FORWARD))

            for (i in 0 until DFT_SIZE) {
                doubleArray[i] = this.autoK[i].toDouble()
            }

            spectralDensityK = harmonicCalculator.calculate(fft.transform(doubleArray, TransformType.FORWARD))
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
            closeFileWriter()
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
            closeFileWriter()
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
            closeFileWriter()
        }

        private fun initFileWriter(name: String) {
            fWriter = FileWriter(name)
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

