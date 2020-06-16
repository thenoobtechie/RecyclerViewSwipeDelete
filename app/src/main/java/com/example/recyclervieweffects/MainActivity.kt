package com.example.recyclervieweffects

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter = TestListAdapter(ArrayList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_test_list.adapter = adapter
        adapter.setData(getList())
        val itemTouchHelper = ItemTouchHelper(object : ListItemTouchCallback(this) {
            override fun onDeleteClicked(itemPosition: Int) {
                adapter.removeItem(itemPosition)
            }


        })/*
        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(this, rv_test_list) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder?,
                underlayButtons: MutableList<UnderlayButton>?
            ) {
                underlayButtons?.add(UnderlayButton(
                    "Delete",
                    0,
                    Color.parseColor("#FF3C30"),
                    UnderlayButtonClickListener {
                        Toast.makeText(applicationContext, "Delete clicked", Toast.LENGTH_LONG).show()
                        adapter.removeItem(it)
                    }
                )
                )
            }

        })*/
        itemTouchHelper.attachToRecyclerView(rv_test_list)
        rv_test_list.itemAnimator = SlideInLeftAnimator(OvershootInterpolator())
        rv_test_list.itemAnimator?.apply {
            addDuration = 1000
            removeDuration = 1000
            moveDuration = 200
            changeDuration = 1000
        }

        /*object : CustomSwipeHelper(this, rv_test_list, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: java.util.ArrayList<MyButton>
            ) {
                buffer.add(MyButton(
                    this@MainActivity,
                    "Delete",
                    R.drawable.ic_del_icon,
                    20f,
                    Color.parseColor("#FF3c30"),
                    object : MyButtonClickListener {
                        override fun onClick(pos: Int) {
                            Toast.makeText(this@MainActivity, "Clicked", Toast.LENGTH_SHORT).show()
                        }

                    }
                ))
            }

        }*/

        btn_reset.setOnClickListener {
            adapter.setData(getList())
        }

        btn_delete_first.setOnClickListener {
            adapter.removeItem(0)
        }

    }


    private fun getList(): List<TestItem> = arrayListOf(
        TestItem("One", "First Item"),
        TestItem("Two", "Second Item"),
        TestItem("Three", "Third Item"),
        TestItem("Four", "Fourth Item"),
        TestItem("Five", "Fifth Item"),
        TestItem("Six", "Sixth Item"),
        TestItem("Seven", "Seventh Item"),
        TestItem("Eight", "Eighth Item"),
        TestItem("Nine", "Ninth Item"),
        TestItem("Ten", "Tenth Item")
    )
}