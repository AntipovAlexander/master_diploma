package com.antipov

import com.antipov.modules.PlotParser

class Application {
    companion object {
        private val timeline = arrayListOf<Int>() // 1, 2, 3, ..., n

        private val i = arrayListOf<Float>() // means Скор. АЗЦК
        private val j = arrayListOf<Float>() // means Ток. АТК
        private val k = arrayListOf<Float>() // means ЭДС

        @JvmStatic
        fun main(args: Array<String>) {
            parsePlots()
            initTimeLine()
        }

        private fun initTimeLine() {
            i.forEachIndexed { index, _ ->
                timeline.add(index + 1)
            }
        }

        private fun parsePlots() {
            i.addAll(PlotParser().parse("black.bmp"))
            j.addAll(PlotParser().parse("red.bmp"))
            k.addAll(PlotParser().parse("blue.bmp"))
        }
    }
}