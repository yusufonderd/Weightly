package com.yonder.weightly.domain.usecase

import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.utils.extensions.endOfDay
import com.yonder.weightly.utils.extensions.startOfDay
import java.util.*
import javax.inject.Inject

class DeleteWeight @Inject constructor(private val weightDao: WeightDao) {

    suspend operator fun invoke(date: Date) {
        val weightList = weightDao.fetchBy(
            startDate = date.startOfDay(),
            endDate = date.endOfDay()
        )
        weightList.firstOrNull()?.run {
            weightDao.delete(this)
        }
    }
}
