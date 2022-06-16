package com.yonder.weightly.data.repository

import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.data.local.WeightEntity
import com.yonder.weightly.domain.mapper.WeightEntityMapper
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeightRepository @Inject constructor(
    private val dbDao: WeightDao,
    private val mapper: WeightEntityMapper
) {
    operator fun invoke() = dbDao.getDbAll().map { weightList ->
        weightList.mapIndexed { index, weightEntity ->
            var previousEntity: WeightEntity? = null
            val previousIndex = index + 1
            if (previousIndex < weightList.size && previousIndex >= 0){
                previousEntity = weightList[previousIndex]
            }
            mapper.map(entity = weightEntity, previousEntity = previousEntity)
        }.reversed()
    }
}

