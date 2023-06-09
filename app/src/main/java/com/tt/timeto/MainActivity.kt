package com.tt.timeto

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.timeto.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), OnItemListener {
    
    private lateinit var binding: ActivityMainBinding
    
    // 년월 변수
    private lateinit var selectedDate: LocalDate 
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // binding 초기화
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        
        // 현재 날짜
        selectedDate = LocalDate.now()
        
        // 화면 설정
        setMonthView()
        
        // 이전 달 버튼 이벤트
        binding.preBtn.setOnClickListener {
            // 현재 월 -1 변수에 담기
            selectedDate = selectedDate.minusMonths(1)
            setMonthView()
        }
        
        // 다음 달 버튼 이벤트
        binding.nextBtn.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            setMonthView()
        }
    }

    // 날짜 화면에 보여주기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView() {
        // 년월 텍스트뷰 셋팅
         binding.monthYearText.text = monthYearFromDate(selectedDate)

        // 날짜 생성해서 리스트에 담기
        val dayList = dayInMonthArray(selectedDate)

        // 어댑터 초기화
        val adapter = CalendarAdapter(dayList, this)

        // 레이아웃 설정(열 7개)
        var manager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)

        // 레이아웃 적용
        binding.recyclerView.layoutManager = manager

        // 어댑터 적용
        binding.recyclerView.adapter = adapter
    }

    // 날짜 타입 설정(월, 년)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthYearFromDate(date: LocalDate): String {
        var formatter = DateTimeFormatter.ofPattern("MM월 yyyy")
        
        // 받아온 날짜를 해당 포맷으로 변경
        return date.format(formatter)
    }

    // 날짜 타입(년, 월)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun yearMonthFromDate(date: LocalDate): String {
        var formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")

        // 받아온 날짜를 해당 포맷으로 변경
        return date.format(formatter)
    }

    // 날짜 생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayInMonthArray(date: LocalDate): ArrayList<String> {
        var dayList = ArrayList<String>()

        var yearMonth = YearMonth.from(date)

        // 해당 월 마지막 날짜 가져오기(예: 28, 30, 31)
        var lastDay = yearMonth.lengthOfMonth()

        // 해당 월의 첫 번째 날 가져오기(예: 4월 1일)
        var firstDay = date.withDayOfMonth(1)

        // 첫번째 날 요일 가져오기(월:1, 일:7)
        var dayOfWeek = firstDay.dayOfWeek.value

        for (i in 1..42) {
            if (i <= dayOfWeek || i > (lastDay + dayOfWeek)) {
                dayList.add("")
            } else {
                dayList.add((i - dayOfWeek).toString())
            }
        }

        return dayList
    }


    // 아이템 클릭 이벤트
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(dayText: String) {
        var yearMonth = yearMonthFromDate(selectedDate) + " " + dayText + "일"
        Toast.makeText(this, yearMonth, Toast.LENGTH_SHORT).show()
    }

}