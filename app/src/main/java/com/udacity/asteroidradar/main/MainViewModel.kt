package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel (application: Application) : AndroidViewModel(application) {
    private val database = AsteroidDatabase.getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private var _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private var _navigateToDetailFragment = MutableLiveData<Asteroid>()
    val navigateToDetailFragment: LiveData<Asteroid>
        get() = _navigateToDetailFragment

    init {
        getAllAstroidsFromDatabase()
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            loadPictureOfDay()
        }
    }

    fun getAllAstroidsFromDatabase(){
        viewModelScope.launch {
            database.asteroidDao.getAllAsteroids().collect {
                _asteroids.value = it
            }
        }
    }

    fun getTodayAsteroidsFromDatabase(){
        viewModelScope.launch {
            database.asteroidDao.getTodayAsteroids(today = getTodayDate()).collect {
                _asteroids.value = it
            }
        }
    }

    fun getWeekAsteroidsFromDatabase(){
        viewModelScope.launch {
            database.asteroidDao.getWeekAsteroids(startDate = getTodayDate(), endDate = getDateAfterSevenDays()).collect {
                _asteroids.value = it
            }
        }
    }

    private suspend fun loadPictureOfDay(){
        _pictureOfDay.value = getPictureOfDay()
    }

    fun onClick(asteroid: Asteroid){
        _navigateToDetailFragment.value = asteroid
    }

    fun doneNavigating(){
        _navigateToDetailFragment.value = null
    }
}