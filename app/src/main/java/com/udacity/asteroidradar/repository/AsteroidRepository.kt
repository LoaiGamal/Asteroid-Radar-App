package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.asDomainModel
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.getDateAfterSevenDays
import com.udacity.asteroidradar.getTodayDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.ArrayList

class AsteroidRepository(private val database: AsteroidDatabase) {
    suspend fun refreshAsteroids(
        startDate: String = getTodayDate(),
        endDate: String = getDateAfterSevenDays()
    ) {
        var asteroidList: ArrayList<Asteroid>
        withContext(Dispatchers.IO) {
            val asteroidResponseBody: ResponseBody = Network.asteroids.getAsteroids(
                startDate, endDate,
            )
                .await()
            asteroidList = parseAsteroidsJsonResult(JSONObject(asteroidResponseBody.string()))
            database.asteroidDao.insertAll(*asteroidList.asDomainModel())
        }
    }

    suspend fun deleteOldAsteroids(){
        withContext(Dispatchers.IO){
            database.asteroidDao.deleteOldAsteroids(getTodayDate())
        }
    }
}