package com.abhishek.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishek.calendar.adapter.TaskAdapter
import com.abhishek.calendar.customViews.CustomCalendarView
import com.abhishek.calendar.dailogs.CustomAlertDialog
import com.abhishek.calendar.databinding.ActivityMainBinding
import com.abhishek.calendar.interfaces.OnTaskDeleteListener
import com.abhishek.calendar.interfaces.OnTaskInputListener
import com.abhishek.calendar.models.request.DeleteTaskRequest
import com.abhishek.calendar.models.request.GetCalendarTaskListRequest
import com.abhishek.calendar.models.request.StoreTaskRequest
import com.abhishek.calendar.models.request.TaskDetail
import com.abhishek.calendar.models.response.Tasks
import com.abhishek.calendar.network.Status
import com.abhishek.calendar.utils.DialogUtils
import com.abhishek.calendar.utils.ItemSpacingDecoration
import com.abhishek.calendar.utils.Utility
import com.abhishek.calendar.viewModels.DeleteCalendarTaskViewModels
import com.abhishek.calendar.viewModels.GetCalenderTaskViewModel
import com.abhishek.calendar.viewModels.StoreCalendarTaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CustomCalendarView.CustomCalendarListener,
    OnTaskInputListener, OnTaskDeleteListener {

    private lateinit var binding: ActivityMainBinding

    lateinit var storeCalendarTaskViewModel: StoreCalendarTaskViewModel
    lateinit var deleteCalendarTaskViewModels: DeleteCalendarTaskViewModels
    lateinit var getCalenderTaskViewModel: GetCalenderTaskViewModel

    private lateinit var taskAdapter: TaskAdapter

    private var tasks: ArrayList<Tasks> = arrayListOf()
    private lateinit var taskToBeDeleted: Tasks
    private var taskToBeDeletedPosition = -1
    private val userId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupUI()
        observeViewModels()
    }

    private fun setupViewModel() {
        storeCalendarTaskViewModel = ViewModelProvider(this)[StoreCalendarTaskViewModel::class.java]
        getCalenderTaskViewModel = ViewModelProvider(this,)[GetCalenderTaskViewModel::class.java]
        deleteCalendarTaskViewModels = ViewModelProvider(this,)[DeleteCalendarTaskViewModels::class.java]
    }

    private fun setupUI() {
        binding.calendarPicker.setCustomCalendarListener(this)
        binding.calendarPicker.setShortWeekDays(false)
        binding.calendarPicker.showDateTitle(true)
        binding.calendarPicker.date = Date()
        getCalendarTaskList(1)
        setUpTaskAdapter()
    }


    private fun observeViewModels() {
        setupCalendarTaskListObserver()
        setupStoreCalendarTaskObserver()
        setupDeleteCalendarTaskObserver()
    }

    private fun setUpTaskAdapter() {
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(tasks, this)
        binding.recyclerViewTasks.adapter = taskAdapter

        val spacingTopInPixels = resources.getDimensionPixelSize(R.dimen.item_spacing_top)
        val spacingBottomInPixels = resources.getDimensionPixelSize(R.dimen.item_spacing_bottom)
        val spacingStartInPixels = resources.getDimensionPixelSize(R.dimen.item_spacing_start)
        val spacingEndInPixels = resources.getDimensionPixelSize(R.dimen.item_spacing_end)
        binding.recyclerViewTasks.addItemDecoration(
            ItemSpacingDecoration(
                spacingTopInPixels, spacingBottomInPixels, spacingStartInPixels, spacingEndInPixels
            )
        )
    }


    private fun showGoToDateDialog(date: Date) {
        val datePickerView = LayoutInflater.from(this).inflate(R.layout.date_picker, null)
        val datePicker = datePickerView.findViewById<DatePicker>(R.id.date_picker)
        datePicker.findViewById<View>(
            resources.getIdentifier("day", "id", "android")
        ).visibility = View.GONE

        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        datePicker.init(year, month, day, null)
        AlertDialog.Builder(this).setView(datePickerView).setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok) { _, _ ->

                val selectedYear = datePicker.year
                val selectedMonth = datePicker.month

                val selectedDateCalendar = Calendar.getInstance()
                selectedDateCalendar.set(Calendar.YEAR, selectedYear)
                selectedDateCalendar.set(Calendar.MONTH, selectedMonth)

                binding.calendarPicker.date = selectedDateCalendar.time
                binding.calendarPicker.markCircleImage1(selectedDateCalendar.time)
                binding.calendarPicker.markCircleImage2(selectedDateCalendar.time)
            }.show()
    }


    fun storeCalendarTask(title: String, description: String) {
        if (Utility.isNetworkAvailable(this)) {
            val storeTaskRequest = StoreTaskRequest(userId, TaskDetail(title, description))
            storeCalendarTaskViewModel.storeCalendarTask(storeTaskRequest)
        } else {
            Utility.showSnackBar(binding.recyclerViewTasks, getString(R.string.no_internet))
        }
    }

    private fun getCalendarTaskList(userId: Int) {
        if (Utility.isNetworkAvailable(this)) {
            val getCalendarTaskListRequest = GetCalendarTaskListRequest(userId)
            getCalenderTaskViewModel.getCalenderTaskList(getCalendarTaskListRequest)
        } else {
            Utility.showSnackBar(binding.recyclerViewTasks, getString(R.string.no_internet))
        }
    }

    private fun deleteCalendarTask(userId: Int, taskId: Int) {
        if (Utility.isNetworkAvailable(this)) {
            val deleteTaskRequest = DeleteTaskRequest(userId, taskId)
            deleteCalendarTaskViewModels.deleteCalendarTask(deleteTaskRequest)
        } else {
            Utility.showSnackBar(binding.recyclerViewTasks, getString(R.string.no_internet))
        }
    }


    private fun setupStoreCalendarTaskObserver() {
        storeCalendarTaskViewModel.getStoreCalendarTaskResult().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { it ->
                        if (it.status == "Success") {
                            getCalendarTaskList(1)
                            Utility.showToast(this, getString(R.string.added_task_message))
                        }
                    }
                    binding.progressBar.visibility = View.GONE
                }

                Status.LOADING -> showProgressBar()
                Status.ERROR -> handleObserverError()
            }

        })
    }

    private fun setupCalendarTaskListObserver() {
        getCalenderTaskViewModel.getCalenderTaskListResult().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    tasks.clear()
                    it.data?.let { it ->
                        if (!it.tasks.isNullOrEmpty()) {
                            taskAdapter.setTasks(it.tasks)
                            binding.titleTextView.visibility = View.VISIBLE
                        } else {
                            binding.titleTextView.visibility = View.GONE
                        }
                    }
                    binding.progressBar.visibility = View.GONE

                }

                Status.LOADING -> showProgressBar()
                Status.ERROR -> handleObserverError()
            }
        })
    }

    private fun setupDeleteCalendarTaskObserver() {
        deleteCalendarTaskViewModels.getDeleteCalendarTaskResult().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { it ->
                        if (it.status == "Success") {
                            if (taskToBeDeletedPosition != -1) taskAdapter.removeTask(
                                taskToBeDeletedPosition
                            );
                            Utility.showToast(this, getString(R.string.task_deleted_message))
                        }
                    }
                    binding.progressBar.visibility = View.GONE
                }

                Status.LOADING -> showProgressBar()
                Status.ERROR -> handleObserverError()
            }

        })
    }

    private fun handleObserverError() {
        hideProgressBar()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDayClick(date: Date) {
        val dialog = CustomAlertDialog(this, this)
        dialog.show()
    }

    override fun onDayLongClick(date: Date) {}

    override fun onRightButtonClick() {}

    override fun onLeftButtonClick() {}

    override fun onDateSelectorClick(date: Date) {
        showGoToDateDialog(date)
    }


    override fun onTaskInput(title: String, description: String) {
        storeCalendarTask(title, description)
    }

    override fun onDeleteClicked(position: Int) {
        taskToBeDeleted = tasks[position]
        taskToBeDeletedPosition = position
        DialogUtils.showDeleteConfirmationDialog(this) {
            deleteCalendarTask(1, taskToBeDeleted.taskId ?: 0)
        }
    }


    override fun onTaskItemClick(taskDetail: TaskDetail) {
        DialogUtils.showTaskDetailDialog(this, taskDetail)
    }

}