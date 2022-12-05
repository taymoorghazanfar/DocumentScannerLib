package com.example.multitoolsdocumentscanner.utils

import android.graphics.Bitmap

class ImageUtils {

    companion object{

        fun scaleToFitHeight(b: Bitmap, height: Int): Bitmap? {
            val factor = height / b.height.toFloat()
            return Bitmap.createScaledBitmap(b, (b.width * factor).toInt(), height, true)
        }
    }
}