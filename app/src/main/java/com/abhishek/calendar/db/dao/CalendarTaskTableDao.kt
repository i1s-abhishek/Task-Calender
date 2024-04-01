package com.abhishek.calendar.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abhishek.calendar.db.table.CalendarTaskTable

@Dao
interface CalendarTaskTableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalenderTask(calendarTaskTable: CalendarTaskTable)

    @Query("SELECT * FROM calendar_task_table WHERE date = :formattedDate")
    fun getCalendarTaskByDate(formattedDate: String): LiveData<List<CalendarTaskTable>>

    @Query("DELETE FROM calendar_task_table WHERE user_id = :userId AND task_id = :taskId")
    suspend fun deleteCalenderTaskByTaskId(userId: Int, taskId: Int)

    @Query("DELETE FROM calendar_task_table WHERE title = :title AND description = :description")
    suspend fun deleteCalenderTaskByTaskId(title: String, description: String)

}