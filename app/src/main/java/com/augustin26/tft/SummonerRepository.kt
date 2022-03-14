package com.augustin26.tft

import androidx.lifecycle.LiveData

class SummonerRepository(private val summonersDao: SummonersDao) {

    val allSummoners: LiveData<List<Summoner>> = summonersDao.getAllSummoners()

    suspend fun insert(summoner: Summoner) {
        summonersDao.insert(summoner)
    }

    suspend fun delete(summoner: Summoner){
        summonersDao.delete(summoner)
    }

    suspend fun update(summoner: Summoner){
        summonersDao.update(summoner)
    }
}