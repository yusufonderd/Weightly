package com.yonder.weightly.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.yonder.weightly.base.BaseViewHolder
import com.yonder.weightly.databinding.ItemHomeInfoCardBinding
import com.yonder.weightly.uicomponents.InfoCardUIModel

class HomeInfoCardViewHolder(
    parent: ViewGroup,
    inflater: LayoutInflater
) : BaseViewHolder<ItemHomeInfoCardBinding>(
    binding = ItemHomeInfoCardBinding.inflate(inflater, parent, false)
) {

    fun bind(uiModel: InfoCardUIModel) = with(binding) {
        infoCard.render(uiModel)
    }

}