package com.yonder.weightly.domain.decider

import android.content.Context
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.R
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.enums.MeasureUnit
import com.yonder.weightly.utils.extensions.orZero
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UnitFormatDecider @Inject constructor(@ApplicationContext val context: Context) {
    operator fun invoke(value: Float?): String {
        return if (MeasureUnit.findValue(Hawk.get(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT)) == MeasureUnit.KG) {
            context.getString(R.string.kg_format, value.orZero())
        } else {
            context.getString(R.string.lb_format, value.orZero())
        }
    }
}