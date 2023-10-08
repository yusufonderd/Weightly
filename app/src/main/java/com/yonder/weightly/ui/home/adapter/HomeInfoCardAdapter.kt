package com.yonder.weightly.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yonder.weightly.base.BaseListAdapter
import com.yonder.weightly.uicomponents.InfoCardUIModel


class HomeInfoCardAdapter : BaseListAdapter<InfoCardUIModel>(
    itemsSame = { old, new -> old.title == new.title },
    contentsSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return HomeInfoCardViewHolder(parent, inflater)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomeInfoCardViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }
}
