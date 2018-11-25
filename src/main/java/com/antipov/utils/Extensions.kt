package com.antipov.utils

import java.io.BufferedWriter

fun Float.comma() = this.toString().replace(".", ",")

fun BufferedWriter.printTable(i: Float, j: Float, k: Float) {
    write("${i.comma()}\t${j.comma()}\t${k.comma()}\n")
}

fun BufferedWriter.printTable(i: String, j: String, k: String) {
    write("$i\t$j\t$k\n")
}

fun BufferedWriter.printTable(i: String, j: String, k: String, z: String, x: String, y: String) {
    write("$i\t$j\t$k\t$z\t$x\t$y\n")
}

fun BufferedWriter.printTable(i: Float, j: Float, k: Float, z: Float, x: Float, y: Float) {
    write("${i.comma()}\t${j.comma()}\t${k.comma()}\t${z.comma()}\t${x.comma()}\t${y.comma()}\n")
}

fun Int.closestPowerOfTwo() = if (this == 1) 1 else Integer.highestOneBit(this - 1)