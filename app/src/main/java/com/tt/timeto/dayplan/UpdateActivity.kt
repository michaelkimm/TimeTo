package com.tt.timeto.dayplan

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.tt.timeto.AppDatabase
import com.tt.timeto.R
import com.tt.timeto.databinding.ActivityUpdateBinding
import com.tt.timeto.notification.AlertReceiver
import com.tt.timeto.notification.Notification
import com.tt.timeto.notification.TimePickerFragment
import com.tt.timeto.util.TimeUtil
import java.text.DateFormat
import java.time.LocalDate
import java.util.Calendar

class UpdateActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: ActivityUpdateBinding
    private lateinit var reservedDate: LocalDate
    private var reservedAlarm: Calendar? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding 초기화
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update)

        // 초기화
        var upTitleEdit: EditText = binding.upTitleEdit
        var upContentEdit: EditText = binding.upContentEdit
        var updateBtn: Button = binding.updateBtn
        var timeUpdateBtn: Button = binding.timeUpdateBtn
        var alarmCancelBtn: Button = binding.alarmCancelBtn

        // UpdateAdapter에서 넘어온 데이터 변수에 담기
        var uToDoId: Int = intent.getIntExtra("uId", 0)
        var uTitle: String? = intent.getStringExtra("uTitle")
        var uContent: String? = intent.getStringExtra("uContent")
        var uReservedDate: Long? = intent.getLongExtra("uReservedDate", 0)
        var uIsDone: Boolean? = intent.getBooleanExtra("uIsDone", false)

        // 화면의 날짜 설정
        reservedDate = LocalDate.ofEpochDay(uReservedDate!!)

        val notification: Notification? = AppDatabase.getDatabase(applicationContext)?.notificationDao()
            ?.getNotificationByToDo(uToDoId.toLong())

        // 화면에 값 적용
        upTitleEdit.setText(uTitle)
        upContentEdit.setText(uContent)
        if (notification != null && notification.reservedTime != null) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = TimeUtil.fromLocalDateTimeToEpochMilli(notification.reservedTime)
            updateTimeText(calendar)
        }

        // 수정 버튼 이벤트
        updateBtn.setOnClickListener { 
            // 입력 값 변수에 담기
            var iTitle = upTitleEdit.text.toString()
            var iContent = upContentEdit.text.toString()

            // 사용자 클래스 생성
            var toDo: ToDo = ToDo(uToDoId, iTitle, iContent, LocalDate.ofEpochDay(uReservedDate!!.toLong()), uIsDone)

            // DB 생성
            var db: AppDatabase? = AppDatabase.getDatabase(applicationContext)

            // 데이터 수정
            db?.toDoDao()?.updateToDo(toDo)

            // 등록된 알람 취소
            cancelAlarmByManager(notification)

            // 알람 등록
            val newNotificationId: Long? = updateOrInsertAlarm(reservedAlarm, notification?.notification_id, uToDoId.toLong())

            // 알람 설정
            startAlarm(uToDoId.toLong(), newNotificationId)

            // 메인 화면으로 이동
            var intent: Intent = Intent(applicationContext, DayPlanActivity::class.java)
            startActivity(intent)

            // 액티비티 종료
            finish()
        }

        // ToDo 알람 시간 정보
        // 알람 설정
        timeUpdateBtn.setOnClickListener {

            var timePicker = TimePickerFragment()
            // 시계 호출
            timePicker.show(supportFragmentManager, "Time picker")
        }


        // 알람 취소
        alarmCancelBtn.setOnClickListener {
            // 알람 취소 함수
            var notification: Notification? = AppDatabase.getDatabase(applicationContext)?.notificationDao()?.getNotificationByToDo(uToDoId.toLong())
            cancelAlarmWithUIChange(notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateOrInsertAlarm(reservedAlarm: Calendar?, notificationId: Long?, toDoRowId: Long?, ): Long? {
        if (reservedAlarm == null) {
            return null
        }
        // 알람 수정
        var notification: Notification = Notification(notificationId, TimeUtil.fromCalendarToLocalDateTime(reservedAlarm!!), toDoRowId)
        var db: AppDatabase? = AppDatabase.getDatabase(applicationContext)

        if (notificationId == null) {
            return db?.notificationDao()?.insertNotification(notification)
        } else {
            db?.notificationDao()?.updateNotification(notification)
            return notificationId
        }
    }

    private fun cancelAlarmByManager(notification: Notification?) {
        if (notification == null)
            return

        // 알람매니저 선언
        var alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var intent = Intent(this, AlertReceiver::class.java)

        var pendingIntent = PendingIntent.getBroadcast(
            this,
            notification.notification_id!!.toInt(),
            intent,
            0 or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun cancelAlarmWithUIChange(notification: Notification?) {
        // 알람 시간 텍스트 업데이트
        binding.timeUpdateText.text = ""

        if (notification == null) {
            Toast.makeText(applicationContext, "등록된 알람 없음", Toast.LENGTH_SHORT).show()
            return
        }
        cancelAlarmByManager(notification)

        Toast.makeText(applicationContext, "알람 취소 완료", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
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
        binding.timeUpdateText.text = ""
        binding.timeUpdateText.append("알람 시간: ")
        binding.timeUpdateText.append(curTime)
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

        var c: Calendar = Calendar.getInstance()
        c.timeInMillis = TimeUtil.fromLocalDateTimeToEpochMilli(notification.reservedTime)

        // 알람매니저 선언
        var alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var intent = Intent(this, AlertReceiver::class.java)

        intent.putExtra("title", toDo.title)
        intent.putExtra("content", toDo.content)
        intent.putExtra("reservedTimeInMillis", TimeUtil.fromLocalDateTimeToEpochMilli(notification.reservedTime))

        var pendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId.toInt(),
            intent,
            0 or PendingIntent.FLAG_MUTABLE
        )

        // 설정 시간이 현재 시간 이전이면 +1일
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }
}