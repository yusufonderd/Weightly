package com.yonder.weightly.ui.home.adapter

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.yonder.weightly.R
import com.yonder.weightly.databinding.ItemWeightHistoryBinding
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.toFormat

const val DATE_FORMAT = "dd MMM yyyy"

class WeightHistoryViewHolder(
    view: View,
    private val onClickWeight: ((weight: WeightUIModel) -> Unit)?
) :
    RecyclerView.ViewHolder(view) {
    private val binding = ItemWeightHistoryBinding.bind(view)

    fun bind(uiModel: WeightUIModel) = with(binding) {
        tvNote.text = uiModel.note
        tvEmoji.text = uiModel.emoji
        tvNote.isGone = uiModel.note.isBlank()
        tvDate.text = uiModel.date.toFormat(DATE_FORMAT)
        tvWeight.text = itemView.context.getString(R.string.kg_format, uiModel.value)
        itemView.setOnClickListener {
            onClickWeight?.invoke(uiModel)
        }
    }
}


