package com.zoliabraham.stonks.homePackage.graphView

import android.graphics.Canvas
import android.graphics.Paint

class GraphPoint(var position: Vector, private var direction: Vector, private val canvasSize: Vector, private val speed: Float, val radius: Float, private val margin: Float = 0f) {
    val neighborPoints = ArrayList<GraphPoint>()
    init {
        direction.normalize()
    }

    fun move(deltaT: Float){
        position += direction * speed * deltaT

        if(position.x < -margin || position.x > canvasSize.x + margin){
            direction.x *=-1
        }
        if(position.y  < -margin || position.y > canvasSize.y + margin){
            direction.y *= -1
        }
    }

    fun draw(canvas: Canvas, paint: Paint){
        canvas.drawCircle(position.x, position.y, radius, paint)
    }
}