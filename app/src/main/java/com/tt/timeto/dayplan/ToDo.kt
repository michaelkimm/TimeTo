package com.tt.timeto.dayplan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.tt.timeto.notification.Notification
import kotlinx.datetime.LocalDate

@Entity(tableName = "todo")
data class ToDo(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "to_do_id")
    val toDoId: Int?,

    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "content")
    val content: String?,

    @ColumnInfo(name = "reserved_date")
    val reservedDate: LocalDate?,
)
