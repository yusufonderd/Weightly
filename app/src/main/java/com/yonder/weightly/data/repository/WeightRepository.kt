package com.yonder.weightly.data.repository

import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.domain.mapper.WeightEntityMapper
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeightRepository @Inject constructor(
    private val dbDao: WeightDao
) {
    operator fun invoke() = dbDao.getDbAll().map { weightList ->
        weightList.map {
            WeightEntityMapper.map(it)
        }.reversed()
    }
}

