package com.abhishek.calendar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.abhishek.calendar.viewModels.DeleteCalendarTaskViewModels
import com.abhishek.calendar.viewModels.GetCalenderTaskViewModel
import com.abhishek.calendar.viewModels.StoreCalendarTaskViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.Date

@RunWith(MockitoJUnitRunner::class)
class MainActivityTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storeCalendarTaskViewModel: StoreCalendarTaskViewModel

    @Mock
    private lateinit var deleteCalendarTaskViewModels: DeleteCalendarTaskViewModels

    @Mock
    private lateinit var getCalenderTaskViewModel: GetCalenderTaskViewModel

    @Mock
    private lateinit var observer: Observer<Any> // Define the type of data you want to observe

    private lateinit var mainActivity: MainActivity

    @Before
    fun setUp() {
        mainActivity = MainActivity()
        mainActivity.storeCalendarTaskViewModel = storeCalendarTaskViewModel
        mainActivity.deleteCalendarTaskViewModels = deleteCalendarTaskViewModels
        mainActivity.getCalenderTaskViewModel = getCalenderTaskViewModel
    }

    @Test
    fun testStoreCalendarTask() {
        // Given
        val title = "Test Title"
        val description = "Test Description"

        // When
        mainActivity.storeCalendarTask(title, description)

        // Then
        verify(storeCalendarTaskViewModel).storeCalendarTask(any())
    }

}
