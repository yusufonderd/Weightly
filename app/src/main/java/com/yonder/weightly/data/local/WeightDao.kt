package com.yonder.weightly.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface WeightDao {

    @Query("SELECT * FROM weight WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<WeightEntity>

    @Query("SELECT * FROM weight WHERE  timestamp BETWEEN :startDate AND :endDate")
    fun fetchBy(startDate: Date, endDate: Date): List<WeightEntity>

    @Insert
    fun insertAll(vararg users: WeightEntity)

    @Insert
    suspend fun save(weight: WeightEntity)

    @Update
    suspend fun update(weight: WeightEntity)

    @Delete
    fun delete(user: WeightEntity)

    @Query("SELECT * FROM weight ORDER BY timestamp DESC")
    fun getDbAll(): Flow<List<WeightEntity>>

    @Query("SELECT AVG(value) as average FROM weight where timestamp BETWEEN :startDay AND :endDay ORDER BY timestamp ASC")
    fun getAverageByDateRange(
        startDay: Date,
        endDay: Date
    ): Float?

    @Query("SELECT AVG(value) as average FROM weight ORDER BY timestamp ASC")
    fun getAverage(): Float?

    @Query("SELECT MAX(value) as average FROM weight ORDER BY timestamp ASC")
    fun getMax(): Float?

    @Query("SELECT MIN(value) as average FROM weight ORDER BY timestamp ASC")
    fun getMin(): Float?
}
