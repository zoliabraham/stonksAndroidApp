package com.zoliabraham.stonks.homePackage.graphView

import kotlin.math.abs
import kotlin.math.sqrt

class Vector (
    var x:Float = 0f,
    var y:Float = 0f
){
    fun normalize(): Vector {
        x /= getLength()
        y /= getLength()
        return this
    }

    private fun getLength(): Float {
        return sqrt(x*x + y*y)
    }

    fun set(x: Float , y: Float){
        this.x = x
        this.y = y
    }

    fun getDistance(vector: Vector): Float {
        val dx = abs(x-vector.x)
        val dy = abs(y- vector.y)

        return sqrt(dx*dx + dy*dy)
    }

    operator fun times(f:Float): Vector {
        return Vector(x*f , y*f)
    }

    operator fun plus(v: Vector): Vector {
        return Vector(x+v.x, y+v.y)
    }
}