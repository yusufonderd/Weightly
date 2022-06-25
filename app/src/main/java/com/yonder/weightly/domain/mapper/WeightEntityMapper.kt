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

    fun map(entity: WeightEntity?, previousEntity: WeightEntity? = null): WeightUIModel? {
        return entity?.toUiModelByComparingWithPreviousWeight(previousEntity)
    }

    private fun WeightEntity.toUiModelByComparingWithPreviousWeight(previousEntity: WeightEntity?) = WeightUIModel(
        uid = getUi(),
        value = getValue(),
        valueText = getValueText(),
        valueWithUnit = getValueWithUnit(),
        emoji = getEmoji(),
        note = getNote(),
        date = getDate(),
        formattedDate = getFormattedDate(),
        formattedValue = getFormattedValue(),
        difference = getDifferenceText(previousEntity),
        differenceColor = getDifferenceColor(previousEntity)
    )

    private fun WeightEntity.getUi() = uid.orZero

    private fun WeightEntity.getValue() = value.orZero()

    private fun WeightEntity.getValueText() = value?.toString().orEmpty()

    private fun WeightEntity.getValueWithUnit() = unitFormatDecider.invoke(value)

    private fun WeightEntity.getEmoji() = emoji.orEmpty()

    private fun WeightEntity.getNote() = note.orEmpty()

    private fun WeightEntity.getDate() = timestamp ?: Date()

    private fun WeightEntity.getFormattedDate() = getDate().toFormat(DATE_FORMAT_CHART)

    private fun WeightEntity.getFormattedValue() = "${getEmoji()} ${getValueText()}"

    private fun WeightEntity.getDifference(previousEntity: WeightEntity?) = differenceDecider.provideValue(
        current = value,
        previous = previousEntity?.value
    )

    private fun WeightEntity.getDifferenceText(previousEntity: WeightEntity?) = differenceDecider.provideText(
        difference = getDifference(previousEntity)
    )

    private fun WeightEntity.getDifferenceColor(previousEntity: WeightEntity?) = differenceDecider.provideColor(
        difference = getDifference(previousEntity)
    )
}
