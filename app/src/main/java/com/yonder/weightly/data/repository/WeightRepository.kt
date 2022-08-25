package com.yonder.weightly.data.repository

import com.yonder.weightly.data.local.WeightDao
import javax.inject.Inject

class WeightRepository @Inject constructor(
    private val dbDao: WeightDao
) {
    fun getAllWeights() = dbDao.getAllWeights()
}