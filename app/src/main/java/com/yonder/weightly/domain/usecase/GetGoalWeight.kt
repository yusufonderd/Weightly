package com.yonder.weightly.domain.usecase

import android.content.Context
import com.yonder.weightly.R
import com.yonder.weightly.data.local.PreferenceManager
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.enums.MeasureUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetGoalWeight
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val preferenceManager: PreferenceManager,
    ) {
        operator fun invoke(): String {
            val goalWeight = preferenceManager.get(Constants.Prefs.KEY_GOAL_WEIGHT, 0.0f)
            val unit = MeasureUnit.findValue(preferenceManager.get(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT, ""))
            return if (unit == MeasureUnit.KG) {
                String.format(context.getString(R.string.kg_format), goalWeight)
            } else {
                String.format(context.getString(R.string.lb_format), goalWeight)
            }
        }
    }
