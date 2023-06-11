package com.tt.timeto.dayplan

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.tt.timeto.AppDatabase
import com.tt.timeto.R

class InsertActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)

        val titleEdit: EditText = findViewById(R.id.title_edit)
        val contentEdit: EditText = findViewById(R.id.content_edit)
        val saveBtn: Button = findViewById(R.id.save_btn)

        saveBtn.setOnClickListener {
            val sTitle = titleEdit.text.toString()
            val sContent = contentEdit.text.toString()

            // 사용자 등록
            insertUser(sTitle, sContent)
        }

    }

    private fun insertUser(title: String, content: String) {
        val toDo = ToDo(null, title, content)
        var db: AppDatabase? = AppDatabase.getDatabase(applicationContext)

        db?.toDoDao()?.insertToDo(toDo)

        // 상태 값을 돌려준다
        setResult(Activity.RESULT_OK)

        // 액티비티 닫기
        finish()
    }
}
