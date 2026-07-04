package com.example.watertracker.data

import com.example.watertracker.util.todayDateString
import kotlinx.coroutines.flow.Flow

class WaterRepository(private val dao: WaterLogDao) {

    fun getLogsByDate(date: String): Flow<List<WaterLog>> = dao.getLogsByDate(date)

    fun getTodayLogs(): Flow<List<WaterLog>> = dao.getLogsByDate(todayDateString())

    fun getAllGroupedByDate(): Flow<List<DailyTotal>> = dao.getAllGroupedByDate()

    fun getDailyTotalsSince(sinceDate: String): Flow<List<DailyTotal>> = dao.getDailyTotalsSince(sinceDate)

    suspend fun addWater(amountMl: Int) {
        val now = System.currentTimeMillis()
        dao.insert(WaterLog(amount = amountMl, timestamp = now, date = todayDateString()))
    }

    suspend fun deleteLog(log: WaterLog) {
        dao.delete(log)
    }
}
