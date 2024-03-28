package com.abhishek.calendar.models.response

import com.google.gson.annotations.SerializedName

data class StoreTaskResponse(
    @SerializedName("status") var status: String? = null
)