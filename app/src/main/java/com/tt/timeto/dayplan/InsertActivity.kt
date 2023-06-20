package com.tt.timeto.dayplan

import android.app.Activity
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.tt.timeto.AppDatabase
import com.tt.timeto.R
import com.tt.timeto.databinding.ActivityInsertBinding
import com.tt.timeto.notification.TimePickerFragment
import com.tt.timeto.util.AlarmUtil
import java.text.DateFormat
import java.time.LocalDate
import java.util.Calendar

class InsertActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: ActivityInsertBinding
    private lateinit var reservedDate: LocalDate
    private var reservedAlarm: Calendar? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding 초기화
        binding = DataBindingUtil.setContentView(this, R.layout.activity_insert)

        // ToDo 텍스트 정보
        val titleEdit: EditText = binding.titleEdit
        val contentEdit: EditText = binding.contentEdit
        val saveBtn: Button = binding.saveBtn

        reservedDate = LocalDate.ofEpochDay(intent.getLongExtra("reservedDateEpochDay", 0))

        saveBtn.setOnClickListener {
            val sTitle = titleEdit.text.toString()
            val sContent = contentEdit.text.toString()

            // ToDo 등록
            val toDoRowId: Long? = insertToDo(sTitle, sContent)

            if (reservedAlarm != null) {
                // 알람 등록
                val nRowId: Long? = AlarmUtil.updateOrInsertAlarm(this, reservedAlarm, null, toDoRowId)
                // 알람 설정
                AlarmUtil.startAlarm(toDoRowId, nRowId, getSystemService(Context.ALARM_SERVICE) as AlarmManager, this)
            }

            // 상태 값을 돌려준다
            setResult(Activity.RESULT_OK)

            // 액티비티 닫기
            finish()
        }

        // ToDo 알람 시간 정보
        // 알람 설정
        binding.timeBtn.setOnClickListener {

            var timePicker = TimePickerFragment()
            // 시계 호출
            timePicker.show(supportFragmentManager, "Time picker")
        }
    }

    private fun insertToDo(title: String, content: String): Long? {

        // ToDo 저장
        val toDo = ToDo(null, title, content, reservedDate, false)
        var db: AppDatabase? = AppDatabase.getDatabase(applicationContext)

        return db?.toDoDao()?.insertToDo(toDo)
    }

    // 시간 정하면 호출되는 함수
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTimeSet(timePicker: TimePicker?, hourOfDay: Int, minute: Int) {

        var c = Calendar.getInstance()

        // 시간 설정
        c.set(Calendar.DAY_OF_MONTH, reservedDate.dayOfMonth)
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        // 화면에 시간 설정
        updateTimeText(c)

        reservedAlarm = c
    }

    private fun updateTimeText(c: Calendar) {

        var curTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.time)
        binding.timeText.text = ""
        binding.timeText.append("알람 시간: ")
        binding.timeText.append(curTime)
    }
}
