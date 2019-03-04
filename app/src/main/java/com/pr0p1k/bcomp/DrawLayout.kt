package com.pr0p1k.bcomp

import android.content.Context
import android.graphics.*
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View

class DrawLayout(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    var bitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas?) {
        val paint = Paint()
        paint.color = resources.getColor(R.color.colorBg)
        if (bitmap != null)
            canvas?.drawBitmap(bitmap, 0f, 0f, paint)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        bitmap = Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.ARGB_8888)
    }

    fun drawBuses(rects: Array<Any>, active: Boolean = false) {
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = resources.getColor(if (active) R.color.colorRed else R.color.busColor)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true
        for (i in rects)
            if (i is Rect)
                canvas.drawRect(i, paint)
            else canvas.drawPath(i as Path, paint)
        invalidate()
    }
}
