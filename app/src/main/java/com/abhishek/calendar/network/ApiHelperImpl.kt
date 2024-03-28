package com.abhishek.calendar.network

import com.abhishek.calendar.models.request.DeleteTaskRequest
import com.abhishek.calendar.models.request.GetCalendarTaskListRequest
import com.abhishek.calendar.models.request.StoreTaskRequest
import javax.inject.Inject
import javax.inject.Singleton


class ApiHelperImpl  @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun storeCalendarTask(storeTaskRequest: StoreTaskRequest) =
        apiService.storeCalendarTask(storeTaskRequest)

    override suspend fun getCalendarTaskList(getCalendarTaskListRequest: GetCalendarTaskListRequest) =
        apiService.getCalendarTaskList(getCalendarTaskListRequest)

    override suspend fun deleteCalendarTask(deleteTaskRequest: DeleteTaskRequest) =
        apiService.deleteCalendarTask(deleteTaskRequest)
}