package com.example.multitoolsdocumentscanner.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.multitoolsdocumentscanner.R
import com.example.multitoolsdocumentscanner.ui.interfaces.IScanOptionsListener

class ScanOptionsBottomSheet : RoundedBottomSheetDialogFragment() {

    private lateinit var buttonCamera: LinearLayout
    private lateinit var buttonGallery: LinearLayout
    private lateinit var listener: IScanOptionsListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        val view = inflater.inflate(R.layout.bottom_sheet_scan_options_dm, container, false)

        buttonCamera = view.findViewById(R.id.button_camera)
        buttonGallery = view.findViewById(R.id.button_gallery)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        buttonCamera.setOnClickListener {

            listener.onCameraSelected()
        }

        buttonGallery.setOnClickListener {

            listener.onGallerySelected()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        listener.onDialogDismissed()
    }

    fun setListener(listener: IScanOptionsListener) {

        this.listener = listener
    }
}