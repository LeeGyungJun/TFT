package com.augustin26.tft

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SummonerViewModal(application: Application) : AndroidViewModel(application) {
    val allSummoners: LiveData<List<Summoner>>
    val repository: SummonerRepository

    init {
        val dao = SummonerDatabase.getDatabase(application).getNotesDao()
        repository = SummonerRepository(dao)
        allSummoners = repository.allSummoners
    }

    fun deleteSummoner(summoner: Summoner) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(summoner)
    }

    fun updateSummoner(summoner: Summoner) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(summoner)
    }

    fun addSummoner(summoner: Summoner) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(summoner)
    }
}