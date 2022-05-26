package com.yonder.weightly.domain.mapper

import com.yonder.weightly.data.local.WeightEntity
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.orZero
import com.yonder.weightly.utils.extensions.toFormat
import java.util.*

const val DATE_FORMAT_CHART = "dd MMM"

object WeightEntityMapper {

    fun map(entity: WeightEntity?): WeightUIModel? {
        if (entity == null)
            return null
        val date = entity.timestamp ?: Date()
        val valueText =  entity.value?.toString().orEmpty()
        val emoji = entity.emoji.orEmpty()
        return WeightUIModel(
            uid = entity.uid.orZero,
            value = entity.value.orZero(),
            valueText = valueText,
            emoji = emoji,
            note = entity.note.orEmpty(),
            date = date,
            formattedDate = date.toFormat(DATE_FORMAT_CHART),
            formattedValue = "$emoji $valueText"
        )
    }
}