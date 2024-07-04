package com.yonder.weightly.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.google.android.material.card.MaterialCardView
import com.yonder.weightly.R
import com.yonder.weightly.databinding.ViewCardRulerBinding
import com.yonder.weightly.utils.enums.MeasureUnit
import com.yonder.weightly.utils.extensions.orZero

const val FLOOR_FOR_LB_TO_KG = 2.204f

class CardRulerViewComponent
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : MaterialCardView(context, attrs, defStyleAttr) {
        private val binding: ViewCardRulerBinding by lazy {
            ViewCardRulerBinding.inflate(LayoutInflater.from(context), this, true)
        }

        private var shouldChangeRulerView = true

        var mValue: Float = 0.0f

        var currentUnit = MeasureUnit.KG

        fun setUnit(unit: MeasureUnit) {
            if (unit == MeasureUnit.LB) {
                if (currentUnit == MeasureUnit.KG) {
                    mValue *= FLOOR_FOR_LB_TO_KG
                }
                binding.rulerViewCurrent.setUnitStr(context.getString(R.string.lbs))
            } else {
                if (currentUnit == MeasureUnit.LB) {
                    mValue /= FLOOR_FOR_LB_TO_KG
                }
                binding.rulerViewCurrent.setUnitStr(context.getString(R.string.kg))
            }
            currentUnit = unit
            binding.tilInputCurrentWeight.setText(
                String.format(context.getString(R.string.kg_format), mValue),
            )
            binding.rulerViewCurrent.setValue(mValue)
        }

        fun setValue(value: Float?) =
            with(binding) {
                mValue = value.orZero()
                if (value != null) {
                    rulerViewCurrent.setValue(value)
                    tilInputCurrentWeight.setText(String.format(context.getString(R.string.kg_format), value))
                } else {
                    rulerViewCurrent.setValue(0f)
                    tilInputCurrentWeight.setText("")
                }
            }

        fun render(cardRuler: CardRuler) =
            with(binding) {
                val context = binding.root.context
                rulerViewCurrent.setUnitStr(context.getString(cardRuler.unit))
                tfInputCurrentWeight.setHint(cardRuler.hint)
                rulerViewCurrent.setValueListener {
                    shouldChangeRulerView = false
                    mValue = it
                    tilInputCurrentWeight.setText(String.format(context.getString(R.string.kg_format), it))
                }

                rulerViewCurrent.setMaxValue(cardRuler.max)
                rulerViewCurrent.setValue(cardRuler.num)

                tilInputCurrentWeight.addTextChangedListener {
                    if (shouldChangeRulerView) {
                        val weight = it.toString().trim().toFloatOrNull()
                        weight?.run {
                            rulerViewCurrent.setValue(this)
                            mValue = this
                        }
                    }
                    shouldChangeRulerView = true
                }
            }
    }

data class CardRuler(
    @StringRes var unit: Int,
    @StringRes var hint: Int,
    var num: Float = 75.0f,
    var max: Int = 350,
)
