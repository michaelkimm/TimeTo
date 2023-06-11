package com.tt.timeto

import android.content.Context
import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.tt.timeto.dayplan.ToDo
import com.tt.timeto.dayplan.ToDoDao
import java.util.concurrent.Executors

@Database(entities = [ToDo::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateConverters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun toDoDao(): ToDoDao
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