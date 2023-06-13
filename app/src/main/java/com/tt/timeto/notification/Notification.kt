package com.tt.timeto.notification

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.tt.timeto.dayplan.ToDo
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "notification",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = ToDo::class,
            parentColumns = arrayOf("to_do_id"),
            childColumns = arrayOf("to_do_id"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class Notification (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notification_id")
    val notification_id: Long?,

    @ColumnInfo(name = "reserved_time")
    val reservedTime: Long,

    @ColumnInfo(name = "to_do_id")
    val toDoId: Long?

//    @ColumnInfo(name = "type")
//    val type: Type?
) : Parcelable

enum class Type {
    PUSH,
    VIBRATION,
    SOUND
}