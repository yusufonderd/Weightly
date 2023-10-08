package com.yonder.weightly.domain.usecase

import android.content.Context
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.R
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.enums.MeasureUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class GetGoalWeight @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    operator fun invoke(): String {
        val goalWeight = Hawk.get<Float>(Constants.Prefs.KEY_GOAL_WEIGHT)
        val unit = MeasureUnit.findValue(Hawk.get<String>(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT))
        return if (unit == MeasureUnit.KG) {
            context.getString(R.string.kg_format, goalWeight)
        } else {
            context.getString(R.string.lb_format, goalWeight)
        }
    }


}
