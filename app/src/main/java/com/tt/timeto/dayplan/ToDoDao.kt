package com.tt.timeto.dayplan

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo WHERE reserved_date = :reservedDate")
    fun getToDoList(reservedDate: LocalDate): List<ToDo>

    @Query("SELECT * FROM todo WHERE to_do_id = :to_do_id")
    fun getToDo(to_do_id: Int): ToDo?

    @Insert
    fun insertToDo(toDo: ToDo): Long?

    @Delete
    fun deleteToDo(toDo: ToDo)

    @Update
    fun updateToDo(toDo: ToDo)
}