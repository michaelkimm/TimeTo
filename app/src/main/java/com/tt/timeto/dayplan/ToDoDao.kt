package com.tt.timeto.dayplan

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.datetime.LocalDate

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo WHERE reserved_date = :reservedDate")
    fun getToDoList(reservedDate: LocalDate): List<ToDo>

    @Insert
    fun insertToDo(toDo: ToDo)

    @Delete
    fun deleteToDo(toDo: ToDo)

    @Update
    fun updateToDo(toDo: ToDo)
}