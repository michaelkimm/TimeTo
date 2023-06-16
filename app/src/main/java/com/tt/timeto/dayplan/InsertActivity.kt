package com.tt.timeto.dayplan

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
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
import com.tt.timeto.notification.AlertReceiver
import com.tt.timeto.notification.Notification
import com.tt.timeto.notification.TimePickerFragment
import com.tt.timeto.util.TimeUtil
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
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
                val nRowId: Long? = insertAlarm(toDoRowId)
                // 알람 설정
                startAlarm(toDoRowId, nRowId)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertAlarm(toDoRowId: Long?): Long? {
        // 알람 저장
        val localDate: LocalDateTime? = TimeUtil.fromCalendarToLocalDateTime(reservedAlarm!!)

        var notification: Notification = Notification(null, localDate, toDoRowId)
        var db: AppDatabase? = AppDatabase.getDatabase(applicationContext)

        return db?.notificationDao()?.insertNotification(notification)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startAlarm(toDoRowId: Long?, notificationId: Long?) {

        if (notificationId == null)
            return

        // 알림 찾기
        var db: AppDatabase? = AppDatabase.getDatabase(applicationContext)
        val notification: Notification? = db?.notificationDao()?.getNotification(notificationId!!)
        if (notification == null || notification.reservedTime == null)
            return

        // 투두 찾기
        val toDo: ToDo? = db?.toDoDao()?.getToDo(toDoRowId!!.toInt())
        if (toDo == null)
            return

        val reservedTimeInMillis = TimeUtil.fromLocalDateTimeToEpochMilli(notification.reservedTime)

        // 알람매니저 선언
        var alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var intent = Intent(this, AlertReceiver::class.java)

        // store data
        intent.putExtra("title", toDo.title)
        intent.putExtra("content", toDo.content)
        intent.putExtra("reservedTimeInMillis", reservedTimeInMillis)

        var pendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId.toInt(),
            intent,
            0 or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reservedTimeInMillis, pendingIntent)
    }
}
