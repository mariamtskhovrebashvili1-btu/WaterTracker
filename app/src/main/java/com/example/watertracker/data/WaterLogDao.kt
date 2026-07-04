package com.example.watertracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterLogDao {

    @Insert
    suspend fun insert(log: WaterLog)

    @Delete
    suspend fun delete(log: WaterLog)

    @Query("SELECT * FROM water_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getLogsByDate(date: String): Flow<List<WaterLog>>

    @Query("SELECT date, SUM(amount) as total FROM water_logs GROUP BY date ORDER BY date DESC")
    fun getAllGroupedByDate(): Flow<List<DailyTotal>>

    @Query("SELECT date, SUM(amount) as total FROM water_logs WHERE date >= :sinceDate GROUP BY date ORDER BY date ASC")
    fun getDailyTotalsSince(sinceDate: String): Flow<List<DailyTotal>>
}
