package com.abhishek.calendar.network

import com.abhishek.calendar.models.request.DeleteTaskRequest
import com.abhishek.calendar.models.request.GetCalendarTaskListRequest
import com.abhishek.calendar.models.request.StoreTaskRequest
import com.abhishek.calendar.models.response.DeleteTaskResponse
import com.abhishek.calendar.models.response.GetCalendarTaskListResponse
import com.abhishek.calendar.models.response.StoreTaskResponse
import retrofit2.http.*

interface ApiService {

    @POST("api/storeCalendarTask")
    suspend fun storeCalendarTask(@Body storeCalendarTask: StoreTaskRequest): StoreTaskResponse

    @POST("api/getCalendarTaskList")
    suspend fun getCalendarTaskList(@Body request: GetCalendarTaskListRequest): GetCalendarTaskListResponse

    @POST("api/deleteCalendarTask")
    suspend fun deleteCalendarTask(@Body request: DeleteTaskRequest): DeleteTaskResponse

}
