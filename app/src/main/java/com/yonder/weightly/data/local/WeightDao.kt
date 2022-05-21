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
}
