package com.example.jobapp_u.home.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import coil.load
import com.google.android.material.chip.Chip
import com.example.jobapp_u.R
import com.example.jobapp_u.databinding.ActivityJobViewBinding
import com.example.jobapp_u.home.viewmodel.StudentJobViewModel
import com.example.jobapp_u.util.LoadingDialog
import com.example.jobapp_u.util.Status.SUCCESS
import com.example.jobapp_u.util.Status.LOADING
import com.example.jobapp_u.util.Status.ERROR
import com.example.jobapp_u.util.createSalaryText
import com.example.jobapp_u.util.showToast

private const val TAG = "JobViewActivity"

class JobViewActivity : AppCompatActivity() {
    private var _binding: ActivityJobViewBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<JobViewActivityArgs>()
    private val studentJobViewModel by viewModels<StudentJobViewModel>()
    private val loadingDialog by lazy { LoadingDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityJobViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObserver()

    }

    private fun setupUI() {
        studentJobViewModel.fetchJobMetrics(args.job.uid)
        val job = args.job
        binding.apply {
            ivPopOut.setOnClickListener {
                finish()
            }
            ivCompanyLogo.load(job.imageUrl)
            tvRole.text = job.role
            tvCompanyLocation.text =
                getString(R.string.field_company_and_location, job.name, job.city)
            tvJobDescription.text = job.description
            tvResponsibility.text = job.responsibility
            tvSalary.text = createSalaryText(job.salary, this@JobViewActivity)
            job.skillSet.forEach { job ->
                createSkillSetChip(job)
            }
        }
    }

    private fun setupObserver() {
        studentJobViewModel.jobMetric.observe(this){ metricStatus ->
            when(metricStatus.status){
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    val (studentCount, hasJobApplied) = metricStatus.data!!
                    binding.tvStudentCount.text = studentCount.toString()
                    if (hasJobApplied){
                        binding.btnApply.text = getString(R.string.field_job_applied)
                        binding.btnApply.isEnabled = false
                    } else {
                        binding.btnApply.isEnabled = true
                        binding.btnApply.setOnClickListener {
                            studentJobViewModel.applyJob(args.job.uid)
                        }
                    }
                }
                ERROR -> {
                    val message = metricStatus.message!!
                    showToast(this, message)
                    loadingDialog.dismiss()
                }
            }
        }

        studentJobViewModel.applicationStatus.observe(this){appliedState ->
            when(appliedState.status){
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    val message = appliedState.data!!
                    showToast(this, message)
                    finish()
                    loadingDialog.dismiss()
                }
                ERROR -> {
                    loadingDialog.dismiss()
                    val message = appliedState.message!!
                    showToast(this, message)
                }
            }
        }
    }

    private fun createSkillSetChip(job: String) {
        val chip = Chip(this@JobViewActivity)
        chip.text = job
        chip.chipBackgroundColor =
            ContextCompat.getColorStateList(this@JobViewActivity, R.color.chip_background_color)
        chip.setTextColor(this@JobViewActivity.getColor(R.color.chip_text_color))
        chip.chipCornerRadius = 8f
        chip.textSize = 14.0f
        binding.requiredSkillSetChipGroup.addView(chip)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}