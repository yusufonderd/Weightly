package com.yonder.weightly.domain.usecase

import android.content.Context
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.R
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.uicomponents.MeasureUnit
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.extensions.orZero
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.abs

class GetUserGoal @Inject constructor(
    @ApplicationContext private val context: Context,
    private val weightDao: WeightDao
) {

     operator fun invoke(): String {
        val firstWeight = weightDao.fetchLastWeight().firstOrNull()
        val goalWeight = Hawk.get(Constants.Prefs.KEY_GOAL_WEIGHT, 0.0f)
        val difference = abs(firstWeight?.value.orZero() - goalWeight)
        val unit = MeasureUnit.findValue(Hawk.get<String>(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT))
        return if (unit == MeasureUnit.KG){
            String.format(context.getString(R.string.goal_summary_format),difference,context.getString(R.string.kg))
        }else{
            String.format(context.getString(R.string.goal_summary_format),difference,context.getString(R.string.lbs))
        }
    }

}