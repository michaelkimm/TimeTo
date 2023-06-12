package com.tt.timeto

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.timeto.databinding.ActivityMainBinding
import com.tt.timeto.monthplan.CalendarAdapter
import com.tt.timeto.util.CalendarUtil
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // binding 초기화
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        
        // 화면 설정
        setMonthView()
        
        // 이전 달 버튼 이벤트
        binding.preBtn.setOnClickListener {
            // 현재 월 -1 변수에 담기
            CalendarUtil.selectedDate.add(Calendar.MONTH, -1)    // 현재 달 -1
            setMonthView()
        }
        
        // 다음 달 버튼 이벤트
        binding.nextBtn.setOnClickListener {
            CalendarUtil.selectedDate.add(Calendar.MONTH, 1)     // 현재 달 +1
            setMonthView()
        }
    }

    // 날짜 화면에 보여주기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView() {
        // 년월 텍스트뷰 셋팅
         binding.monthYearText.text = monthYearFromDate(CalendarUtil.selectedDate)

        // 날짜 생성해서 리스트에 담기
        val dayList = dayInMonthArray()

        // 어댑터 초기화
        val adapter = CalendarAdapter(this, dayList)

        // 레이아웃 설정(열 7개)
        var manager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)

        // 레이아웃 적용
        binding.recyclerView.layoutManager = manager

        // 어댑터 적용
        binding.recyclerView.adapter = adapter
    }

    // 날짜 타입 설정(월, 년)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthYearFromDate(calendar: Calendar): String {
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH) + 1

        return "$month 월 $year"
    }

    // 날짜 생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayInMonthArray(): ArrayList<Date> {
        var dayList = ArrayList<Date>()

        var monthCalendar = CalendarUtil.selectedDate.clone() as Calendar

        // 1일로 셋팅
        monthCalendar[Calendar.DAY_OF_MONTH] = 1

        // 해당 달의 1일의 요일[1: 일요일, 2: 월요일... 7일: 토요일]
        val firstDayOfMonth = monthCalendar[Calendar.DAY_OF_WEEK] - 1

        // 요일 숫자만큼 이전 날짜로 설정
        // 예 : 6월 1일이 수요일이면 3만큼 이전 날짜 셋팅
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        while (dayList.size < 42) {
            dayList.add(monthCalendar.time)

            // 1일씩 늘린다. 1일 -> 2일 -> 3일
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dayList
    }
}