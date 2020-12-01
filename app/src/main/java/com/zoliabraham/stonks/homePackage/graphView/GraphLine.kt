package com.zoliabraham.stonks.homePackage.graphView

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.pow

class GraphLine (private val p1: GraphPoint, private val p2: GraphPoint, distance: Float, maxDistance: Float) {
    private var thickness = 1f
    private var alpha = 1f
    init {
        thickness = if(p1.radius < p2.radius) p1.radius/2f else p2.radius/2f
        if(thickness < 1f) {
            thickness = 1f
        }
        val alphaMultiplier = ((distance/maxDistance))
        val y = (alphaMultiplier-1).pow(2)
        alpha = 220f* y
    }

    fun draw(canvas: Canvas, paint: Paint){
        paint.strokeWidth = thickness
        val color = paint.color
        paint.color = Color.argb(alpha.toInt(), color.red, color.green, color.blue)
        canvas.drawLine(p1.position.x,p1.position.y,p2.position.x,p2.position.y,paint)
    }
}