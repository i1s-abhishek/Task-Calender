package com.abhishek.calendar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.calendar.R
import com.abhishek.calendar.databinding.ItemTaskBinding
import com.abhishek.calendar.db.table.CalendarTaskTable

class DayTaskListAdapter(private val taskList: List<CalendarTaskTable>) :
    RecyclerView.Adapter<DayTaskListAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = taskList[position]
        holder.binding.textViewTitle.text = currentItem.title
        holder.binding.textViewDescription.text = currentItem.description
    }

    override fun getItemCount() = taskList.size
}
