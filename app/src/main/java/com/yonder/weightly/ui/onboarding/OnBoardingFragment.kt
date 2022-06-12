package com.yonder.weightly.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentOnBoardingBinding
import com.yonder.weightly.uicomponents.CardRuler
import com.yonder.weightly.uicomponents.MeasureUnit
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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
                    OnBoardingViewModel.Event.NavigateToHome -> {
                        findNavController().navigate(OnBoardingFragmentDirections.actionNavigateHome())
                    }
                }
            }
        }
    }

    private fun initViews() = with(binding) {
        cardRulerCurrent.render(CardRuler(unit = R.string.kg, hint = R.string.enter_current_weight))
        cardRulerGoal.render(CardRuler(unit = R.string.kg, hint = R.string.enter_goal_weight))
        cardRulerHeight.render(
            CardRuler(
                unit = R.string.cm,
                hint = R.string.enter_current_height,
                num = 175f,
                max = 250
            )
        )
        btnContinue.setOnClickListener {
            val currentWeight: Float = cardRulerCurrent.value
            val goalWeight: Float = cardRulerGoal.value
            val currentHeight: Float = cardRulerHeight.value
            val unit = if (toggleButton.checkedButtonId == R.id.button1) {
                MeasureUnit.KG
            } else {
                MeasureUnit.LB
            }
            viewModel.save(
                currentWeight = currentWeight,
                goalWeight = goalWeight,
                currentHeight = currentHeight,
                unit = unit
            )
        }
        toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
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
    }

}