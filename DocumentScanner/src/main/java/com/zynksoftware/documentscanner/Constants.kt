package com.zynksoftware.documentscanner

import android.os.Environment

class Constants {

    companion object {

        private const val APP_NAME = "Multi Tools Document Scanner"

        var ROOT_DIR = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .path + "/" + APP_NAME
    }
}