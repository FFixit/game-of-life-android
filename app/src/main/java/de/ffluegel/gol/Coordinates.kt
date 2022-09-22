package de.ffluegel.gol

class Coordinates(x: Int, y: Int) {

    private var y: Int
    private val x: Int

    init {
        this.x = x
        this.y = y
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Coordinates) {
            return false
        }
        return (this.x == other.x) && (this.y == other.y)
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + x
        result = 31 * result + y
        return result
    }

    fun getX(): Int {
        return x
    }

    fun getY(): Int {
        return y
    }
}