package com.tt.timeto.dayplan

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.tt.timeto.AppDatabase
import com.tt.timeto.R
import com.tt.timeto.databinding.ActivityInsertBinding
import com.tt.timeto.notification.AlertReceiver
import com.tt.timeto.notification.TimePickerFragment
import kotlinx.datetime.LocalDate
import java.text.DateFormat
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

        reservedDate = LocalDate.fromEpochDays(intent.getIntExtra("reservedDate", 0))

        saveBtn.setOnClickListener {
            val sTitle = titleEdit.text.toString()
            val sContent = contentEdit.text.toString()

            // 사용자 등록
            insertToDo(sTitle, sContent)

            Log.d("kkang", "==============================")

            // 알람 설정
            startAlarm(reservedAlarm)
        }

        // ToDo 알람 시간 정보
        // 알람 설정
        binding.timeBtn.setOnClickListener {

            var timePicker = TimePickerFragment()
            // 시계 호출
            timePicker.show(supportFragmentManager, "Time picker")
        }

//        // 알람 취소
//        binding.alarmCancelBtn.setOnClickListener {
//            // 알람 취소 함수
//            cancelAlarm()
//        }
    }

    private fun insertToDo(title: String, content: String) {
        val toDo = ToDo(null, title, content, reservedDate)
        var db: AppDatabase? = AppDatabase.getDatabase(applicationContext)

        db?.toDoDao()?.insertToDo(toDo)

        // 상태 값을 돌려준다
        setResult(Activity.RESULT_OK)

        // 액티비티 닫기
        finish()
    }

    // 시간 정하면 호출되는 함수
    override fun onTimeSet(timePicker: TimePicker?, hourOfDay: Int, minute: Int) {

        var c = Calendar.getInstance()

        // 시간 설정
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        // 화면에 시간 설정
        updateTimeText(c)

        reservedAlarm = c
    }

    private fun updateTimeText(c: Calendar) {

        var curTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.time)

        binding.timeText.append("알람 시간: ")
        binding.timeText.append(curTime)
    }

    private fun startAlarm(c: Calendar?) {

        if (c == null)
            return

        // 알람매니저 선언
        var alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var intent = Intent(this, AlertReceiver::class.java)

        // store data
        var curTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.time)
        intent.putExtra("time", curTime)
        Log.d("kkang", curTime)

        var pendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            intent,
            0 or PendingIntent.FLAG_IMMUTABLE
        )

        // 설정 시간이 현재 시간 이전이면 +1일
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }

//    private fun cancelAlarm() {
//        // 알람매니저 선언
//        var alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        var intent = Intent(this, AlertReceiver::class.java)
//
//        var pendingIntent = PendingIntent.getBroadcast(
//            this,
//            1,
//            intent,
//            0 or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        alarmManager.cancel(pendingIntent)
//        binding.timeText.text = "알람 취소"
//    }

}
