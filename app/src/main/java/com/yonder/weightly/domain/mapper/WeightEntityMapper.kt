package com.yonder.weightly.domain.mapper

import com.yonder.weightly.data.local.WeightEntity
import com.yonder.weightly.domain.decider.DifferenceDecider
import com.yonder.weightly.domain.decider.UnitFormatDecider
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.orZero
import com.yonder.weightly.utils.extensions.toFormat
import java.util.*
import javax.inject.Inject

const val DATE_FORMAT_CHART = "dd MMM"
const val DEFAULT_VALUE_OF_WEIGHT_DIFFERENCE = 0.0f

class WeightEntityMapper @Inject constructor(
    private val unitFormatDecider: UnitFormatDecider,
    private val differenceDecider: DifferenceDecider
) {

    fun map(entity: WeightEntity, previousEntity: WeightEntity? = null): WeightUIModel {
        return with(entity) {
            val safeDate = timestamp ?: Date()
            val safeValue = value?.toString().orEmpty()
            val difference = differenceDecider.getDifference(
                current = value,
                previous = previousEntity?.value
            )
            val safeEmoji = emoji.orEmpty()
            WeightUIModel(
                uid = uid.orZero,
                value = value.orZero(),
                valueText = safeValue,
                valueWithUnit = unitFormatDecider.invoke(value),
                emoji = safeEmoji,
                note = note.orEmpty(),
                date = safeDate,
                formattedDate = safeDate.toFormat(DATE_FORMAT_CHART),
                formattedValue = "$safeEmoji $safeValue",
                difference = differenceDecider.provideText(difference = difference),
                differenceColor = differenceDecider.provideColor(difference = difference)
            )
        }

    }

}