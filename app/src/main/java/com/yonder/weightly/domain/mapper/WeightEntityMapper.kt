package com.yonder.weightly.domain.mapper

import com.yonder.weightly.data.local.WeightEntity
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.orZero
import java.util.*

object WeightEntityMapper {

    fun map(entity: WeightEntity?): WeightUIModel? {
        if (entity == null)
            return null
        return WeightUIModel(
            uid = entity.uid.orZero,
            value = entity.value.orZero(),
            emoji = entity.emoji.orEmpty(),
            note = entity.note.orEmpty(),
            date = entity.timestamp ?: Date()
        )
    }
}