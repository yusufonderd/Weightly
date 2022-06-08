package com.yonder.weightly.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.google.android.material.card.MaterialCardView
import com.yonder.weightly.R
import com.yonder.weightly.databinding.ViewCardRulerBinding


class CardRulerViewComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding: ViewCardRulerBinding by lazy {
        ViewCardRulerBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private var shouldChangeRulerView = true

    var value: Float = 0.0f

    fun render(cardRuler: CardRuler) = with(binding) {
        val context = binding.root.context
        rulerViewCurrent.setUnitStr(context.getString(R.string.kg))
        tvTitle.setText(cardRuler.title)
        tfInputCurrentWeight.setHint(cardRuler.hint)
        rulerViewCurrent.setValueListener {
            shouldChangeRulerView = false
            value = it
            tilInputCurrentWeight.setText(context.getString(R.string.kg_format, it))
        }

        tilInputCurrentWeight.addTextChangedListener {
            if (shouldChangeRulerView) {
                val weight = it.toString().trim().toFloatOrNull()
                weight?.run {
                    rulerViewCurrent.setValue(this)
                    value = this
                }
            }
            shouldChangeRulerView = true
        }

    }

}


data class CardRuler(
    @StringRes var title: Int,
    @StringRes var hint: Int
)

