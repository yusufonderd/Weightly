package com.yonder.weightly.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.BuildConfig
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentAddWeightBinding
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.ui.emoji.EmojiFragment
import com.yonder.weightly.uicomponents.CardRuler
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.enums.MeasureUnit
import com.yonder.weightly.utils.extensions.*
import com.yonder.weightly.utils.setSafeOnClickListener
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


const val CURRENT_DATE_FORMAT = "dd MMM yyyy"
const val TAG_DATE_PICKER = "Tag_Date_Picker"

@AndroidEntryPoint
class AddWeightFragment : BottomSheetDialogFragment() {

    private val args: AddWeightFragmentArgs by navArgs()

    private val viewModel: AddWeightViewModel by viewModels()

    private var selectedDate = Date()
    private var emoji: String = String.EMPTY

    lateinit var adRequest: AdRequest

    private val binding by viewBinding(FragmentAddWeightBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
        val argWeight = args.weight
        val selectedDateModel = args.selectedDate
        if (argWeight != null) {
            fetchDate(argWeight.date)
        } else {
            if (selectedDateModel != null) {
                fetchDate(selectedDateModel.selectedDate)
            } else {
                //fetch current date
                binding.btnNext.isEnabled = false
                viewModel.fetchDate(selectedDate)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_weight, container, false)


    private fun initAdListener() {
        adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(),
            BuildConfig.FULL_SCREEN_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    mInterstitialAd = p0
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    mInterstitialAd = null
                }
            })
        binding.adView.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkIsPremiumUser()
    }

    override fun onStop() {
        super.onStop()
        viewModel.cancelJobs()
    }

    private fun initViews() = with(binding) {

        initAdListener()
        btnPrev.setSafeOnClickListener {
            fetchDate(selectedDate.prevDay())
        }

        btnNext.setSafeOnClickListener {
            fetchDate(selectedDate.nextDay())
        }

        btnEmoji.setSafeOnClickListener {
            findNavController().navigate(R.id.action_navigate_emoji)
        }

        btnDelete.setSafeOnClickListener {
            viewModel.delete(date = selectedDate)
            findNavController().popBackStack()
        }

        btnSelectDate.setSafeOnClickListener {
            val calendar = Calendar.getInstance()
            val startFrom = calendar.timeInMillis
            val constraints = CalendarConstraints.Builder()
                .setEnd(startFrom)
                .setValidator(DateValidatorPointBackward.now())
                .build()

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_date))
                    .setCalendarConstraints(constraints)
                    .setSelection(selectedDate.time)
                    .build()
            datePicker.addOnPositiveButtonClickListener { timestamp ->
                fetchDate(Date(timestamp))
            }

            datePicker.show(parentFragmentManager, TAG_DATE_PICKER);
        }

        btnSelectDate.text = selectedDate.toFormat(CURRENT_DATE_FORMAT)

        val unit = MeasureUnit.findValue(Hawk.get<String>(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT))
        val unitResource = if (unit == MeasureUnit.KG) {
            R.string.kg
        } else {
            R.string.lbs
        }
        binding.cardRulerWeight.render(
            CardRuler(
                unit = unitResource,
                hint = R.string.enter_current_weight
            )
        )

        btnSaveOrUpdate.setSafeOnClickListener {
            val weight = binding.cardRulerWeight.mValue.toString()
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
        emoji = String.EMPTY
        selectedDate = date
        binding.btnSelectDate.text = selectedDate.toFormat(CURRENT_DATE_FORMAT)
        viewModel.fetchDate(selectedDate)
        val shouldHideNextButton =
            selectedDate > Date() || selectedDate.toFormat(CURRENT_DATE_FORMAT) == Date().toFormat(
                CURRENT_DATE_FORMAT
            )
        binding.btnNext.isEnabled = shouldHideNextButton.not()
    }

    private var mInterstitialAd: InterstitialAd? = null

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    AddWeightViewModel.Event.PopBackStack -> {
                        goBack()
                    }

                    AddWeightViewModel.Event.ShowInterstitialAd -> {
                        showInterstitialAd()
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

    private fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object :
                FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    goBack()
                }

                override fun onAdDismissedFullScreenContent() {
                    goBack()
                }
            }
            mInterstitialAd?.show(requireActivity())
        } else {
            goBack()
        }
    }

    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun setUIState(uiState: AddWeightViewModel.UiState) = with(binding) {
        val weight = uiState.currentWeight
        val shouldShowSaveButton = uiState.shouldShowSaveButton
        tilInputNote.setText(weight?.note.orEmpty())
        cardRulerWeight.setValue(uiState.currentWeight?.value)
        setBtnSaveStatus(shouldShowSaveButton = shouldShowSaveButton)
        setBtnEmojiStatus(weight = weight)
        setDeleteButton(shouldShowSaveButton = shouldShowSaveButton)
        binding.adView.isVisible = uiState.shouldShowAds
    }

    private fun setBtnEmojiStatus(weight: WeightUIModel?) = with(binding.btnEmoji) {
        if (weight == null) {
            setText(R.string.select_emoji)
        } else {
            emoji = weight.emoji
            text = getString(R.string.select_emoji_with_emoji_format, weight.emoji)
        }
    }

    private fun setDeleteButton(shouldShowSaveButton: Boolean) {
        binding.btnDelete.isGone = shouldShowSaveButton
    }

    private fun setBtnSaveStatus(shouldShowSaveButton: Boolean) = with(binding.btnSaveOrUpdate) {
        if (shouldShowSaveButton) {
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
