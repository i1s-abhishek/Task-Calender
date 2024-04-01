package com.abhishek.calendar.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.abhishek.calendar.db.table.CalendarTaskTable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class CalendarTaskViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    var taskList: LiveData<List<CalendarTaskTable>>? = null

    fun insertCalenderTask(
        @ApplicationContext context: Context, calendarTaskTable: CalendarTaskTable
    ) {
        repository.insertCalendarTaskData(context, calendarTaskTable)
    }

    fun getCalendarTaskByDate(formattedDate: String): LiveData<List<CalendarTaskTable>>? {
        taskList = repository.getCalendarTaskByDate(formattedDate)
        return taskList
    }

    fun deleteCalenderTaskByTaskId(@ApplicationContext context: Context, userId: Int, taskId: Int) {
        repository.deleteTaskById(context, userId, taskId)
    }

    fun deleteCalenderTaskByTaskTaskDetail(
        @ApplicationContext context: Context, title: String, description: String
    ) {
        repository.deleteTaskByByTaskTaskDetail(context, title, description)
    }
}





