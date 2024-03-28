package com.abhishek.calendar.models.response

import com.abhishek.calendar.models.request.TaskDetail
import com.google.gson.annotations.SerializedName

data class GetCalendarTaskListResponse(
    @SerializedName("tasks") val tasks: List<Tasks>
)

data class Tasks(
    @SerializedName("task_id") var taskId: Int? = null,
    @SerializedName("task_detail") var taskDetail: TaskDetail? = TaskDetail()
)