package com.example.recyclervieweffects

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class CustomSwipeHelper(var context: Context, var recyclerView: RecyclerView, var buttonWidth: Int): ItemTouchHelper.SimpleCallback(0, LEFT) {

    private var swipePosition = -1
    private var swipeThreshold = 0.5f
    private var buttonList = ArrayList<MyButton>()
    private lateinit var buttonBuffer: HashMap<Int, ArrayList<MyButton>>
    private var gestureDetector: GestureDetector? = null
    private lateinit var removerQueue: Queue<Int>

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {

            if (e == null) return false

            for (button in buttonList) {
                if (button.onClick(e.x, e.y)) break
            }
            return true
        }
    }

    private val onTouchListener = View.OnTouchListener { v, event ->

        if (swipePosition < 0) false
        val point = Point(event.rawX.toInt(), event.rawY.toInt())

        val swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition)
        val swipedItem = swipeViewHolder?.itemView
        val rect = Rect()
        swipedItem?.getGlobalVisibleRect(rect)

        if (event.action == MotionEvent.ACTION_DOWN ||
            event.action == MotionEvent.ACTION_UP ||
            event.action == MotionEvent.ACTION_MOVE
        ) {

            if (rect.top < point.y && rect.bottom > point.y)
                gestureDetector?.onTouchEvent(event)
            else {
                removerQueue.add(swipePosition)
                swipePosition = -1
                recoverSwipedItem()
            }
        }

        false

    }

    init {
        gestureDetector = GestureDetector(context, gestureListener)
        recyclerView.setOnTouchListener(onTouchListener)
        buttonBuffer = HashMap()
        removerQueue = object : LinkedList<Int>() {
            override fun add(element: Int): Boolean {
                return if (contains(element)) false
                else super.add(element)
            }
        }

        attachSwipe()
    }

    private fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun recoverSwipedItem() {
        synchronized(context) {
            while (removerQueue.isNotEmpty()) {
                val pos = removerQueue.poll()
                if (pos != null && pos > -1) recyclerView.adapter?.notifyItemChanged(pos)
            }
        }
    }

    inner class MyButton(
        private var context: Context,
        private var text: String,
        private var imageResId: Int,
        private var textSize: Float,
        private var color: Int,
        private var buttonClickListener: MyButtonClickListener
    ) {
        private var pos: Int = -1
        private var clickRegion: RectF? = null
        private var resources: Resources = context.resources

        fun onClick(x: Float, y: Float): Boolean {

            if (clickRegion != null && clickRegion!!.contains(x, y)) {
                buttonClickListener.onClick(pos)
                return true
            }

            return false
        }

        fun onDraw(c: Canvas, rectF: RectF, pos: Int) {

            val p = Paint()
            p.color = color
            c.drawRect(rectF, p)

            //Text
            p.color = (Color.WHITE)
            p.textSize = textSize
            val r = Rect()
            val cHeight = rectF.height()
            val cWidth = rectF.width()
            p.textAlign = Paint.Align.LEFT
            p.getTextBounds(text, 0, text.length, r)
            var x = 0f
            var y = 0f

            if (imageResId == 0) {
                x = cWidth / 2f - r.width() / 2f - r.left
                y = cHeight / 2f + r.height() / 2f - r.bottom
                c.drawText(text, rectF.left + x, rectF.top + y, p)
            } else {
                val d = ActivityCompat.getDrawable(context, imageResId)
                val bitmap = drawableToBitmap(d!!)
                c.drawBitmap(
                    bitmap,
                    (rectF.left + rectF.right) / 2,
                    (rectF.top + rectF.bottom) / 2,
                    p
                )
            }

            clickRegion = rectF
            this.pos = pos
        }
    }

    private fun drawableToBitmap(d: Drawable): Bitmap {

        if (d is BitmapDrawable) return d.bitmap
        val bitmap =
            Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        d.setBounds(0, 0, canvas.width, canvas.height)
        d.draw(canvas)
        return bitmap
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipePosition != pos) removerQueue.add(swipePosition)
        swipePosition = pos
        if (buttonBuffer.containsKey(swipePosition))
            buttonList = buttonBuffer[swipePosition]!!
        else
            buttonList.clear()

        buttonBuffer.clear()
        swipeThreshold = 0.5f * buttonList.size * buttonWidth
        recoverSwipedItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = swipeThreshold

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float = 0.1f * defaultValue

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float = 5f * defaultValue

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView

        if (pos < 0) {
            swipePosition = pos
            return
        }

        if (actionState == ACTION_STATE_SWIPE) {

            if (dX < 0) {
                var buffer = ArrayList<MyButton>()
                if (!buttonBuffer.containsKey(pos)) {
                    instantiateMyButton(viewHolder, buffer)
                    buttonBuffer.put(pos, buffer)
                } else {
                    buffer = buttonBuffer[pos]!!
                }
                translationX = dX * buffer.size * buttonWidth / itemView.width
                drawButton(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawButton(
        c: Canvas,
        itemView: View,
        buffer: java.util.ArrayList<MyButton>,
        pos: Int,
        translationX: Float
    ) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1* translationX/buffer.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(c, RectF(left, itemView.top.toFloat(), right, itemView.bottom.toFloat()), pos)
            right = left
        }
    }

    abstract fun instantiateMyButton(
        viewHolder: RecyclerView.ViewHolder,
        buffer: java.util.ArrayList<MyButton>
    )
}