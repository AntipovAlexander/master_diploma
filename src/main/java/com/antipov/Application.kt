package com.antipov

import com.antipov.modules.Autocorrelation
import com.antipov.modules.Dispersion
import com.antipov.modules.MathExpectation
import com.antipov.modules.PlotParser
import com.antipov.utils.comma
import com.antipov.utils.printTable
import java.io.BufferedWriter
import java.io.FileWriter

class Application {
    companion object {
        private const val PLOTS_FILENAME = "plots.tsv"

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

        @JvmStatic
        fun main(args: Array<String>) {
            initFileWriter()

            parsePlots()
            writePlotsToResults()
            initTimeLine()
            calculateMathExpectation()
            calculateDispersion()
            calculateAutoCorrelation()
            writeAutoCorrelationToResults()
            closeFileWriter()
        }

        private fun writeAutoCorrelationToResults() {
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
            // assuming that vectors lengths are equal
            writer.write("Parsed plots\n")
            writer.printTable(RED_NAME, BLACK_NAME, BLUE_NAME)
            i.forEachIndexed { index, it ->
                writer.printTable(it, j[index], k[index])
            }
        }

        private fun initFileWriter() {
            fWriter = FileWriter(PLOTS_FILENAME, false)
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
