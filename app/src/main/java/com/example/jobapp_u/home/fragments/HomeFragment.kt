package com.example.jobapp_u.home.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.jobapp_u.R
import com.example.jobapp_u.databinding.FragmentHomeBinding
import com.example.jobapp_u.home.activity.UserActivity
import com.example.jobapp_u.home.adapter.JobListAdapter
import com.example.jobapp_u.home.viewmodel.HomeViewModel
import com.example.jobapp_u.model.Job
import com.example.jobapp_u.util.Status
import com.example.jobapp_u.util.counterAnimation
import com.example.jobapp_u.util.getGreeting
import com.example.jobapp_u.util.showToast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var _jobListAdapter: JobListAdapter? = null
    private val jobListAdapter get() = _jobListAdapter!!

    private val homeViewModel by viewModels<HomeViewModel>()
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _jobListAdapter = JobListAdapter(::onItemClick, requireActivity())

        setupUI()
        setupObserver()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI() {
        binding.apply {
            homeViewModel.fetchMetrics()
            homeViewModel.fetchJobs()

            //run các coroutine theo cac vòng đời của fragment
            //lặp lại coroutine khi trạng thái của vòng đời thay đổi
            //chỉ chạy khi fragment là started
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val username = mAuth.currentUser?.displayName!!
                    tvWelcomeHeading.text = createGreetingText(username.replaceFirstChar { it.uppercase() })
                    ivProfileImage.load(mAuth.currentUser?.photoUrl)
                }
            }

            ivProfileImage.setOnClickListener {
                navigateToUserActivity()
            }

            rvRecentJobs.adapter = jobListAdapter
            rvRecentJobs.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObserver() {
        homeViewModel.metrics.observe(viewLifecycleOwner) { metrics ->
            when (metrics.status) {
                Status.LOADING -> Unit
                Status.SUCCESS -> {
                    val (companiesCount, jobsAppliedCount) = metrics.data!!
                    counterAnimation(0, companiesCount, binding.tvCompaniesCount)
                    counterAnimation(0, jobsAppliedCount, binding.tvJobAppliedCount)
                }
                Status.ERROR -> {
                    val errorMessage = metrics.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }

        homeViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            when (jobs.status) {
                Status.LOADING -> Unit
                Status.SUCCESS -> {
                    val jobList = jobs.data!!.take(3)
                    jobListAdapter.setJobListData(jobList)
                }
                Status.ERROR -> {
                    val errorMessage = jobs.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }
    }

    private fun navigateToUserActivity() {
        val userActivity = Intent(requireContext(), UserActivity::class.java)
        startActivity(userActivity)
    }

    private fun onItemClick(job: Job) {
        val direction = HomeFragmentDirections.actionHomeFragmentToJobViewActivity(job = job)
        findNavController().navigate(direction)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createGreetingText(username: String): SpannableString {
        val greeting = getGreeting()
        val greetingWithUsername = "$greeting\n$username"
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val greetingColor = ForegroundColorSpan(color)
        val greetingText = SpannableString(greetingWithUsername)
        val userNameStart = greetingWithUsername.indexOf(username)
        val userNameEnd = userNameStart + username.length
        greetingText.setSpan(greetingColor, userNameStart, userNameEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return greetingText
    }
    override fun onDestroyView() {
        _jobListAdapter = null
        _binding = null
        super.onDestroyView()
    }
}