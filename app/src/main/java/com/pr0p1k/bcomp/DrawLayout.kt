package com.pr0p1k.bcomp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View

class DrawLayout(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    var bitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas?) {
        Log.i("dfh", "dfhdghk")
        val paint = Paint()
        paint.color = resources.getColor(R.color.colorBg)
//        canvas?.drawRect(0f,0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)
//        paint.color = resources.getColor(R.color.busColor)
//        val kek = (Math.random() * 300 + 60).toFloat()
//        canvas?.drawRect(20f, kek, 405f, kek + 20, paint)
//        setWillNotDraw(false)
        if (bitmap != null)
            canvas?.drawBitmap(bitmap, 0f, 0f, paint)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        bitmap = Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.ARGB_8888)
    }

    fun drawBuses(rects: Array<Rect>) {
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = resources.getColor(R.color.busColor)
        for (i in rects)
            canvas.drawRect(i, paint)
        invalidate()
    }
}
