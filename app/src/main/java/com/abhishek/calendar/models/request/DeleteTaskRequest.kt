package com.abhishek.calendar.models.request

import com.google.gson.annotations.SerializedName

data class DeleteTaskRequest(
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("task_id") var taskId: Int? = null
)