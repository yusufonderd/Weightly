package com.yonder.weightly.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentAddWeightBinding
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.ui.emoji.EmojiFragment
import com.yonder.weightly.utils.extensions.*
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*


const val CURRENT_DATE_FORMAT = "dd MMM yyyy"
const val TAG_DATE_PICKER = "Tag_Date_Picker"

@AndroidEntryPoint
class AddWeightFragment : BottomSheetDialogFragment() {

    private val args: AddWeightFragmentArgs by navArgs()

    private val viewModel: AddWeightViewModel by viewModels()

    private var selectedDate = Date()
    private var emoji: String = String.EMPTY

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

        args.weight?.date?.run(::fetchDate)

        btnPrev.setOnClickListener {
            fetchDate(selectedDate.prevDay())
        }

        btnNext.setOnClickListener {
            fetchDate(selectedDate.nextDay())
        }

        btnEmoji.setOnClickListener {
            findNavController().navigate(R.id.action_navigate_emoji)
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

        btnSaveOrUpdate.setOnClickListener {
            val weight = tilInputWeight.text.toString()
            val note = tilInputNote.text.toString()
            viewModel.saveOrUpdateWeight(
                weight = weight,
                note = note,
                emoji = emoji,
                date = selectedDate
            )
        }
    }

    private fun fetchDate(date: Date) {
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

        setFragmentResultListener(EmojiFragment.KEY_REQUEST_EMOJI) { _, bundle ->
            emoji = bundle.getString(EmojiFragment.KEY_BUNDLE_EMOJI).orEmpty()
            binding.btnEmoji.text = getString(R.string.select_emoji_with_emoji_format, emoji)
        }

    }

    private fun setUIState(uiState: AddWeightViewModel.UiState) = with(binding) {
        val weight = uiState.currentWeight
        tilInputNote.setText(weight?.note.orEmpty())
        tilInputWeight.setText(uiState.currentWeight?.valueText.orEmpty())
        setBtnSaveStatus(weight = weight)
        setBtnEmojiStatus(weight = weight)
    }

    private fun setBtnEmojiStatus(weight: WeightUIModel?) = with(binding.btnEmoji) {
        if (weight == null) {
            setText(R.string.select_emoji)
        } else {
            text = getString(R.string.select_emoji_with_emoji_format, weight.emoji)
        }
    }

    private fun setBtnSaveStatus(weight: WeightUIModel?) = with(binding.btnSaveOrUpdate) {
        if (weight == null) {
            setText(R.string.save)
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_add_24)
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
        } else {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_edit_24)
            setText(R.string.update)
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }


}
