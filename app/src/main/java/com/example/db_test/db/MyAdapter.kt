package com.example.db_test.db

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.db_test.EditActivity
import com.example.db_test.R

class MyAdapter(listMain:ArrayList<ListItem>, contextM: Context) : RecyclerView.Adapter<MyAdapter.myHolder>() {
    var listArray = listMain
    var context = contextM

    class myHolder(itemView: View, contextV: Context) : RecyclerView.ViewHolder(itemView) {
        val tvTitle:TextView = itemView.findViewById(R.id.tvTitle)
        val tvTime:TextView = itemView.findViewById(R.id.tvTime)
        val context = contextV

        fun setData(item:ListItem){
            tvTitle.text = item.title
            tvTime.text = item.time

            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                    putExtra(MyIntentConstant.I_TITLE_KEY, item.title)
                    putExtra(MyIntentConstant.I_DESC_KEY, item.desc)
                    putExtra(MyIntentConstant.I_URI_KEY, item.uri)
                    putExtra(MyIntentConstant.I_TIME_KEY, item.time)
                    putExtra(MyIntentConstant.I_ID_KEY, item.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        val inflater = LayoutInflater.from(parent.context)
        return myHolder(inflater.inflate(R.layout.rc_item, parent, false), context)
    }

    //получаем количество элементов в массиве
    override fun getItemCount(): Int {
        return listArray.size
    }
    //передаем позицию выбранного элемента
    override fun onBindViewHolder(holder: myHolder, position: Int) {
        holder.setData(listArray.get(position))
    }

    fun updateAdapter(listItems:List<ListItem>){
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    // удаляем выбранный элемент
    fun removeItem(pos: Int,dbManager: MyDbManager){
        dbManager.delFromDb(listArray[pos].id.toString())
        listArray.removeAt(pos)
        notifyItemRangeChanged(0,listArray.size)
        notifyItemRemoved(pos)
    }
}