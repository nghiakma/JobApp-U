package com.example.jobapp_u.user_detail.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.example.jobapp_u.model.Student
import com.example.jobapp_u.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.example.jobapp_u.util.Constants.Companion.PROFILE_IMAGE_PATH
import com.example.jobapp_u.util.Constants.Companion.RESUME_PATH
import com.example.jobapp_u.util.Resource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private const val TAG = "UserDetailViewModel"

class UserDetailViewModel : ViewModel() {
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

    fun uploadStudentData(
        pdfUri: Uri,
        imageUri: Uri,
        student: Student
    ) {
        viewModelScope.launch(IO) {
            try {
                val studentId = student.uid.toString()
                _uploadStudent.postValue(Resource.loading())
                val metaDataBuilder = StorageMetadata.Builder()
                val metaData = metaDataBuilder
                    .setCustomMetadata("fileName", resumeFileName)
                    .setCustomMetadata("fileMetaData", fileMetaData)
                    .build()

                val resumePath = "$RESUME_PATH/$studentId"
                val resumeUrl = uploadData(resumePath, pdfUri, metaData)
                student.academic?.resumeUrl = resumeUrl

                val imagePath = "$PROFILE_IMAGE_PATH/$studentId"
                val imageUrl = uploadData(imagePath, imageUri, null)
                student.details?.imageUrl = imageUrl

                val userProfileBuilder = UserProfileChangeRequest.Builder()
                val userProfile = userProfileBuilder
                    .setDisplayName(student.details?.username)
                    .setPhotoUri(Uri.parse(imageUrl))
                    .build()

                val currentUser = mAuth.currentUser!!
                currentUser.updateProfile(userProfile).await()

                val studentRef = mFirestore.collection(COLLECTION_PATH_STUDENT).document(studentId)
                studentRef.set(student).await()

                _uploadStudent.postValue(Resource.success("Data upload success."))
            } catch (error : Exception) {
                val errorMessage = error.message!!
                _uploadStudent.postValue(Resource.error(errorMessage))
            }
        }
    }

    private suspend fun uploadData(
        path: String,
        fileUri: Uri,
        metadata: StorageMetadata?
    ): String {
        val fileRef = mStorage.child(path)
        fileRef.putFile(fileUri).await()
        if (metadata != null) {
            fileRef.updateMetadata(metadata).await()
        }
        return fileRef.downloadUrl.await().toString()
    }

}