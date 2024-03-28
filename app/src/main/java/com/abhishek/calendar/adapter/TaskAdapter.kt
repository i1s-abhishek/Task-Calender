package com.abhishek.calendar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.calendar.interfaces.OnTaskDeleteListener
import com.abhishek.calendar.models.request.TaskDetail
import com.abhishek.calendar.models.response.Tasks
import com.abhishek.calendar.databinding.TaskItemLayoutBinding

class TaskAdapter(
    private val tasks: ArrayList<Tasks>,
    private val onDeleteClickListener: OnTaskDeleteListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    fun setTasks(tasks: List<Tasks>) {
        this.tasks.clear()
        this.tasks.addAll(tasks)
        notifyDataSetChanged()
    }

    fun removeTask( taskToBeDeletedPosition: Int) {
        tasks.removeAt(taskToBeDeletedPosition)
        notifyItemRemoved(taskToBeDeletedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position].taskDetail
        task?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    inner class TaskViewHolder(private val binding: TaskItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskDetail) {
            binding.apply {
                titleTextView.text = task.title
                descriptionTextView.text = task.description

                deleteImageView.setOnClickListener {
                    onDeleteClickListener.onDeleteClicked(adapterPosition)
                }
                root.setOnClickListener { onDeleteClickListener.onTaskItemClick(task) }
            }
        }
    }
}
