package com.example.multitoolsdocumentscanner.ui.activities

import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import com.example.multitoolsdocumentscanner.R
import com.zynksoftware.documentscanner.ScanActivity
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.model.ScannerResults

class AppScanActivity : ScanActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_scan)

        val uri: Uri? = intent.getParcelableExtra("uri") as Uri?

        addFragmentContentLayout(uri)
    }

    override fun onError(error: DocumentScannerErrorModel) {

        setResult(RESULT_OK, intent.putExtra("code", -1))
        finish()
    }

    override fun onSuccess(scannerResults: ScannerResults) {

        if (scannerResults.transformedImageFile == null) {

            setResult(RESULT_OK, intent.putExtra("code", -1))
            finish()

        } else {

            // scan to update gallery
            MediaScannerConnection.scanFile(
                this@AppScanActivity,
                arrayOf<String>(scannerResults.transformedImageFile!!.path),
                null
            ) { _, _ -> }

            finish()
        }
    }

    override fun onClose() {
        finish()
    }
}