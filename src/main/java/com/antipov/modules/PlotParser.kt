package com.antipov.modules

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class PlotParser {

    private val dots = arrayListOf<Float>()

    fun parse(filePath: String): ArrayList<Float> {
        try {
            val image = ImageIO.read(File(javaClass.classLoader.getResource(filePath).file))
            marchThroughImage(image)
        } catch (e: IOException) {
            System.err.println(e.message)
        } finally {
            return dots
        }
    }

    private fun marchThroughImage(image: BufferedImage) {
        val w = image.width
        val h = image.height
        for (i in 0 until w) {
            for (j in 0 until h) inner@ {
                val pixel = image.getRGB(i, j)
                if (isPixelBlack(pixel)) {
                    val value = (((h - j).toFloat()) / h.toFloat())
                    dots.add(value)
                    break
                }
            }
        }
    }

    private fun isPixelBlack(pixel: Int): Boolean {
        return (pixel shr 16 and 0xff == 0) && (pixel shr 8 and 0xff == 0) && (pixel and 0xff == 0)
    }
}