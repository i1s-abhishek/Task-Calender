package com.abhishek.calendar.models.request

import com.google.gson.annotations.SerializedName

data class StoreTaskRequest(
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("task") var taskModel: TaskDetail = TaskDetail()
)

data class TaskDetail(
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null
)