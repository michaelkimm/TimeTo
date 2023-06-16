package com.tt.timeto.notification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.tt.timeto.MainActivity
import com.tt.timeto.R

class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        var titleText: TextView = findViewById(R.id.title_text)
        var contentText: TextView = findViewById(R.id.content_text)
        var timeText: TextView = findViewById(R.id.time_text)
        var alarmAgainBtn: Button = findViewById(R.id.alarm_again_btn)
        var alarmCancelBtn: Button = findViewById(R.id.stop_alarm_btn)

        // 알람 시간 받아오기
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val targetTime: String? = intent?.extras?.getString("time")

        // 알람 내용 보여주기
        // 예약 시간 보여주기
        titleText.setText("제목: " + title)
        contentText.setText("내용: " + content)
        timeText.setText(targetTime)
        
        // 다시 알람 버튼 클릭 상호작용 지정
        alarmAgainBtn.setOnClickListener {

        }
        
        // 알람 종료 버튼 클릭 상호작용 지정
        alarmCancelBtn.setOnClickListener {
            // 알람 종료

            // 시작 화면으로 전환
            var intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
}