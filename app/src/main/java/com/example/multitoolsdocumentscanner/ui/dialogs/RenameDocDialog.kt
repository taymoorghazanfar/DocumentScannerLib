package com.example.multitoolsdocumentscanner.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.multitoolsdocumentscanner.R
import com.example.multitoolsdocumentscanner.ui.interfaces.IRenameDialogListener


class RenameDocDialog(currentName: String, listener: IRenameDialogListener) : DialogFragment() {

    private lateinit var currentName: String
    private lateinit var listener: IRenameDialogListener

    init {

        this.currentName = currentName
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

        return inflater.inflate(R.layout.dialog_rename_doc_dm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextName = view.findViewById<EditText>(R.id.edit_text_name)
        val textViewCancel = view.findViewById<TextView>(R.id.text_view_cancel)
        val textViewRename = view.findViewById<TextView>(R.id.text_view_rename)

        editTextName.setText(currentName)

        textViewCancel.setOnClickListener {

            listener.onRenameDialogCancelled()
        }

        textViewRename.setOnClickListener {

            val name = editTextName.text.toString().trim()

            if (name.isEmpty()) {

                editTextName.error = "Field is required"

            } else {

                listener.onRenameDialogSubmit(name)
            }
        }
    }
}