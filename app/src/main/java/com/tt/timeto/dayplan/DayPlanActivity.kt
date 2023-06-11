package com.tt.timeto.dayplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tt.timeto.AppDatabase
import com.tt.timeto.R

class DayPlanActivity : AppCompatActivity() {

    private lateinit var adapter: ToDoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_plan)

        val insertBtn: FloatingActionButton = findViewById(R.id.insert_btn)
        insertBtn.setOnClickListener {
            val intent: Intent = Intent(this, InsertActivity::class.java)

            activityResult.launch(intent)
        }

        // RecyclerView 설정
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // UserAdapter 초기화
        adapter = ToDoAdapter(this)

        // Adapter 적용
        recyclerView.adapter = adapter

        // 사용자 조회
        loadUserList()
    }

    // 액티비티가 백그라운드에 있는데 호출되면 실행
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // 사용자 조회
        loadUserList()
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            // 돌아온 값이 OK라면

            // 사용자 조회
            loadUserList()
        }
    }

    private fun loadUserList() {
        val db: AppDatabase? = AppDatabase.getDatabase(applicationContext)

        val toDoList: ArrayList<ToDo> = db?.toDoDao()!!.getAllToDo() as ArrayList<ToDo>

        if (toDoList.isNotEmpty()) {
            // 데이터 적용
            adapter.setToDoList(toDoList)
        }
    }
}