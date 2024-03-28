package com.abhishek.calendar.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishek.calendar.models.request.DeleteTaskRequest
import com.abhishek.calendar.models.response.DeleteTaskResponse
import com.abhishek.calendar.network.ApiHelper
import com.abhishek.calendar.network.Resource
import kotlinx.coroutines.launch

class DeleteCalendarTaskViewModels(private val apiHelper: ApiHelper) : ViewModel() {

    private val deleteTaskResponseResult = MutableLiveData<Resource<DeleteTaskResponse>>()

    fun deleteCalendarTask(deleteTaskRequest: DeleteTaskRequest) {
        viewModelScope.launch {
            deleteTaskResponseResult.postValue(Resource.loading(null))
            try {
                val submitEditAddressFromApi =
                    apiHelper.deleteCalendarTask(deleteTaskRequest)
                deleteTaskResponseResult.postValue(Resource.success(submitEditAddressFromApi))
            } catch (t: Throwable) {
                deleteTaskResponseResult.postValue(t.message?.let { Resource.error(it, null) })
            }
        }
    }

    fun getDeleteCalendarTaskResult(): LiveData<Resource<DeleteTaskResponse>> {
        return deleteTaskResponseResult
    }
}