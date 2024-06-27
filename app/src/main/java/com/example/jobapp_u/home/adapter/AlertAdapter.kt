package com.example.jobapp_u.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobapp_u.databinding.AlertCardLayoutBinding
import com.example.jobapp_u.model.BroadcastNotification
import com.example.jobapp_u.model.HostNotification
import com.example.jobapp_u.model.Notification
import com.example.jobapp_u.util.convertTimeStamp

class AlertAdapter : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    private val notifications: MutableList<Notification> = mutableListOf()

    inner class AlertViewHolder(
        private val binding: AlertCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            when (notification) {
                is BroadcastNotification -> {
                    binding.tvNotificationTitle.text = notification.title
                    binding.tvMessage.text = notification.body
                    binding.tvTimestamp.text = convertTimeStamp(notification.timestamp.toDate())
                }
                is HostNotification -> {
                    binding.tvNotificationTitle.text = notification.title
                    binding.tvMessage.text = notification.body
                    binding.tvTimestamp.text = convertTimeStamp(notification.timestamp.toDate())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = AlertCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        when (notifications[position]) {
            is BroadcastNotification -> {
                holder.bind(notifications[position] as BroadcastNotification)
            }
            is HostNotification -> {
                holder.bind(notifications[position] as HostNotification)
            }
        }
    }

    override fun getItemCount(): Int = notifications.size

    fun setData(newNotification: List<Notification>) {
        notifications.clear()
        notifications.addAll(newNotification)
        notifyDataSetChanged()
    }
}