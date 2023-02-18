package com.udacity.asteroidradar

import com.udacity.asteroidradar.api.AsteroidService
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.database.DatabaseAsteroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

fun getTodayDate(): String{
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun getDateAfterSevenDays(): String{
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 7)
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun ArrayList<Asteroid>.asDomainModel(): Array<DatabaseAsteroid> {
    return map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
        .toTypedArray()
}

suspend fun getPictureOfDay(): PictureOfDay?{
    var pictureOfDay: PictureOfDay
    withContext(Dispatchers.IO){
        pictureOfDay = Network.asteroids.getPictureOfDay().await()
    }
    if (pictureOfDay.mediaType == "image"){
        return pictureOfDay
    }else
        return null
}