package com.augustin26.tft

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SummonersDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(summoner: Summoner)

    @Update
    suspend fun update(summoner: Summoner)

    @Delete
    suspend fun delete(summoner: Summoner)

    @Query("Select * from summonersTable order by name ASC")
    fun getAllSummoners(): LiveData<List<Summoner>>

}