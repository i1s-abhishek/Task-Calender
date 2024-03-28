package com.abhishek.calendar.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishek.calendar.models.request.GetCalendarTaskListRequest
import com.abhishek.calendar.models.response.GetCalendarTaskListResponse
import com.abhishek.calendar.network.ApiHelper
import com.abhishek.calendar.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetCalenderTaskViewModel @Inject constructor(private val apiHelper: ApiHelper) : ViewModel() {

    private val getCalenderTaskResult = MutableLiveData<Resource<GetCalendarTaskListResponse>>()

    fun getCalenderTaskList(getCalendarTaskListRequest: GetCalendarTaskListRequest) {
        viewModelScope.launch {
            getCalenderTaskResult.postValue(Resource.loading(null))
            try {
                val submitEditAddressFromApi =
                    apiHelper.getCalendarTaskList(getCalendarTaskListRequest)
                getCalenderTaskResult.postValue(Resource.success(submitEditAddressFromApi))
            } catch (t: Throwable) {
                getCalenderTaskResult.postValue(t.message?.let { Resource.error(it, null) })
            }
        }
    }

    fun getCalenderTaskListResult(): LiveData<Resource<GetCalendarTaskListResponse>> {
        return getCalenderTaskResult
    }
}