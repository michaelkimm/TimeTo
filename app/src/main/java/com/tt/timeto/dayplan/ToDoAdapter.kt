package com.tt.timeto.dayplan

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tt.timeto.R

class ToDoAdapter(private val context: Context): RecyclerView.Adapter<ToDoAdapter.MyViewHolder>() {

    // 초기화
    private var toDoList: ArrayList<ToDo> = ArrayList<ToDo>()

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

        // 데이터 변수에 담기
        var uId = toDoList[holder.adapterPosition].toDoId
        var uTitle = toDoList[holder.adapterPosition].title
        var uContent = toDoList[holder.adapterPosition].content
        var uReservedDate = toDoList[holder.adapterPosition].reservedDate
        var uIsDone = toDoList[holder.absoluteAdapterPosition].isDone

        // 데이터 적용
        holder.titleText.text = uTitle
        holder.contentText.text = uContent
        if (uIsDone == true) {
            holder.titleText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.contentText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        // 수정 화면으로 이동
        holder.itemView.setOnClickListener {
            var intent: Intent = Intent(context, UpdateActivity::class.java)
            // 값 담기
            intent.putExtra("uId", uId)
            intent.putExtra("uTitle", uTitle)
            intent.putExtra("uContent", uContent)
            intent.putExtra("uReservedDate", uReservedDate?.toEpochDays())
            intent.putExtra("uIsDone", uIsDone)
            context.startActivity(intent)
        }
    }

    // 아이템 갯수
    override fun getItemCount(): Int {
        return toDoList.size
    }

    // 투두 등록
    fun setToDoList(toDoList: ArrayList<ToDo>) {
        this.toDoList = toDoList
        // 데이터 재설정
        notifyDataSetChanged()
    }

    // 투두 삭제
    fun deleteToDo(position: Int) {
        this.toDoList.removeAt(position)
    }

    // 투두 변경
    fun updateToDo(position: Int, toDo: ToDo) {
        this.toDoList.set(position, toDo)
    }
}
