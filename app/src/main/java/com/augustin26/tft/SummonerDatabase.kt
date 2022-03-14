package com.augustin26.tft

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Summoner::class], version = 1, exportSchema = false)
abstract class SummonerDatabase : RoomDatabase() {

    abstract fun getNotesDao(): SummonersDao

    companion object {
        // 싱글톤으로 데이터가 여러 번 열리는 것을 방지한다.
        @Volatile
        private var INSTANCE: SummonerDatabase? = null

        fun getDatabase(context: Context): SummonerDatabase {
            // INSTANCE 가 null 이 아니면 반환하고 null 이라면 데이터베이스 생성
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SummonerDatabase::class.java,
                    "note_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}