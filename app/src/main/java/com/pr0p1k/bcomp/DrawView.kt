package com.pr0p1k.bcomp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DrawView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    override fun onDraw(canvas: Canvas?) {
        val paint = Paint()
        paint.color = resources.getColor(R.color.colorBg)
        canvas?.drawRect(0f,0f, canvas.width.toFloat(), canvas.width.toFloat(), paint)
        paint.color = resources.getColor(R.color.busColor)
        val kek = (Math.random() * 300 + 60).toFloat()
        canvas?.drawRect(20f, kek, 405f, kek + 20, paint)
    }
}
