package com.yonder.weightly.domain.usecase

import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.data.local.WeightEntity
import com.yonder.weightly.utils.extensions.endOfDay
import com.yonder.weightly.utils.extensions.startOfDay
import java.util.*
import javax.inject.Inject

class SaveOrUpdateWeight @Inject constructor(private val weightDao: WeightDao) {

    suspend operator fun invoke(weight: String, note: String, emoji: String, date: Date) {
        val weightList = weightDao.fetchBy(
            startDate = date.startOfDay(),
            endDate = date.endOfDay()
        )
        if (weightList.isEmpty()) {
            weightDao.save(
                WeightEntity(
                    timestamp = date,
                    value = weight.toFloat(),
                    emoji = emoji,
                    note = note
                )
            )
        } else {
            weightDao.update(
                WeightEntity(
                    uid = weightList.first().uid,
                    timestamp = date,
                    value = weight.toFloat(),
                    emoji = emoji,
                    note = note
                )
            )
        }
    }
}