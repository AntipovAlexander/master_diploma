package com.antipov

import com.antipov.modules.PlotParser
import java.io.BufferedWriter
import java.io.FileWriter

class Application {
    companion object {
        private const val RESULT_FILENAME = "results.tsv"

        private const val RED_NAME = "Ток. АТК"
        private const val BLACK_NAME = "Скор. АЗЦК"
        private const val BLUE_NAME = "ЭДС"

        private lateinit var writer: BufferedWriter
        private lateinit var fWriter: FileWriter

        private val timeline = arrayListOf<Int>() // 1, 2, 3, ..., n

        private val i = arrayListOf<Float>() // means Ток. АТК
        private val j = arrayListOf<Float>() // means Скор. АЗЦК
        private val k = arrayListOf<Float>() // means ЭДС

        @JvmStatic
        fun main(args: Array<String>) {
            initFileWriter()
            parsePlots()
            writePlotsToResults()
            initTimeLine()
            closeFileWriter()
        }

        private fun closeFileWriter() {
            writer.close()
            fWriter.close()
        }

        private fun writePlotsToResults() {
            // assuming that vectors lengths are equal
            writer.write("Parsed plots\n")
            writer.write("$RED_NAME\t$BLACK_NAME\t$BLUE_NAME\n")
            i.forEachIndexed { index, it ->
                writer.write("$it\t${j[index]}\t${k[index]}\n")
            }
        }

        private fun initFileWriter() {
            fWriter = FileWriter(RESULT_FILENAME, false)
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