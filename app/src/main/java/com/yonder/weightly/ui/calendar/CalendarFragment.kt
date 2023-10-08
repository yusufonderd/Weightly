package com.yonder.weightly.ui.calendar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.yonder.weightly.R
import com.yonder.weightly.databinding.Example3CalendarDayBinding
import com.yonder.weightly.databinding.Example3CalendarHeaderBinding
import com.yonder.weightly.databinding.FragmentCalendarBinding
import com.yonder.weightly.ui.calendar.calendarview.DayViewContainer
import com.yonder.weightly.utils.extensions.setTextColorRes
import com.yonder.weightly.utils.extensions.toDate
import com.yonder.weightly.utils.extensions.toFormat
import com.yonder.weightly.utils.setSafeOnClickListener
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

const val DATE_FORMAT = "dd MMM yyyy"

@AndroidEntryPoint
class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val binding by viewBinding(FragmentCalendarBinding::bind)

    private val viewModel: CalendarViewModel by viewModels()

    lateinit var adRequest: AdRequest

    private val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
    private val daysOfWeek = daysOfWeek()
    private val currentMonth = YearMonth.now()
    private val startMonth = currentMonth.minusMonths(50)
    private val endMonth = currentMonth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdListener()
        observe()
        initViews()
    }

    private fun initViews(){
        setupCalendarView()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect(::setUIState)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is CalendarViewModel.Event.NavigateToWeight -> {
                        findNavController().navigate(
                            CalendarFragmentDirections.actionNavigateAddWeight(
                                weight= event.model,
                                selectedDate =  null
                            )
                        )
                    }
                    is CalendarViewModel.Event.NavigateToNewWeight -> {
                        findNavController().navigate(
                            CalendarFragmentDirections.actionNavigateAddWeight(
                                weight= null,
                                selectedDate =  event.model
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setUIState(uiState: CalendarViewModel.UiState) = with(binding) {
        calendarView.notifyCalendarChanged()
        adView.isVisible = uiState.shouldShowAds
        if (uiState.shouldShowAds){
            adView.loadAd(adRequest)
        }
    }

    private fun initAdListener() {
        adRequest = AdRequest.Builder().build()
    }

    private fun setupCalendarView() {
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()
            }
        }
        configureBinders(daysOfWeek)
        binding.calendarView.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val binding = Example3CalendarDayBinding.bind(view)

            init {
                view.setSafeOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }
            }
        }
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.binding.exThreeDayText
                val dotView = container.binding.exThreeDotView

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {

                    val pairedDate =
                        viewModel.uiState.value.histories.find {
                            data.date.format(formatter) == it?.date?.toFormat(DATE_FORMAT)
                        }

                    textView.isVisible = true
                    when (data.date) {
                        viewModel.selectedLocalDate -> {
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.example_3_selected_bg)
                        }
                        viewModel.today -> {
                            textView.setTextColorRes(R.color.primary)
                            textView.setBackgroundResource(R.drawable.example_3_today_bg)
                        }
                        else -> {
                            textView.setTextColorRes(R.color.black)
                            textView.background = null
                        }
                    }
                    dotView.isVisible = pairedDate != null
                } else {
                    textView.isInvisible = true
                    dotView.isInvisible = true
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = Example3CalendarHeaderBinding.bind(view).legendLayout.root
        }

        binding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].name.first().toString()
                                tv.setTextColorRes(R.color.white)
                            }
                    }
                }
            }
    }

    private fun selectDate(date: LocalDate) {
        if (viewModel.selectedLocalDate != date) {
            val oldDate = viewModel.selectedLocalDate
            viewModel.selectedLocalDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
            viewModel.getWeightByDate(
                date = date.toDate()
            )
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.cancelJobs()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getWeightHistories()
        viewModel.checkIsPremiumUser()
    }
}



