package com.yonder.weightly.ui.onboarding

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentOnBoardingBinding
import com.yonder.weightly.uicomponents.CardRuler
import com.yonder.weightly.utils.*
import com.yonder.weightly.utils.enums.MeasureUnit
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {
    private val binding by viewBinding(FragmentOnBoardingBinding::bind)

    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is OnBoardingViewModel.Event.Message -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }

                    is OnBoardingViewModel.Event.NavigateToHome -> {
                        findNavController().navigate(OnBoardingFragmentDirections.actionNavigateHome())
                    }
                }
            }
        }
    }

    private fun initViews() = with(binding) {
        cardRulerCurrent.render(CardRuler(unit = R.string.kg, hint = R.string.enter_current_weight))
        cardRulerGoal.render(CardRuler(unit = R.string.kg, hint = R.string.enter_goal_weight))
        btnContinue.setSafeOnClickListener {
            val currentWeight: Float = cardRulerCurrent.mValue
            val goalWeight: Float = cardRulerGoal.mValue
            val unit = if (toggleButton.checkedButtonId == R.id.button1) {
                MeasureUnit.KG
            } else {
                MeasureUnit.LB
            }
           // scheduleNotification()
            viewModel.save(
                currentWeight = currentWeight,
                goalWeight = goalWeight,
                unit = unit
            )
        }
        toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked)
                return@addOnButtonCheckedListener
            if (checkedId == R.id.button1) {
                cardRulerCurrent.setUnit(MeasureUnit.KG)
                cardRulerGoal.setUnit(MeasureUnit.KG)
            } else {
                cardRulerCurrent.setUnit(MeasureUnit.LB)
                cardRulerGoal.setUnit(MeasureUnit.LB)
            }
        }
      //  initAdListener()
    }

    private fun scheduleNotification()
    {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 30)
        calendar.set(Calendar.SECOND, 0)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Hawk.put(Constants.Prefs.KEY_IS_SCHEDULE_NOTIFICATION,true)
    }


}