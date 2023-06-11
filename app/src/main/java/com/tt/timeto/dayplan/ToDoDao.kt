package com.tt.timeto.dayplan

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo")
    fun getAllToDo(): List<ToDo>

    @Insert
    fun insertToDo(toDo: ToDo)

    @Delete
    fun deleteToDo(toDo: ToDo)

    @Update
    fun updateToDo(toDo: ToDo)
}