package com.example.multitoolsdocumentscanner.viewmodel

import android.app.Application
import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.multitoolsdocumentscanner.repository.DocsRepository

class DocsViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: DocsRepository
    private lateinit var docsLiveData: MutableLiveData<List<DocumentFile>>
    private lateinit var docRenameResponseLiveData: MutableLiveData<String>
    private lateinit var docDeleteResponseLiveData: MutableLiveData<Boolean>

    fun init(context: Context) {

        repository = DocsRepository(context)
        docsLiveData = repository.getDocsLiveData()
        docRenameResponseLiveData = repository.getDocRenameResponseLiveData()
        docDeleteResponseLiveData = repository.getDocDeleteResponseLiveData()
    }

    fun getDocsLiveData(): MutableLiveData<List<DocumentFile>> {

        return this.docsLiveData
    }

    fun getDocRenameResponseLiveData(): MutableLiveData<String> {

        return this.docRenameResponseLiveData
    }

    fun getDocDeleteResponseLiveData(): MutableLiveData<Boolean> {

        return this.docDeleteResponseLiveData
    }

    fun fetchDocs() {

        repository.fetchDocs()
    }

    fun renameDoc(doc: DocumentFile, name: String) {

        repository.renameDoc(doc, name)
    }

    fun deleteDoc(doc: DocumentFile) {

        repository.deleteDoc(doc)
    }
}