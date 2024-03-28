package com.abhishek.calendar.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhishek.calendar.network.ApiHelper

class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeleteCalendarTaskViewModels::class.java)) {
            return DeleteCalendarTaskViewModels(apiHelper) as T
        } else if (modelClass.isAssignableFrom(GetCalenderTaskViewModel::class.java)) {
            return GetCalenderTaskViewModel(apiHelper) as T
        } else if (modelClass.isAssignableFrom(StoreCalendarTaskViewModel::class.java)) {
            return StoreCalendarTaskViewModel(apiHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}