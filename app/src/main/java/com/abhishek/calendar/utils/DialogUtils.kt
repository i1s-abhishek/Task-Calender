package com.abhishek.calendar.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.abhishek.calendar.R
import com.abhishek.calendar.models.request.TaskDetail

object DialogUtils {
    fun showDeleteConfirmationDialog(
        context: Context,
        onDeleteConfirmed: () -> Unit
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(context.getString(R.string.confirm_delete_title))
        alertDialogBuilder.setMessage(context.getString(R.string.confirm_delete_message))
        alertDialogBuilder.setPositiveButton(context.getString(R.string.confirm_delete_positive)) { dialog, _ ->
            onDeleteConfirmed.invoke()
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton(context.getString(R.string.confirm_delete_negative)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun showTaskDetailDialog(context: Context, taskDetail: TaskDetail) {
        AlertDialog.Builder(context)
            .setTitle(taskDetail.title)
            .setMessage(taskDetail.description)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}
