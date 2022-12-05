package com.example.multitoolsdocumentscanner.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.multitoolsdocumentscanner.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    fun expendDialog(activity: FragmentActivity?, logTag: String, performOnError: () -> Unit) {

        try {

            val bottomSheet =
                dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet) as View
            val behavior = BottomSheetBehavior.from(bottomSheet)
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager!!.defaultDisplay!!.getMetrics(displayMetrics)
            behavior.peekHeight = displayMetrics.heightPixels

        } catch (e: NullPointerException) {

            Log.d(logTag, e.message ?: "NPE in onResume")
            performOnError()
        }
    }
}