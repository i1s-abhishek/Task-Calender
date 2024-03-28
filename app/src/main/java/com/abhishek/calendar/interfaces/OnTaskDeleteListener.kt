package com.abhishek.calendar.interfaces

import com.abhishek.calendar.models.request.TaskDetail

interface OnTaskDeleteListener {
    fun onDeleteClicked(position: Int)
    fun onTaskItemClick(taskDetail: TaskDetail)
}
