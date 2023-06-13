package com.tt.timeto.notification

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification WHERE notification_id = :notificationId")
    fun getNotification(notificationId: Long): Notification?

    @Query("SELECT * FROM notification WHERE to_do_id = :toDoId")
    fun getNotificationByToDo(toDoId: Long): List<Notification>

    @Insert
    fun insertNotification(notification: Notification): Long?

    @Delete
    fun deleteNotification(notification: Notification)

    @Update
    fun updateNotification(notification: Notification)
}
