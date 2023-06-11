package com.tt.timeto.dayplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tt.timeto.R

class ToDoAdapter: RecyclerView.Adapter<ToDoAdapter.MyViewHolder>() {

    private var toDoList = ArrayList<ToDo>()
    
    // 투두 등록
    fun setToDoList(toDoList: ArrayList<ToDo>) {
        this.toDoList = toDoList
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.title_text)
        val contentText: TextView = itemView.findViewById(R.id.content_text)
    }

    // 화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return MyViewHolder(view)
    }

    // 데이터 설정
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.titleText.text = toDoList[holder.adapterPosition].title
        holder.contentText.text = toDoList[holder.adapterPosition].content
    }

    // 아이템 갯수
    override fun getItemCount(): Int {
        return toDoList.size
    }
}
