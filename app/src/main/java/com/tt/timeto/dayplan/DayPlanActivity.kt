package com.tt.timeto.dayplan

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tt.timeto.AppDatabase
import com.tt.timeto.R
import com.tt.timeto.notification.Notification
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.datetime.LocalDate

class DayPlanActivity : AppCompatActivity() {

    private lateinit var adapter: ToDoAdapter
    private lateinit var reservedDate: LocalDate
    private var toDoList: ArrayList<ToDo> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_plan)
        // 변수 초기화
        var reservedYear: Int? = intent.getIntExtra("year", 0)
        var reservedMonth: Int? = intent.getIntExtra("month", 0)
        var reservedDay: Int? = intent.getIntExtra("day", 0)
        reservedDate = LocalDate(reservedYear!!, reservedMonth!!, reservedDay!!)

        // 버튼 이벤트 등록
        val insertBtn: FloatingActionButton = findViewById(R.id.insert_btn)
        insertBtn.setOnClickListener {
            val intent: Intent = Intent(this, InsertActivity::class.java)
            intent.putExtra("reservedDate", reservedDate.toEpochDays())
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

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var position: Int = viewHolder.bindingAdapterPosition

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        var uId: Int? = toDoList.get(position).toDoId
                        var uTitle: String? = toDoList.get(position).title
                        var uContent: String? = toDoList.get(position).content
                        var uReservedDate: LocalDate? = toDoList.get(position).reservedDate

                        var toDo: ToDo = ToDo(uId, uTitle, uContent, uReservedDate)

                        // 아이템 삭제
                        adapter.deleteUser(position)

                        // 아이템 삭제 화면 재정리
                        adapter.notifyItemRemoved(position)
                        
                        // DB 생성
                        var db: AppDatabase? = AppDatabase.getDatabase(applicationContext)
                        
                        // 삭제 쿼리
                        db?.toDoDao()?.deleteToDo(toDo)

                        // Notification 삭제

                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                // 스와이프 기능
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(Color.RED)
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeLeftLabel("삭제")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(recyclerView)   // recyclerView에 스와이프 적용
    }

    // 액티비티가 백그라운드에 있는데 호출되면 실행
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // 사용자 조회
        loadUserList()
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {   // 돌아온 값이 OK라면
            // 사용자 조회
            loadUserList()
        }
    }

    private fun loadUserList() {
        val db: AppDatabase? = AppDatabase.getDatabase(applicationContext)

        toDoList = db?.toDoDao()!!.getToDoList(reservedDate) as ArrayList<ToDo>

        if (toDoList.isNotEmpty()) {
            // 데이터 적용
            adapter.setToDoList(toDoList)
        }
    }
}