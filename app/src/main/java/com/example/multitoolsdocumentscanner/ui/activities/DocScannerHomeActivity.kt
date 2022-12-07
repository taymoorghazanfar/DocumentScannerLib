package com.example.multitoolsdocumentscanner.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multitoolsdocumentscanner.Globals
import com.example.multitoolsdocumentscanner.R
import com.example.multitoolsdocumentscanner.databinding.ActivityDocScannerHomeDmBinding
import com.example.multitoolsdocumentscanner.ui.adapters.IDocClickListener
import com.example.multitoolsdocumentscanner.ui.adapters.RecyclerAdapterDocs
import com.example.multitoolsdocumentscanner.ui.decorators.EqualSpacingItemDecoration
import com.example.multitoolsdocumentscanner.ui.dialogs.DeleteDocDialog
import com.example.multitoolsdocumentscanner.ui.dialogs.RenameDocDialog
import com.example.multitoolsdocumentscanner.ui.dialogs.ScanOptionsBottomSheet
import com.example.multitoolsdocumentscanner.ui.interfaces.IDeleteDialogListener
import com.example.multitoolsdocumentscanner.ui.interfaces.IRenameDialogListener
import com.example.multitoolsdocumentscanner.ui.interfaces.IScanOptionsListener
import com.example.multitoolsdocumentscanner.viewmodel.DocsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException

