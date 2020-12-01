@file:Suppress("unused")

package com.zoliabraham.stonks.homePackage.graphView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.zoliabraham.stonks.R
import kotlin.random.Random

class GraphView @JvmOverloads constructor( context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var framesPerSecond = 60
    private val paint: Paint
    private val pointList = ArrayList<GraphPoint>()
    private val lineList = ArrayList<GraphLine>()
    var size = Vector()
    private var startTime = 0L

    private var numOfPoints = 20
    private val maxDistance = 800F
    private val minRadius = 1
    private val maxRadius = 5
    private val minSpeed = 50F
    private val maxSpeed = 100F
    private val extendedMargin = 200F

    private var canvasBackGroundColor = Color.argb(255,0,0,0)
    private var colorDots = Color.argb(255,255,255,255)
    private var colorLines = Color.argb(255,255,255,255)

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.GraphView,0,0).apply {
            canvasBackGroundColor = getColor(R.styleable.GraphView_canvasBackgroundColor, Color.argb(255,0,0,0))
            colorDots = getColor(R.styleable.GraphView_graphPointColor, Color.argb(255,255,255,255))
            colorLines = getColor(R.styleable.GraphView_graphLineColor, Color.argb(255,255,255,255))
            numOfPoints = getInteger(R.styleable.GraphView_numberOfDots, 40)
        }
        startTime = System.currentTimeMillis()
        paint = Paint()
        //createPoints()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        this.size.set(w.toFloat(),h.toFloat())
        super.onSizeChanged(w, h, oldw, oldh)
        createPoints()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        val currentTime = System.currentTimeMillis()
        val deltaT = (currentTime - startTime).toFloat()/1000f
        startTime = currentTime

        movePoints(deltaT)
        calculateLines()
        //calculateTriangles()

        canvas?.drawARGB(canvasBackGroundColor.alpha, canvasBackGroundColor.red, canvasBackGroundColor.green, canvasBackGroundColor.blue)

        paint.isAntiAlias = true
        paint.strokeWidth = 80f

        if (canvas != null) {
            //drawTriangles(canvas, paint)
            drawLines(canvas, paint)
            drawPoints(canvas, paint)
        }

        this.postInvalidateDelayed(1000L/framesPerSecond)
    }

    private fun drawPoints(canvas: Canvas, paint: Paint) {
        paint.color = colorDots
        pointList.forEach {
            it.draw(canvas, paint)
        }
    }

    private fun drawLines(canvas: Canvas, paint: Paint) {
        paint.color = colorLines
        for( l in lineList){
            l.draw(canvas, paint)
        }
    }

    private fun calculateLines() {
        clearNeighbors()
        lineList.clear()
        for(p1 in pointList){
            for(p2 in pointList){
                val distance = p1.position.getDistance(p2.position)
                if (p1 != p2 && distance < maxDistance) {
                    if((!p1.neighborPoints.contains(p2) && !p2.neighborPoints.contains(p1))) { //no double lines
                        lineList.add(GraphLine(p1, p2, distance, maxDistance))
                        p1.neighborPoints.add(p2)
                        p2.neighborPoints.add(p1)
                    }
                }
            }
        }
    }


    private fun movePoints(deltaT: Float) {
        pointList.forEach {
            it.move(deltaT)
        }
    }


    private fun createPoints() {
        if(pointList.isEmpty()) {
            for (x in 0..numOfPoints) {
                val rX = Random.nextFloat() * size.x
                val rY = Random.nextFloat() * size.y
                val rDirX = (Random.nextFloat() - 0.5f) * 2f
                val rDirY = (Random.nextFloat() - 0.5f) * 2f
                val radius = minRadius + Random.nextFloat() * maxRadius
                val speed = minSpeed + Random.nextFloat() * maxSpeed
                pointList.add(GraphPoint(Vector(rX, rY), Vector(rDirX, rDirY), size, speed, radius, extendedMargin))
            }
        }
    }


    private fun clearNeighbors() {
        pointList.forEach {
            it.neighborPoints.clear()
        }
    }

}
