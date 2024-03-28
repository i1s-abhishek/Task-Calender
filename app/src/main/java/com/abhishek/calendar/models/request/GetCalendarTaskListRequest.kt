package com.abhishek.calendar.models.request

import com.google.gson.annotations.SerializedName


data class GetCalendarTaskListRequest(
    @SerializedName("user_id") var userId: Int? = null
)
