package com.example.recyclervieweffects

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue
import kotlin.math.log

abstract class ListItemTouchCallback(context: Context) :
    ItemTouchHelper.SimpleCallback(0, LEFT) {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_del_icon)
    private val intrinsicWidth = deleteIcon?.intrinsicWidth
    private val intrinsicHeight = deleteIcon?.intrinsicHeight
    private val background = ColorDrawable()
    private var backgroundColor = Color.parseColor("#CBCBCB")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        val fromPos = viewHolder.adapterPosition
        val toPos = target.adapterPosition
        // move item in `fromPos` to `toPos` in adapter.
        return false// true if moved, false otherwise
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        if (viewHolder != null) {

        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        // Calculate position of delete icon
        val deleteIconTop = itemView.top + (itemHeight - this!!.intrinsicHeight!!) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight!!) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth!!
        var deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight
        val deleteIconWidth = (deleteIconLeft - deleteIconRight).absoluteValue

        val swipedAreaWidth =
            (dX * (deleteIconWidth + (2 * deleteIconMargin)).absoluteValue) / itemView.width

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + swipedAreaWidth,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                swipedAreaWidth,
                dY,
                actionState,
                true
            )
            return
        }

        // Draw the red delete background
        background.color = backgroundColor
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)

        recyclerView.setOnTouchListener(
            object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    val x = event?.x
                    val y = event?.y
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            //Check if the x and y position of the touch is inside the bitmap

                            val deviceWidth =
                                itemView.context.resources.displayMetrics.widthPixels.toFloat()
                            val leftPositionOfSwipedView = itemView.right + dX.toInt()
                            println("onSwipe : dX - " + dX + " deviceWidth - " + deviceWidth + " leftPosition - " + (itemView.right + dX.toInt()) + " rightPosition - " + itemView.right)
                            if (leftPositionOfSwipedView <= 0 &&
                                x!! > deleteIconLeft && x < deleteIconRight && y!! > deleteIconTop && y < deleteIconBottom
                            ) {
                                //Drawable touched

                                if (viewHolder.adapterPosition >= 0 && deleteIconRight > 0) {
                                    onDeleteClicked(viewHolder.adapterPosition)
                                    deleteIconRight = 0
                                }
                            }
                            return true
                        }
                    }
                    return false
                }
            })

        // Draw the delete icon
        deleteIcon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon?.draw(c)

        Log.v("Item swipe", "dX - $swipedAreaWidth , dY - $dY, isActive - $isCurrentlyActive, state - $actionState")

        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            swipedAreaWidth,
            dY,
            actionState,
            true
        )
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

    abstract fun onDeleteClicked(itemPosition: Int)
}