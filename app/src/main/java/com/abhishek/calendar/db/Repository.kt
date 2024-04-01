package com.abhishek.calendar.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.abhishek.calendar.db.table.CalendarTaskTable
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val appDatabase: AppDatabase) {

    var taskList: LiveData<List<CalendarTaskTable>>? = null

    fun insertCalendarTaskData(context: Context, userDetailTable: CalendarTaskTable) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.calendarTaskDao().insertCalenderTask(userDetailTable)
        }
    }

    fun getCalendarTaskByDate(formattedDate: String): LiveData<List<CalendarTaskTable>>? {
        taskList = appDatabase.calendarTaskDao().getCalendarTaskByDate(formattedDate)
        return taskList
    }

    fun deleteTaskById(
        context: Context, userId: Int, taskId: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.calendarTaskDao().deleteCalenderTaskByTaskId(userId, taskId)
        }
    }

    fun deleteTaskByByTaskTaskDetail(
        context: Context, title: String, description: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.calendarTaskDao().deleteCalenderTaskByTaskId(title, description)
        }
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object RepositoryModule {
        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
            return AppDatabase.getInstance(appContext)
        }
    }
}
