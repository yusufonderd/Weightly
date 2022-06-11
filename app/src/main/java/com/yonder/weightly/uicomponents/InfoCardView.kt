package com.yonder.weightly.uicomponents


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.yonder.weightly.R
import com.yonder.weightly.databinding.ViewInfoCardBinding


class InfoCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding: ViewInfoCardBinding by lazy {
        ViewInfoCardBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun render(uiModel: InfoCardUIModel) = with(binding) {
        tvDescription.setText(uiModel.description)
        tvValue.text = uiModel.title
        tvDescription.setTextColor(ContextCompat.getColor(context, uiModel.textColor))
        tvValue.setTextColor(ContextCompat.getColor(context, uiModel.textColor))
        clRoot.setBackgroundColor(ContextCompat.getColor(context, uiModel.backgroundColor))
    }

}


data class InfoCardUIModel(
    var title: String? ,
    @StringRes var description: Int,
    @ColorRes var backgroundColor: Int = R.color.white,
    @ColorRes var textColor: Int =R.color.black

    )