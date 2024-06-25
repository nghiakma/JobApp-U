package com.example.jobapp_u.user_detail.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobapp_u.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


private const val TAG = "UserDetailViewModel"
class UserDetailViewModel : ViewModel(){

    private var imageUri: Uri? = null
    private var pdfUri: Uri? = null
    var resumeFileName: String? = null
    var fileMetaData: String? = null
    private val mStorage: StorageReference by lazy { FirebaseStorage.getInstance().reference }
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mAuth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun setPdfUri(pdfUri: Uri?) {
        this.pdfUri = pdfUri
    }

    fun getPdfUri(): Uri? {
        return this.pdfUri
    }

    fun setImageUri(imageUri: Uri) {
        this.imageUri = imageUri
    }

    fun getImageUri(): Uri? {
        return this.imageUri
    }

    private val _uploadStudent: MutableLiveData<Resource<String>> = MutableLiveData()
    val uploadStudent: LiveData<Resource<String>> = _uploadStudent
}