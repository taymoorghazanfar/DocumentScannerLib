package com.example.multitoolsdocumentscanner.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics

import android.util.TypedValue


object DeviceDimensionHelper {

    // DeviceDimensionsHelper.getDisplayWidth(context) => (display width in pixels)
    fun getDisplayWidth(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels
    }

    // DeviceDimensionsHelper.getDisplayHeight(context) => (display height in pixels)
    fun getDisplayHeight(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return displayMetrics.heightPixels
    }

    // DeviceDimensionsHelper.convertDpToPixel(25f, context) => (25dp converted to pixels)
    fun convertDpToPixel(dp: Float, context: Context): Float {
        val r: Resources = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.displayMetrics)
    }

    // DeviceDimensionsHelper.convertPixelsToDp(25f, context) => (25px converted to dp)
    fun convertPixelsToDp(px: Float, context: Context): Float {
        val r: Resources = context.resources
        val metrics: DisplayMetrics = r.displayMetrics
        return px / (metrics.densityDpi / 160f)
    }
}