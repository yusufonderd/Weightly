package com.yonder.weightly.domain.mapper

import com.yonder.weightly.data.local.WeightEntity
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.orZero

object WeightEntityMapper {

    fun map(entity: WeightEntity): WeightUIModel {
        return WeightUIModel(
            uid = entity.uid,
            value = entity.value.orZero(),
            emoji = entity.emoji.orEmpty(),
            note = entity.note.orEmpty()
        )
    }
}