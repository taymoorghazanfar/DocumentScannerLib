package com.example.multitoolsdocumentscanner.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.example.multitoolsdocumentscanner.R
import java.io.File

class DocsRepository(context: Context) {

    private var root: DocumentFile? = null
    private var docsLiveData: MutableLiveData<List<DocumentFile>> = MutableLiveData()
    private var docRenameResponseLiveData: MutableLiveData<String> = MutableLiveData()
    private var docDeleteResponseLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var appName: String

    init {

        root = DocumentFile.fromTreeUri(
            context,
            context.contentResolver.persistedUriPermissions[0].uri
        )

        appName = context.resources.getString(R.string.app_name)
    }

    fun getDocsLiveData(): MutableLiveData<List<DocumentFile>> {

        return docsLiveData
    }

    fun getDocRenameResponseLiveData(): MutableLiveData<String> {

        return this.docRenameResponseLiveData
    }

    fun getDocDeleteResponseLiveData(): MutableLiveData<Boolean> {

        return this.docDeleteResponseLiveData
    }

    fun fetchDocs() {

        try {

            val rootPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .path + "/" + appName

            val rootDir = File(rootPath)

            if (rootDir.exists() && root != null) {

                val files: Array<DocumentFile> = root!!.listFiles()

                if (files != null) {

                    docsLiveData.value = files.toList()

                } else {

                    docsLiveData.value = null
                }

            } else {

                docsLiveData.value = null
            }

        } catch (e: Exception) {

            e.printStackTrace()
            Log.d("file_r", "error: " + e.message)

            docsLiveData.value = null
        }
    }

    fun renameDoc(doc: DocumentFile, name: String) {

        try {

            val rootPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .path + "/" + appName

            val rootDir = File(rootPath)

            if (rootDir.exists() && root != null && doc.exists()) {

                docRenameResponseLiveData.value =
                    if (doc.renameTo("$name.jpg")) name
                    else null

            } else {

                docRenameResponseLiveData.value = null
            }

        } catch (e: Exception) {

            e.printStackTrace()
            docRenameResponseLiveData.value = null
        }
    }

    fun deleteDoc(doc: DocumentFile) {

        try {

            val rootPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .path + "/" + appName

            val rootDir = File(rootPath)

            if (rootDir.exists() && root != null && doc.exists()) {

                docDeleteResponseLiveData.value = doc.delete()

            } else {

                docDeleteResponseLiveData.value = false
            }

        } catch (e: Exception) {

            e.printStackTrace()
            docDeleteResponseLiveData.value = false
        }
    }
}