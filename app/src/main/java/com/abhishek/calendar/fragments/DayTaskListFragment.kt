package com.abhishek.calendar.fragments

import android.view.LayoutInflater

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishek.calendar.adapter.DayTaskListAdapter
import com.abhishek.calendar.databinding.FragmentDayTaskListBinding
import com.abhishek.calendar.db.table.CalendarTaskTable
import java.text.SimpleDateFormat

import java.util.*

class DayTaskListFragment : Fragment() {

    private var _binding: FragmentDayTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var dayTaskListAdapter: DayTaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedDate: Date? = arguments?.getSerializable("date") as? Date
        selectedDate?.let {
            setHeader(it)
        }

        val taskList: ArrayList<CalendarTaskTable>? = arguments?.getParcelableArrayList("taskList")

        taskList?.let {
            if (it.isNotEmpty()) {
                // Initialize RecyclerView and Adapter
                dayTaskListAdapter = DayTaskListAdapter(it)
                binding.recyclerViewTasks.apply {
                    adapter = dayTaskListAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
        setListener()
    }

    private fun setHeader(selectedDate: Date) {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
        binding.includeHeader.tvTitleFragment.text = "Tasks for $formattedDate"
    }

    private fun setListener() {
        binding.includeHeader.ivBackFragment.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
