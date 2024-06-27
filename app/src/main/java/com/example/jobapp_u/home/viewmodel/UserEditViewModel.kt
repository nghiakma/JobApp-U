package com.example.jobapp_u.home.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobapp_u.model.Student
import com.example.jobapp_u.model.Tpo
import com.example.jobapp_u.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.example.jobapp_u.util.Constants.Companion.COLLECTION_PATH_TPO
import com.example.jobapp_u.util.Constants.Companion.RESUME_PATH
import com.example.jobapp_u.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private const val TAG = "UserEditViewModelTAG"
class UserEditViewModel : ViewModel(){

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val studentId: String by lazy { mAuth.currentUser?.uid.toString() }
    private val mStorage: StorageReference by lazy { FirebaseStorage.getInstance().reference }
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mRealtimeDb: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private var tpoListener : ListenerRegistration? = null

    private var imageUri: Uri? = null

    fun setImageUri(imageUri: Uri) {
        this.imageUri = imageUri
    }

    fun getImageUri(): Uri? {
        return this.imageUri
    }


    private val _student: MutableLiveData<Resource<Student>> = MutableLiveData()
    val student: LiveData<Resource<Student>> = _student

    private val _tpoList: MutableLiveData<List<Tpo>> = MutableLiveData(emptyList())
    val tpoList: LiveData<List<Tpo>> = _tpoList

    private val _updateState: MutableLiveData<Resource<String>> = MutableLiveData()
    val updateState: LiveData<Resource<String>> = _updateState

    private val _resumeState: MutableLiveData<Resource<Triple<String, String, Uri>>> = MutableLiveData()
    val resumeState: LiveData<Resource<Triple<String, String, Uri>>> = _resumeState

    private val _deleteState: MutableLiveData<Resource<String>> = MutableLiveData()
    val deleteState: LiveData<Resource<String>> = _deleteState


    fun fetchStudent() {
        viewModelScope.launch(IO) {
            try {
                _student.postValue(Resource.loading())
                val studentRef = mFirestore.collection(COLLECTION_PATH_STUDENT).document(studentId).get().await()
                val student = studentRef.toObject(Student::class.java)!!
                _student.postValue(Resource.success(student))
            } catch (error: Exception) {
                Log.d(TAG, "Error: ${error.message}")
                _student.postValue(Resource.error(error.message!!))
            }
        }
    }

    fun fetchResume() {
        viewModelScope.launch(IO) {
            try {
                _resumeState.postValue(Resource.loading())
                val resumeRef = mStorage.child(RESUME_PATH).child(studentId)
                val resumeUri = resumeRef.downloadUrl.await()
                val metaData = resumeRef.metadata.await()
                val fileName = metaData.getCustomMetadata("fileName") ?: ""
                val fileMetaData = metaData.getCustomMetadata("fileMetaData") ?: ""
                val resumeData = Triple(fileName, fileMetaData, resumeUri)
                _resumeState.postValue(Resource.success(resumeData))
            } catch (error: Exception) {
                val errorMessage = error.message!!
                _resumeState.postValue(Resource.error(errorMessage))
            }
        }
    }

    fun fetchTpo() {
        viewModelScope.launch(IO) {
            tpoListener = mFirestore.collection(COLLECTION_PATH_TPO)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    val documents = value?.documents!!
                    val tpoList = documents.map {
                        it.toObject(Tpo::class.java)!!
                    }
                    _tpoList.postValue(tpoList)
                }
        }
    }
}