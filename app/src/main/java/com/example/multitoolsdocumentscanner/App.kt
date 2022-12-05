package com.example.multitoolsdocumentscanner

import android.app.Application
import android.graphics.Bitmap
import com.zynksoftware.documentscanner.ui.DocumentScanner

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val configuration = DocumentScanner.Configuration()

        configuration.imageQuality = 100
        configuration.imageSize = 1000000 // 1 MB
        configuration.imageType = Bitmap.CompressFormat.JPEG
        DocumentScanner.init(this, configuration) // or simply DocumentScanner.init(this)
    }
}