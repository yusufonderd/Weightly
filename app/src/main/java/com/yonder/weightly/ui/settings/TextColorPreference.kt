package com.yonder.weightly.ui.settings

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

class TextColorPreference(
    context: Context,
    attrs: AttributeSet?,
): Preference(context, attrs) {


    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.itemView.findViewById<TextView>(android.R.id.title)?.setTextColor(Color.RED)
    }

}