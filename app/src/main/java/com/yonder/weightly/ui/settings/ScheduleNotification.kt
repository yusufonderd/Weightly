package com.yonder.weightly.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.R
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.NotificationReceiver
import com.yonder.weightly.utils.notificationID
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class ScheduleNotification @Inject constructor(
    private val alarmManager: AlarmManager,
    @ApplicationContext val context: Context
) {

    fun setAlarm(hour: Int, minute: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        alarmManager.cancel(pendingIntent)
        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        if (now.after(calendar)) {
            calendar.add(Calendar.DATE, 1)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent,
        )
        Hawk.put(Constants.Prefs.KEY_IS_SCHEDULE_NOTIFICATION, true)
    }

    fun removeAlarm() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        alarmManager.cancel(pendingIntent)
    }
}