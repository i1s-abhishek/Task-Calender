package com.abhishek.calendar.network

import com.abhishek.calendar.models.request.DeleteTaskRequest
import com.abhishek.calendar.models.request.GetCalendarTaskListRequest
import com.abhishek.calendar.models.request.StoreTaskRequest
import com.abhishek.calendar.models.response.DeleteTaskResponse
import com.abhishek.calendar.models.response.GetCalendarTaskListResponse
import com.abhishek.calendar.models.response.StoreTaskResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiHelper {

    suspend fun storeCalendarTask(storeTaskRequest: StoreTaskRequest): StoreTaskResponse

    suspend fun getCalendarTaskList(getCalendarTaskListRequest: GetCalendarTaskListRequest): GetCalendarTaskListResponse

    suspend fun deleteCalendarTask(deleteTaskRequest: DeleteTaskRequest): DeleteTaskResponse

}
