package com.example.multitoolsdocumentscanner.ui.activities

import android.R.anim
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.multitoolsdocumentscanner.Globals
import com.example.multitoolsdocumentscanner.databinding.ActivityDocViewerBinding
import com.example.multitoolsdocumentscanner.ui.dialogs.RenameDocDialog
import com.example.multitoolsdocumentscanner.ui.interfaces.IRenameDialogListener
import com.example.multitoolsdocumentscanner.utils.DeviceDimensionHelper
import com.example.multitoolsdocumentscanner.utils.ImageUtils
import com.example.multitoolsdocumentscanner.viewmodel.DocsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileDescriptor

class DocViewerActivity : AppCompatActivity(), IRenameDialogListener {

    private lateinit var binding: ActivityDocViewerBinding
    private lateinit var viewModel: DocsViewModel
    private var doc: DocumentFile? = null
    private var renameDialog: RenameDocDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()

        initHeader()
    }

    override fun onResume() {
        super.onResume()

        try {

            // check if file is deleted/moved/renamed externally
            if (doc != null) {

                if (!doc!!.exists()) {

                    setResult(RESULT_OK)
                    finish()
                }
            }
        } catch (e: Exception) {

            e.printStackTrace()

            showError()
        }
    }

    private fun initHeader() {

        binding.header.buttonBack.setOnClickListener {

            finish()
        }
    }

    private fun getData() {

        doc = Globals.openedDoc

        if (doc == null) {

            showError()

        } else {

            loadImage()

            initViews()

            initViewModel()
        }
    }

    private fun initViewModel() {

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)
        )[DocsViewModel::class.java]

        viewModel.init(this@DocViewerActivity)

        viewModel
            .getDocRenameResponseLiveData()
            .observe(this@DocViewerActivity) { newName ->

                if (newName == null) {

                    Snackbar
                        .make(
                            binding.root,
                            "Failed to rename the file",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()

                } else {

                    animateView(binding.header.textViewTitle, newName)

                    Snackbar
                        .make(
                            binding.root,
                            "File renamed successfully",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()
                }
            }
    }

    private fun loadImage() {

        try {

            CoroutineScope(Dispatchers.IO).launch {

                val parcelFileDescriptor = contentResolver.openFileDescriptor(doc!!.uri, "r")
                val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
                val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)

                parcelFileDescriptor.close()

                if (bitmap != null) {

                    val height: Int = DeviceDimensionHelper.getDisplayHeight(this@DocViewerActivity)

                    val scaledBitmap: Bitmap? = ImageUtils.scaleToFitHeight(bitmap, height)

                    if (scaledBitmap != null) {

                        CoroutineScope(Dispatchers.Main).launch {

                            Glide
                                .with(this@DocViewerActivity)
                                .load(scaledBitmap)
                                .into(binding.content.image)
                        }
                    } else {

                        showError()
                    }
                } else {

                    showError()
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()

            showError()
        }
    }

    private fun initViews() {

        animateView(binding.header.textViewTitle, doc!!.name!!)

        binding.header.buttonEdit.setOnClickListener {

            openRenameDocDialog()
        }
    }

    private fun openRenameDocDialog() {

        val currentName = doc!!.name!!.substring(0, doc!!.name!!.length - 4)

        renameDialog = RenameDocDialog(currentName, this)
        renameDialog!!.isCancelable = false
        renameDialog!!.show(supportFragmentManager, "rename_dialog")
    }

    private fun animateView(view: View, data: String) {

        val animOut: Animation =
            AnimationUtils.loadAnimation(
                this@DocViewerActivity,
                anim.fade_out
            )
        val animIn: Animation =
            AnimationUtils.loadAnimation(
                this@DocViewerActivity,
                anim.fade_in
            )

        animOut.setAnimationListener(object :
            Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {

                (view as TextView).text = data
                view.startAnimation(animIn)
            }
        })

        view.startAnimation(animOut)
    }

    override fun onRenameDialogCancelled() {

        renameDialog?.dismiss()
    }

    override fun onRenameDialogSubmit(name: String) {

        renameDialog?.dismiss()

        viewModel.renameDoc(doc!!, name)
    }

    private fun showError() {

        binding.header.root.visibility = View.GONE
        binding.content.root.visibility = View.GONE
        binding.bannerAd.root.visibility = View.GONE

        binding.layoutError.root.visibility = View.VISIBLE

        Snackbar.make(
            binding.root,
            "Error occurred while reading the file",
            Snackbar.LENGTH_SHORT
        ).show()
    }
}