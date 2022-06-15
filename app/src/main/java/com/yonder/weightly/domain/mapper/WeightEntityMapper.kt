package com.yonder.weightly.domain.mapper

import com.yonder.weightly.R
import com.yonder.weightly.data.local.WeightEntity
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.format
import com.yonder.weightly.utils.extensions.orZero
import com.yonder.weightly.utils.extensions.toFormat
import java.lang.StringBuilder
import java.util.*

const val DATE_FORMAT_CHART = "dd MMM"
const val DEFAULT_VALUE_OF_WEIGHT_DIFFERENCE = 0.0f
object WeightEntityMapper {

    fun map(entity: WeightEntity?,previousEntity: WeightEntity? = null): WeightUIModel? {
        if (entity == null)
            return null
        val date = entity.timestamp ?: Date()
        val valueText = entity.value?.toString().orEmpty()
        val emoji = entity.emoji.orEmpty()

        val difference : Float = if (previousEntity?.value.orZero() == 0.0f) {
            DEFAULT_VALUE_OF_WEIGHT_DIFFERENCE
        } else {
            entity.value.orZero() - previousEntity?.value.orZero()
        }

        val differenceColor = if (difference == 0.0f) {
            R.color.gray_500
        } else if (difference > 0.0f) {
            R.color.red
        } else {
            R.color.green
        }

        val differenceBuilder = StringBuilder()
        if (difference > 0){
            differenceBuilder.append("+")
        }
        differenceBuilder.append(difference.format(1))

        return WeightUIModel(
            uid = entity.uid.orZero,
            value = entity.value.orZero(),
            valueText = valueText,
            emoji = emoji,
            note = entity.note.orEmpty(),
            date = date,
            formattedDate = date.toFormat(DATE_FORMAT_CHART),
            formattedValue = "$emoji $valueText",
            difference = differenceBuilder.toString(),
            differenceColor = differenceColor
        )
    }
}