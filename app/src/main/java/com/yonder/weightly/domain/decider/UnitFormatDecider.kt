package com.yonder.weightly.domain.decider

import android.content.Context
import com.yonder.weightly.R
import com.yonder.weightly.data.local.PreferenceManager
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.enums.MeasureUnit
import com.yonder.weightly.utils.extensions.orZero
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UnitFormatDecider
    @Inject
    constructor(
        @ApplicationContext val context: Context,
        private val preferenceManager: PreferenceManager,
    ) {
        operator fun invoke(value: Float?): String =
            if (MeasureUnit.findValue(preferenceManager.get(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT, "")) == MeasureUnit.KG) {
                String.format(context.getString(R.string.kg_format), value.orZero())
            } else {
                String.format(context.getString(R.string.lb_format), value.orZero())
            }
    }
