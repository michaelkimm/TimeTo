package com.tt.timeto.dayplan

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.tt.timeto.AppDatabase
import com.tt.timeto.R
import kotlinx.datetime.LocalDate

class UpdateActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        // 초기화
        var upTitleEdit: EditText = findViewById(R.id.up_title_edit)
        var upContentEdit: EditText = findViewById(R.id.up_content_edit)
        var updateBtn: Button = findViewById(R.id.update_btn)

        // UpdateAdapter에서 넘어온 데이터 변수에 담기
        var uId: Int = intent.getIntExtra("uId", 0)
        var uTitle: String? = intent.getStringExtra("uTitle") 
        var uContent: String? = intent.getStringExtra("uContent")
        var uReservedDate: Int? = intent.getIntExtra("uReservedDate", 0)
        
        // 화면에 값 적용
        upTitleEdit.setText(uTitle)
        upContentEdit.setText(uContent)
        
        // 수정 버튼 이벤트
        updateBtn.setOnClickListener { 
            // 입력 값 변수에 담기
            var iTitle = upTitleEdit.text.toString()
            var iContent = upContentEdit.text.toString()

            // 사용자 클래스 생성
            var toDo: ToDo = ToDo(uId, iTitle, iContent, LocalDate.fromEpochDays(uReservedDate!!))

            // DB 생성
            var db: AppDatabase? = AppDatabase.getDatabase(applicationContext)

            // 데이터 수정
            db?.toDoDao()?.updateToDo(toDo)

            // 메인 화면으로 이동
            var intent: Intent = Intent(applicationContext, DayPlanActivity::class.java)
            startActivity(intent)

            // 액티비티 종료
            finish()
        }
    }
}