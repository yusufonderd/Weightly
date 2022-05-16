package com.yonder.weightly.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface WeightDao {

    @Query("SELECT * FROM weight WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<WeightEntity>

    @Query("SELECT * FROM weight WHERE timestamp = :targetDate")
    fun findWeightByOnDate(targetDate: Date): List<WeightEntity>

    @Insert
    fun insertAll(vararg users: WeightEntity)

    @Insert
    suspend fun insert(weight: WeightEntity)

    @Delete
    fun delete(user: WeightEntity)

    @Query("SELECT * FROM weight ORDER BY timestamp DESC")
    fun getDbAll(): Flow<List<WeightEntity>>
}
