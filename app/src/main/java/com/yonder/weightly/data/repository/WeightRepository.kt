package com.yonder.weightly.data.repository

import com.yonder.weightly.data.local.AppDatabase
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.domain.mapper.WeightEntityMapper
import com.yonder.weightly.domain.uimodel.WeightUIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeightRepository @Inject constructor(
    private val dbDao: WeightDao
) {
    operator fun invoke(): Flow<List<WeightUIModel>> = dbDao.getDbAll().map { weightList ->
        weightList.map {
            WeightEntityMapper.map(it)
        }
    }
}

