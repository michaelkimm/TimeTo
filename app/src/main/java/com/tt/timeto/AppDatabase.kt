package com.tt.timeto

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tt.timeto.dayplan.ToDo
import com.tt.timeto.dayplan.ToDoDao
import com.tt.timeto.notification.Notification
import com.tt.timeto.notification.NotificationConverters
import com.tt.timeto.notification.NotificationDao
import com.tt.timeto.util.LocalDateConverters
import java.util.concurrent.Executors

@Database(entities = [ToDo::class, Notification::class], version = 1, exportSchema = false)
@TypeConverters(
    value = [
        LocalDateConverters::class,
        NotificationConverters::class
    ]
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun toDoDao(): ToDoDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database")
                    .allowMainThreadQueries()
                    .setQueryCallback(RoomDatabase.QueryCallback { sqlQuery, bindArgs ->
                        println("SQL Query: $sqlQuery SQL Args: $bindArgs")
                    }, Executors.newSingleThreadExecutor())
                    .build()
            }
            return INSTANCE;
        }
    }
}