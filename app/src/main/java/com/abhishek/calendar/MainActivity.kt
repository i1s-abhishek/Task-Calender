package com.abhishek.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishek.calendar.adapter.TaskAdapter
import com.abhishek.calendar.customViews.CustomCalendarView
import com.abhishek.calendar.dailogs.CustomAlertDialog
import com.abhishek.calendar.databinding.ActivityMainBinding
import com.abhishek.calendar.db.CalendarTaskViewModel
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
import com.abhishek.calendar.db.table.CalendarTaskTable
import com.abhishek.calendar.fragments.DayTaskListFragment
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
    private lateinit var calendarTaskViewModel: CalendarTaskViewModel

    private lateinit var taskAdapter: TaskAdapter

    private var tasks: ArrayList<Tasks> = arrayListOf()
    private lateinit var taskToBeDeleted: Tasks
    private var taskToBeDeletedPosition = -1
    private val userId = 1
    private var isActive = false
    private lateinit var calendarTaskTable: CalendarTaskTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        setupViewModel()
        setupUI()
        observeViewModels()
    }

    private fun setupViewModel() {
        storeCalendarTaskViewModel = ViewModelProvider(this)[StoreCalendarTaskViewModel::class.java]
        getCalenderTaskViewModel = ViewModelProvider(this)[GetCalenderTaskViewModel::class.java]
        deleteCalendarTaskViewModels =
            ViewModelProvider(this)[DeleteCalendarTaskViewModels::class.java]
        calendarTaskViewModel = ViewModelProvider(this)[CalendarTaskViewModel::class.java]
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
//                binding.calendarPicker.markCircleImage1(selectedDateCalendar.time)
//                binding.calendarPicker.markCircleImage2(selectedDateCalendar.time)
            }.show()
    }


    fun storeCalendarTask(title: String, description: String, date: Date) {
        if (Utility.isNetworkAvailable(this)) {
            calendarTaskTable =
                CalendarTaskTable(0, userId, title, description, Utility.getFormattedDate(date))
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
                            calendarTaskViewModel.insertCalenderTask(
                                this, calendarTaskTable
                            )
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
                            calendarTaskViewModel.deleteCalenderTaskByTaskTaskDetail(
                                this,
                                taskToBeDeleted.taskDetail?.title ?: "",
                                taskToBeDeleted.taskDetail?.description ?: ""
                            )
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
        val dialog = CustomAlertDialog(this, this, date)
        dialog.show()
    }

    override fun onDayLongClick(date: Date) {}

    override fun onRightButtonClick() {}

    override fun onLeftButtonClick() {}

    override fun onDateSelectorClick(date: Date) {
        showGoToDateDialog(date)
    }

    override fun onShowTaskOfDayClick(date: Date?) {
        isActive = true
        getDayTaskListData(date)
    }


    override fun onTaskInput(title: String, description: String, date: Date) {
        storeCalendarTask(title, description, date)
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

    private fun getDayTaskListData(date: Date?) {
        var firebaseItemList: ArrayList<CalendarTaskTable> = arrayListOf()
        if (date != null) {
            calendarTaskViewModel.getCalendarTaskByDate(Utility.getFormattedDate(date))
                ?.observe(this, Observer {
                    if (isActive) {
                        isActive = false
                        if (!it.isNullOrEmpty()) {
                            firebaseItemList = it as ArrayList<CalendarTaskTable>
                            val taskListFragment = DayTaskListFragment().apply {
                                arguments = Bundle().apply {
                                    putParcelableArrayList("taskList", firebaseItemList)
                                    putSerializable("date", date)
                                }
                            }
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, taskListFragment)
                                .addToBackStack(null).commit()
                        } else {
                            Utility.showToast(this, getString(R.string.task_of_day_message))
                        }
                    }
                })
        }

    }
}