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
import com.tt.timeto.util.AlarmUtil
import com.tt.timeto.util.AlarmUtil.Companion.cancelAlarmByManager
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
            AlarmUtil.cancelAlarmByManager(this, getSystemService(Context.ALARM_SERVICE) as AlarmManager, notification)

            // 알람 등록
            val newNotificationId: Long? = AlarmUtil.updateOrInsertAlarm(this, reservedAlarm, notification?.notification_id, uToDoId.toLong())

            // 알람 설정
            AlarmUtil.startAlarm(uToDoId.toLong(), newNotificationId, getSystemService(Context.ALARM_SERVICE) as AlarmManager, this)

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
            AlarmUtil.cancelAlarmByManager(this, getSystemService(Context.ALARM_SERVICE) as AlarmManager, notification)
            cancelAlarmUIChange(notification)
            if (notification != null) {
                AppDatabase.getDatabase(applicationContext)?.notificationDao()?.deleteNotification(notification)
            }
        }
    }

    private fun cancelAlarmUIChange(notification: Notification?) {
        // 알람 시간 텍스트 업데이트
        binding.timeUpdateText.text = ""

        if (notification == null) {
            Toast.makeText(applicationContext, "등록된 알람 없음", Toast.LENGTH_SHORT).show()
            return
        }

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
}