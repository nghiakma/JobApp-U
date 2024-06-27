package com.example.jobapp_u.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.jobapp_u.R
import com.example.jobapp_u.databinding.JobDetailCardBinding
import com.example.jobapp_u.model.Job
import com.example.jobapp_u.util.createSalaryText

class JobListAdapter(
    private val onItemClick: (job: Job) -> Unit,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<JobListAdapter.JobListAdapterViewHolder>() {

    private var jobs: MutableList<Job> = mutableListOf()

    inner class JobListAdapterViewHolder(
        private val binding: JobDetailCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job) {
            binding.apply {
                ivCompanyLogo.load(job.imageUrl){
                    error(R.drawable.ic_apple_logo)
                    placeholder(R.drawable.ic_jobs)
                    build()
                }
                tvJobRole.text = job.role
                chipDesignation.text = job.designation
                val nameLocation = itemView.context.getString(R.string.field_company_and_location, job.name, job.city)
                tvCompanyNameLocation.text = nameLocation
                tvSalary.text = createSalaryText(job.salary, requireActivity = activity)
                chipWorkType.text = job.workType
                btnApply.setOnClickListener {
                    onItemClick(job)
                }
                cvJob.setOnClickListener {
                    onItemClick(job)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobListAdapterViewHolder {
        return JobListAdapterViewHolder(
            JobDetailCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: JobListAdapterViewHolder, position: Int) {
        holder.bind(jobs[position])
    }

    override fun getItemCount(): Int = jobs.size

    fun setJobListData(newJobList : List<Job>){
        jobs.clear()
        jobs.addAll(newJobList)
        notifyDataSetChanged()
    }
}