package com.yonder.weightly.domain.usecase

import com.yonder.weightly.data.local.WeightDao
import javax.inject.Inject

class DeleteAllWeights @Inject constructor(private val weightDao: WeightDao) {

    suspend operator fun invoke(){
        weightDao.deleteAll()
    }

}
