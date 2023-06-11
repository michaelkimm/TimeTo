package com.tt.timeto.monthplan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tt.timeto.CalendarUtil
import com.tt.timeto.R
import com.tt.timeto.dayplan.DayPlanActivity
import java.util.Calendar
import java.util.Date

class CalendarAdapter(private val context: Context, private val dayList: ArrayList<Date>):
    RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.dayText)
    }

    // 화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_item, parent, false)

        return ItemViewHolder(view)
    }

    // 데이터 설정
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        // 날짜 변수에 담기
        var monthDate = dayList[holder.adapterPosition]

        // 초기화
        var dateCalendar = Calendar.getInstance()

        // 날짜 캘린더에 담기
        dateCalendar.time = monthDate

        // 캘린더 값 날짜 변수에 담기
        var dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH)

        holder.dayText.text = dayNo.toString()

        // 넘어온 놀짜
        var iYear   = dateCalendar.get(Calendar.YEAR)
        var iMonth  = dateCalendar.get(Calendar.MONTH)
        var iDay    = dateCalendar.get(Calendar.DAY_OF_MONTH)

        // 현재 날짜
        var selectYear = CalendarUtil.selectedDate.get(Calendar.YEAR)
        var selectMonth = CalendarUtil.selectedDate.get(Calendar.MONTH)
        var selectDay = CalendarUtil.selectedDate.get(Calendar.DAY_OF_MONTH)

        // 넘어온 날짜와 현재 날짜 비교
        if (iYear == selectYear && iMonth == selectMonth) { // 같다면 진한 색상
            holder.dayText.setTextColor(Color.parseColor("#000000"))    // 검정

            // 현재 일자 비교해서 배경 색상 변경
            if (selectDay == dayNo) {
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            }

            // 텍스트 색상 지정
            if ((position + 1) % 7 == 0) {  // 토요일은 파랑
                holder.dayText.setTextColor(Color.BLUE)
            } else if (position % 7 == 0) { // 일요일은 빨강
                holder.dayText.setTextColor(Color.RED)
            }
        } else {    // 다르다면 연한 색상
            holder.dayText.setTextColor(Color.parseColor("#B4B4B4"))    // 연한 검정

            // 텍스트 색상 지정
            if ((position + 1) % 7 == 0) {  // 토요일은 연한 파랑
//                holder.dayText.setTextColor(Color.parseColor("#74F0E4"))
            } else if (position % 7 == 0) { // 일요일은 연한 빨강
                holder.dayText.setTextColor(Color.parseColor("#FF9999"))
            }
        }

        // 날짜 클릭 이벤트
        holder.itemView.setOnClickListener {
            // 인터페이스를 통해 날짜를 넘겨줌


            var yearMonDay = "$iYear 년 $iMonth 월 $iDay 일"

            // dayplan으로 화면 전환
            var intent: Intent = Intent(context, DayPlanActivity::class.java)
            intent.putExtra("year", iYear)
            intent.putExtra("month", iMonth)
            intent.putExtra("day", iDay)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

}