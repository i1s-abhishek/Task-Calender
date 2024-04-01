package com.abhishek.calendar.interfaces

import java.util.Date

interface OnTaskInputListener {
    fun onTaskInput(title: String, description: String, date: Date)
}