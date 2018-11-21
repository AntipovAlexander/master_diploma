package com.antipov.utils

import java.io.BufferedWriter

fun Float.comma() = this.toString().replace(".", ",")

fun BufferedWriter.printTable(i: Float, j: Float, k: Float) {
    write("${i.comma()}\t${j.comma()}\t${k.comma()}\n")
}

fun BufferedWriter.printTable(i: String, j: String, k: String) {
    write("$i\t$j\t$k\n")
}