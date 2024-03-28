package com.abhishek.calendar.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemSpacingDecoration(
    private val spacingTop: Int,
    private val spacingBottom: Int,
    private val spacingStart: Int,
    private val spacingEnd: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = spacingTop
        outRect.bottom = spacingBottom
        outRect.left = spacingStart
        outRect.right = spacingEnd
    }
}
