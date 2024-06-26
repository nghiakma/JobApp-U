package com.example.jobapp_u.home.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobapp_u.model.Job
import com.example.jobapp_u.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.example.jobapp_u.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.example.jobapp_u.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query.Direction.DESCENDING
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mRealtimeDb: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    private val currentUserId = mAuth.currentUser?.uid!!

    //đăng ký lắng nghe firestore để theo dõi sự thay đổi của các documents trong firestore
    //ví dụ sau khi viewmodel bị huỷ gọi phương thức remove() trên jobListener để hủy bỏ lắng nghe.
    //giải phóng tài nguyên
    private var jobListener: ListenerRegistration? = null

    //số liệu companies,jobs
    private val _metrics: MutableLiveData<Resource<Pair<Int, Int>>> = MutableLiveData()
    val metrics: LiveData<Resource<Pair<Int, Int>>> = _metrics



    private val _jobs: MutableLiveData<Resource<List<Job>>> = MutableLiveData()
    val jobs: LiveData<Resource<List<Job>>> = _jobs


    fun fetchMetrics() {
        viewModelScope.launch(IO) {
            try {
                _metrics.postValue(Resource.loading())

                // Get the companies count and jobs applied count using coroutines
                val companiesCountDeffered = async { getCompaniesCount() }
                val jobsAppliedCountDeffered = async { getJobsAppliedCount() }
                val companiesCount = companiesCountDeffered.await()
                val jobsAppliedCount = jobsAppliedCountDeffered.await()
                val metric = Pair(companiesCount, jobsAppliedCount)

                _metrics.postValue(Resource.success(metric))
            } catch (error: Exception) {
                Log.d(TAG, "fetchMetrics Error: ${error.message}")
                _metrics.postValue(Resource.error(error.message!!))
            }
        }
    }

    //Deferred biểu diễn giá trị bất đồng bộ or một nhiệm vụ bất đồng bộ
    private suspend fun getCompaniesCount(): Int {
        val companiesCountDeffered = CompletableDeferred<Int>()
        val companiesRef = mFirestore.collection(COLLECTION_PATH_COMPANY)
        val companiesCountListener = companiesRef
            .addSnapshotListener { value, error ->
                if (error != null) {
                    companiesCountDeffered.completeExceptionally(error)
                    return@addSnapshotListener
                }
                val companies = value?.documents!!
                val count = companies.count()
                companiesCountDeffered.complete(count)
            }

        //hàm xử ly bất đồng bộ kể cả có hoàn thành hay không
        companiesCountDeffered.invokeOnCompletion { error ->
            Log.d(TAG, "fetchMetrics Error: ${error?.message}")
            companiesCountListener.remove()
        }
        return companiesCountDeffered.await()
    }

    private suspend fun getJobsAppliedCount(): Int {
        val jobAppliedPath = "$COLLECTION_PATH_STUDENT/$currentUserId/$COLLECTION_PATH_COMPANY"
        val jobAppliedRef = mRealtimeDb.child(jobAppliedPath)
        val jobAppliedCountDeffered = CompletableDeferred<Int>()
        val jobAppliedCountListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appliedCount = snapshot.childrenCount.toInt()
                jobAppliedCountDeffered.complete(appliedCount)
            }

            override fun onCancelled(error: DatabaseError) {
                jobAppliedCountDeffered.completeExceptionally(error.toException())
            }
        }
        jobAppliedRef.addValueEventListener(jobAppliedCountListener)
        jobAppliedCountDeffered.invokeOnCompletion { error ->
            Log.d(TAG, "fetchMetrics Error: ${error?.message}")
            jobAppliedRef.removeEventListener(jobAppliedCountListener)
        }
        return jobAppliedCountDeffered.await()
    }

    fun fetchJobs() {
        viewModelScope.launch(IO) {
            _jobs.postValue(Resource.loading())
            val jobsRef = mFirestore.collection(COLLECTION_PATH_COMPANY).orderBy("uid", DESCENDING)
            jobListener = jobsRef.addSnapshotListener { value, error ->
                if (error != null) {
                    _jobs.postValue(Resource.error(error.message!!))
                    return@addSnapshotListener
                }
                val jobs = value!!.documents
                val jobList = jobs.map { documentSnapshot ->
                    documentSnapshot.toObject(Job::class.java)!!
                }
                _jobs.postValue(Resource.success(jobList))
            }
        }
    }

    override fun onCleared() {
        jobListener?.remove()
        super.onCleared()
    }
}