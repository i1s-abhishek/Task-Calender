package com.abhishek.calendar.models.response

import com.google.gson.annotations.SerializedName

class DeleteTaskResponse(
    @SerializedName("status") var status: String? = null
)