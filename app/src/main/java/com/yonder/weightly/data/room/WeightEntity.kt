package com.yonder.weightly.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "weight")
data class WeightEntity(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Date?,
    @ColumnInfo(name = "value") val value: Float?,
    @ColumnInfo(name = "emoji") val emoji: String?,
    @ColumnInfo(name = "note") val note: String?,
)
