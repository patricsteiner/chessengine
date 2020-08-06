package io.github.patricsteiner.chessengine.domain

class Matrix<T>(private val width: Int, private val height: Int) {

    private val data = arrayOfNulls<Any>(width * height)

    operator fun get(row: Int, col: Int): T {
        return data[width * row + col] as T
    }

    operator fun set(row: Int, col: Int, item: T) {
        data[width * row + col] = item
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in 0 until height) {
            for (j in 0 until width) {
                sb.append(data[width * i + j] ?: "\u2610")
                sb.append("\t")
            }
            sb.append("\n")
        }
        return sb.toString()
    }

}
