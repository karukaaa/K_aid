package com.example.myapplication.requestcreation

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.requestlist.Request

@Database(entities = [Request::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun requestDao(): RequestDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "request_db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
