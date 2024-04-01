package com.abhishek.calendar.dailogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.abhishek.calendar.R
import com.abhishek.calendar.interfaces.OnTaskInputListener
import java.util.Date

class CustomAlertDialog(
    context: Context, private val onTaskInputListener: OnTaskInputListener, date: Date
) : AlertDialog(context) {

    init {
        setCancelable(false)
        setTitle(context.getString(R.string.add_task_title))

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_task_input, null)
        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)

        setView(dialogView)
        setButton(BUTTON_POSITIVE, context.getString(R.string.add_button_label)) { dialog, _ ->
            val title = editTextTitle.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                onTaskInputListener.onTaskInput(title, description, date)
                dialog.dismiss()
            } else {
                val errorMessage = when {
                    title.isEmpty() -> context.getString(R.string.title_empty_error)
                    description.isEmpty() -> context.getString(R.string.description_empty_error)
                    else -> context.getString(R.string.title_description_empty_error)
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel_button_label)) { dialog, _ ->
            dialog.dismiss()
        }
    }
}