class DocScannerHomeActivity : AppCompatActivity(), IDocClickListener, IScanOptionsListener,
    IRenameDialogListener, IDeleteDialogListener {

    private lateinit var binding: ActivityDocScannerHomeDmBinding
    private lateinit var viewModel: DocsViewModel
    private lateinit var resultLauncherPickDoc: ActivityResultLauncher<Intent>

    @SuppressLint("StaticFieldLeak")
    private lateinit var adapter: RecyclerAdapterDocs
    private var bottomSheet: ScanOptionsBottomSheet? = null
    private var renameDialog: RenameDocDialog? = null
    private var deleteDialog: DeleteDocDialog? = null

    private var fetchData: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocScannerHomeDmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        initLaunchers()

        checkPersistentPermissions()
    }

    private fun checkPersistentPermissions() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

            if (contentResolver != null &&
                contentResolver.persistedUriPermissions.isNotEmpty() &&
                contentResolver.persistedUriPermissions[0] != null &&
                contentResolver.persistedUriPermissions[0].uri != null
            ) {
                val persistentUri = DocumentFile.fromTreeUri(
                    this@DocScannerHomeActivity,
                    contentResolver.persistedUriPermissions[0].uri
                )

                if (persistentUri != null) {

                    fetchData = true
                    initViews()
                    initViewModel()

                } else {

                    showDialog(
                        "Permission required",
                        "To fetch all the scanned documents, access to the required folder is necessary",
                        true
                    )
                }
            } else {

                showDialog(
                    "Permission required",
                    "To fetch all the scanned documents, access to the required folder is necessary",
                    true
                )
            }

        } else {

            fetchData = true
            initViews()
            initViewModel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun launchPersistentPermissionsDialog() {

        // make dir first if not there
        val appName = resources.getString(R.string.app_name)
        val rootDir = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .path + "/" + appName

        val root = File(rootDir)

        if (!root.exists()) {

            root.mkdirs()
        }

        val sm = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent: Intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
        val targetDir = "Pictures%2F${appName}";

        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString()

        scheme = scheme.replace("/root/", "/document/")
        scheme += "%3A$targetDir"
        uri = Uri.parse(scheme)

        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        startActivityForResult(
            intent,
            REQUEST_CODE_URI_PERMISSION
        )
    }

    override fun onResume() {
        super.onResume()

        if (fetchData) {

            initViewModel()
        }
    }

    private fun initLaunchers() {

        resultLauncherPickDoc = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            parsePicDocResult(result)
        }
    }

    private fun parsePicDocResult(result: ActivityResult?) {

        if (result != null && result.resultCode != RESULT_CANCELED) {

            if (result.data != null) {

                val uri = result.data!!.data

                if (uri != null) {

                    try {

                        // todo: parse uri

                    } catch (e: Exception) {

                        e.printStackTrace()

                        // todo: error
                    }
                }
            }
        }
    }

    private fun initViews() {

        CoroutineScope(Dispatchers.Main).launch {

            binding.contentEmpty.root.visibility = View.GONE
            binding.contentRecents.root.visibility = View.GONE

            binding.contentEmpty.buttonScan.setOnClickListener {

                checkReadWritePermissions()
            }

            binding.contentRecents.buttonScan.setOnClickListener {

                checkReadWritePermissions()
            }

            binding.contentRecents.recyclerView.layoutManager =
                LinearLayoutManager(this@DocScannerHomeActivity)
            val spacingInPixels =
                resources.getDimensionPixelSize(R.dimen.recycler_view_spacing)
            binding.contentRecents.recyclerView.addItemDecoration(
                EqualSpacingItemDecoration(spacingInPixels, EqualSpacingItemDecoration.VERTICAL)
            )
            adapter = RecyclerAdapterDocs(
                this@DocScannerHomeActivity,
                this@DocScannerHomeActivity
            )
            binding.contentRecents.recyclerView.adapter = adapter
        }
    }

    private fun setupHeader() {

        CoroutineScope(Dispatchers.Main).launch {

            binding.header.textViewTitle.text = "Document Scanner"
            binding.header.buttonBack.setOnClickListener {

                finish()
            }
        }
    }

    private fun initViewModel() {

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)
        )[DocsViewModel::class.java]

        viewModel.init(this@DocScannerHomeActivity)

        // getting list of all recent files
        viewModel.getDocsLiveData().observe(this) { docs ->

            if (docs.isNullOrEmpty()) {

                updateViews(true)

            } else {

                updateViews(false, docs)
            }
        }

        viewModel
            .getDocRenameResponseLiveData()
            .observe(this@DocScannerHomeActivity) { newName ->

                if (newName == null) {

                    Snackbar
                        .make(
                            binding.root,
                            "Failed to rename the file",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()

                } else {

                    viewModel.fetchDocs()

                    Snackbar
                        .make(
                            binding.root,
                            "File renamed successfully",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()
                }
            }

        viewModel
            .getDocDeleteResponseLiveData()
            .observe(this@DocScannerHomeActivity) { isDeleted ->

                if (!isDeleted) {

                    Snackbar
                        .make(
                            binding.root,
                            "Failed to delete the file",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()

                } else {

                    viewModel.fetchDocs()

                    Snackbar
                        .make(
                            binding.root,
                            "File is deleted",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()
                }
            }

        viewModel.fetchDocs()
    }

    private fun updateViews(isEmpty: Boolean, docs: List<DocumentFile>? = null) {

        // if there are no recent files
        if (isEmpty) {

            CoroutineScope(Dispatchers.Main).launch {

                binding.contentEmpty.root.visibility = View.VISIBLE
                binding.contentRecents.root.visibility = View.GONE
            }

        } else {

            CoroutineScope(Dispatchers.Main).launch {

                binding.contentEmpty.root.visibility = View.GONE
                binding.contentRecents.root.visibility = View.VISIBLE

                adapter.updateData(docs!!)
            }
        }
    }

    private fun checkReadWritePermissions() {

        if (ContextCompat
                .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
            && ContextCompat
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        ) {

            openBottomSheet()

        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_STORAGE_PERMISSIONS
            )
        }
    }

    private fun openBottomSheet() {

        bottomSheet = ScanOptionsBottomSheet()
        bottomSheet!!.setListener(this)
        bottomSheet!!.show(supportFragmentManager, bottomSheet!!.tag)
    }

    override fun onCameraSelected() {

        bottomSheet?.dismiss()

        val i = Intent(this@DocScannerHomeActivity, AppScanActivity::class.java)
        i.putExtra("app_name", resources.getString(R.string.app_name))
        startActivityForResult(
            i, REQUEST_CODE_SCAN
        )
    }

    override fun onGallerySelected() {

        bottomSheet?.dismiss()

        val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onDialogDismissed() {

        bottomSheet?.dismiss()
    }

    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_URI_PERMISSION) {

            // persistent permission result
            if (resultCode == RESULT_OK) {

                if (data != null) {

                    val uri = data.data

                    if (uri != null) {

                        if (uri.toString()
                                .endsWith(resources.getString(R.string.app_name_encoded))
                        ) {

                            val takeFlags = (data.flags
                                    and Intent.FLAG_GRANT_READ_URI_PERMISSION)

                            contentResolver.takePersistableUriPermission(uri, takeFlags)

                            fetchData = true
                            initViews()
                            initViewModel()

                        } else {

                            showDialog(
                                "Incorrect folder selected",
                                "Please select the folder named '" + resources.getString(R.string.app_name) + "'",
                                true
                            )
                        }

                    } else {

                        showDialog(
                            "Error occurred",
                            "Error occurred while selecting the folder. Try again later",
                            false
                        )
                    }
                }
            } else {

                showDialog(
                    "Permissions denied",
                    "Folder access is required to use this feature",
                    false
                )
            }
        } else if (requestCode == REQUEST_CODE_PICK_IMAGE) {

            if (resultCode == RESULT_OK) {

                try {
                    val imageUri = data?.data

                    if (imageUri != null) {

                        val i = Intent(this@DocScannerHomeActivity, AppScanActivity::class.java)
                        i.putExtra("app_name", resources.getString(R.string.app_name))
                        i.putExtra("uri", imageUri)
                        startActivityForResult(i, REQUEST_CODE_SCAN)
                    }

                } catch (e: FileNotFoundException) {

                    e.printStackTrace()
                    Snackbar.make(
                        binding.root,
                        "Error occurred while picking the image",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        } else if (requestCode == REQUEST_CODE_SCAN) {

            if (resultCode == RESULT_OK) {

                if (data != null) {

                    val code = data.getIntExtra("code", 0)

                    if (code == -1) {

                        Snackbar.make(
                            binding.root,
                            "Error occurred while scanning the document",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSIONS) {

            if (ContextCompat
                    .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat
                    .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
            ) {

                openBottomSheet()

            } else {

                Snackbar.make(
                    binding.root,
                    "Storage permission is required to use this feature",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDocClick(doc: DocumentFile) {

        Globals.openedDoc = doc
        val i = Intent(this@DocScannerHomeActivity, DocViewerActivity::class.java)
        startActivity(i)
    }

    override fun onDocRenameClick(doc: DocumentFile) {

        Globals.openedDoc = doc

        val currentName = doc.name!!.substring(0, doc.name!!.length - 4)

        renameDialog = RenameDocDialog(currentName, this)
        renameDialog!!.isCancelable = false
        renameDialog!!.show(supportFragmentManager, "rename_dialog")
    }

    override fun onDocDeleteClick(doc: DocumentFile) {

        Globals.openedDoc = doc

        deleteDialog = DeleteDocDialog(this)
        deleteDialog!!.isCancelable = false
        deleteDialog!!.show(supportFragmentManager, "delete_dialog")
    }

    private fun showDialog(title: String, message: String, forPersistent: Boolean) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)

        builder.setPositiveButton(if (forPersistent) "Grant access" else "OK") { d, _ ->

            if (forPersistent) {

                d.dismiss()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    launchPersistentPermissionsDialog()
                }
            } else {

                finish()
            }
        }

        if (forPersistent) {

            builder.setNegativeButton("Deny") { _, _ ->

                finish()
            }
        }

        builder.show()
    }

    companion object {

        private const val REQUEST_CODE_STORAGE_PERMISSIONS = 1
        private const val REQUEST_CODE_URI_PERMISSION = 2
        private const val REQUEST_CODE_SCAN = 3
        private const val REQUEST_CODE_PICK_IMAGE = 4
    }

    override fun onRenameDialogCancelled() {
        renameDialog?.dismiss()
    }

    override fun onRenameDialogSubmit(name: String) {

        renameDialog?.dismiss()
        viewModel.renameDoc(Globals.openedDoc!!, name)
    }

    override fun onDeleteDialogCancelled() {

        deleteDialog?.dismiss()
    }

    override fun onDeleteDialogSubmit() {

        deleteDialog?.dismiss()
        viewModel.deleteDoc(Globals.openedDoc!!)
    }
}