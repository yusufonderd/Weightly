package com.yonder.weightly.data.repository

import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.data.local.WeightEntity
import com.yonder.weightly.domain.mapper.WeightEntityMapper
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeightRepository @Inject constructor(
    private val dbDao: WeightDao
) {
    operator fun invoke() = dbDao.getDbAll().map { weightList ->
        weightList.mapIndexed { index, weightEntity ->
            var previousEntity: WeightEntity? = null
            val previousIndex = index + 1
            if (previousIndex < weightList.size && previousIndex >= 0){
                previousEntity = weightList[previousIndex]
            }
            WeightEntityMapper.map(entity = weightEntity, previousEntity = previousEntity)
        }.reversed()
    }
}

