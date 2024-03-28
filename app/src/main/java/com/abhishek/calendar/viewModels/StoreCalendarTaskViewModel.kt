package com.abhishek.calendar.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishek.calendar.models.request.StoreTaskRequest
import com.abhishek.calendar.models.response.StoreTaskResponse
import com.abhishek.calendar.network.ApiHelper
import com.abhishek.calendar.network.Resource
import kotlinx.coroutines.launch

class StoreCalendarTaskViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    private val storeCalendarTaskResult = MutableLiveData<Resource<StoreTaskResponse>>()

    fun storeCalendarTask(storeTaskRequest: StoreTaskRequest) {
        viewModelScope.launch {
            storeCalendarTaskResult.postValue(Resource.loading(null))
            try {
                val submitStoreTaskRequestFromApi = apiHelper.storeCalendarTask(storeTaskRequest)
                storeCalendarTaskResult.postValue(Resource.success(submitStoreTaskRequestFromApi))
            } catch (t: Throwable) {
                storeCalendarTaskResult.postValue(t.message?.let { Resource.error(it, null) })
            }
        }
    }

    fun getStoreCalendarTaskResult(): LiveData<Resource<StoreTaskResponse>> {
        return storeCalendarTaskResult
    }
}