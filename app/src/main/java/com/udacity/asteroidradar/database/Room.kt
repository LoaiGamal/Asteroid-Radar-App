package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.*
import com.udacity.asteroidradar.Asteroid
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao{
    @Query("SELECT * FROM databaseasteroid ORDER BY closeApproachDate DESC")
    fun getAllAsteroids(): Flow<List<Asteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate = :today ORDER BY closeApproachDate DESC")
    fun getTodayAsteroids(today: String): Flow<List<Asteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate >= :startDate AND closeApproachDate <= :endDate ORDER BY closeApproachDate DESC")
    fun getWeekAsteroids(startDate: String, endDate: String): Flow<List<Asteroid>>

    @Query("DELETE FROM databaseasteroid WHERE closeApproachDate < :today")
    fun deleteOldAsteroids(today: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao

    companion object {
        @Volatile
        private lateinit var INSTANCE: AsteroidDatabase

        fun getDatabase(context: Context): AsteroidDatabase {
            synchronized(AsteroidDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidDatabase::class.java,
                        "asteroid"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}