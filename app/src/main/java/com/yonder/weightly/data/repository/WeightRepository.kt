package com.yonder.weightly.data.repository

import com.yonder.weightly.data.local.AppDatabase
import com.yonder.weightly.domain.mapper.WeightEntityMapper
import javax.inject.Inject

class WeightRepository @Inject constructor(private val appDatabase: AppDatabase) {
    operator fun invoke()= appDatabase.weightDao().getAll().map {
        WeightEntityMapper.map(it)
    }
}

