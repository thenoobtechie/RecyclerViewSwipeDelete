package com.example.recyclervieweffects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.test_list_item.view.*

class TestListAdapter(var itemList: List<TestItem>): RecyclerView.Adapter<TestListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(item: TestItem) {
            itemView.title.text = item.title
            itemView.subTitle.text = item.subTitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.test_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    fun setData(itemList: List<TestItem>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }

    fun removeItem(itemPosition: Int) {
        (this.itemList as ArrayList).removeAt(itemPosition)
        notifyItemRemoved(itemPosition)
    }
}