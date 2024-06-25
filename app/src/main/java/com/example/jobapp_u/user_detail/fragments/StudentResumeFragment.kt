package com.example.jobapp_u.user_detail.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.jobapp_u.R
import com.example.jobapp_u.databinding.FragmentStudentResumeBinding
import com.example.jobapp_u.user_detail.viewmodel.UserDetailViewModel
import com.example.jobapp_u.util.LoadingDialog


private const val TAG = "StudentResumeFragment"
class StudentResumeFragment : Fragment() {
    private var _binding: FragmentStudentResumeBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<StudentResumeFragmentArgs>()
    private val userDetailViewModel by  viewModels<UserDetailViewModel>()
    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleCapturedPdf(result)
        }
    private val loadingDialog : LoadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStudentResumeBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupObserver() {
        TODO("Not yet implemented")
    }

    private fun setupUI() {
        TODO("Not yet implemented")
    }

    private fun handleCapturedPdf(result: ActivityResult) {
        val resultCode = result.resultCode
        val data = result.data
        when (resultCode) {
            Activity.RESULT_OK -> {
                userDetailViewModel.setPdfUri(pdfUri = data?.data!!)
                getFileInfo(userDetailViewModel.getPdfUri()!!)
            }
            Activity.RESULT_CANCELED -> {
                Log.d(TAG, "TASK CANCELLED")
            }
        }
    }

    private fun hidePdfUploadView() {
        binding.layoutUploadPdf.root.visibility = View.GONE
        binding.layoutUploadedPdf.root.visibility = View.VISIBLE
    }

    private fun hidePdfUploadedView() {
        binding.layoutUploadedPdf.root.visibility = View.GONE
        binding.layoutUploadPdf.root.visibility = View.VISIBLE
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}