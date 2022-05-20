package com.yonder.weightly.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentAddWeightBinding
import com.yonder.weightly.utils.extensions.nextDay
import com.yonder.weightly.utils.extensions.prevDay
import com.yonder.weightly.utils.extensions.showToast
import com.yonder.weightly.utils.extensions.toFormat
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*

const val CURRENT_DATE_FORMAT = "dd MMM yyyy"
const val TAG_DATE_PICKER = "Tag_Date_Picker"

@AndroidEntryPoint
class AddWeightFragment : BottomSheetDialogFragment() {

    private val viewModel: AddWeightViewModel by viewModels()

    private var selectedDate = Date()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_weight, container, false)

    private val binding by viewBinding(FragmentAddWeightBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
        viewModel.fetchDate(selectedDate)
    }

    private fun initViews() = with(binding) {
        btnPrev.setOnClickListener {
            fetchDate(selectedDate.prevDay())
        }

        btnNext.setOnClickListener {
            fetchDate(selectedDate.nextDay())
        }

        btnSelectDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_date))
                    .setSelection(selectedDate.time)
                    .build()
            datePicker.addOnPositiveButtonClickListener { timestamp ->
                fetchDate(Date(timestamp))
            }
            datePicker.show(parentFragmentManager, TAG_DATE_PICKER);
        }

        btnSelectDate.text = selectedDate.toFormat(CURRENT_DATE_FORMAT)

        btnSave.setOnClickListener {
            val weight = tilInputWeight.text.toString()
            val note = tilInputNote.text.toString()
            viewModel.addWeight(weight = weight, note = note, date = selectedDate)
        }
    }

    private fun fetchDate(date: Date){
        selectedDate = date
        binding.btnSelectDate.text = selectedDate.toFormat(CURRENT_DATE_FORMAT)
        viewModel.fetchDate(selectedDate)
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    AddWeightViewModel.Event.PopBackStack -> {
                        findNavController().popBackStack()
                    }
                    is AddWeightViewModel.Event.ShowToast -> {
                        context.showToast(event.textResId)
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect(::setUIState)
        }
    }

    private fun setUIState(uiState: AddWeightViewModel.UiState) = with(binding) {
        val currentWeight = uiState.currentWeight
        tilInputNote.setText(currentWeight?.note.orEmpty())
        tilInputWeight.setText(uiState.currentWeight?.valueText.orEmpty())
        if (currentWeight == null) {
            btnSave.setText(R.string.save)
            btnSave.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_add_24)
            btnSave.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
        } else {
            btnSave.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_edit_24)
            btnSave.setText(R.string.update)
            btnSave.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }


}
