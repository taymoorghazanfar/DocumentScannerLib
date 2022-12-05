package com.example.multitoolsdocumentscanner.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.multitoolsdocumentscanner.R
import com.example.multitoolsdocumentscanner.ui.interfaces.IDeleteDialogListener

class DeleteDocDialog(listener: IDeleteDialogListener) : DialogFragment() {

    private lateinit var listener: IDeleteDialogListener

    init {

        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return inflater.inflate(R.layout.dialog_delete_doc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewCancel = view.findViewById<TextView>(R.id.text_view_cancel)
        val textViewDelete = view.findViewById<TextView>(R.id.text_view_delete)

        textViewCancel.setOnClickListener {

            listener.onDeleteDialogCancelled()
        }

        textViewDelete.setOnClickListener {

          listener.onDeleteDialogSubmit()
        }
    }
}