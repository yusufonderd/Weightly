package com.yonder.weightly.ui.home

import android.app.DownloadManager
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.yonder.statelayout.State
import com.yonder.weightly.BuildConfig
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentHomeBinding
import com.yonder.weightly.network.ApkDownloadManager
import com.yonder.weightly.network.Constants
import com.yonder.weightly.network.DownloadReceiver
import com.yonder.weightly.ui.home.adapter.HomeInfoCardAdapter
import com.yonder.weightly.ui.home.adapter.HomeInfoCardCreator
import com.yonder.weightly.ui.home.chart.ChartFeeder
import com.yonder.weightly.ui.home.chart.ChartInitializer
import com.yonder.weightly.ui.home.chart.LimitLineFeeder
import com.yonder.weightly.utils.enums.ChartType
import com.yonder.weightly.utils.extensions.safeNavigate
import com.yonder.weightly.utils.setSafeOnClickListener
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel: HomeViewModel by viewModels()

    private val adapter: HomeInfoCardAdapter by lazy {
        HomeInfoCardAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    val receiver = DownloadReceiver()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
        initAdListener()
        requireContext().registerReceiver(
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )

    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect(::setUIState)
        }
    }

    private fun setUIState(uiState: HomeViewModel.UiState) = with(binding) {
        if (uiState.shouldShowEmptyView) {
            stateLayout.setState(State.EMPTY)
        } else {
            stateLayout.setState(State.CONTENT)
            binding.adView.isVisible = uiState.shouldShowAds
            btnRemoveAds.isVisible = uiState.shouldShowAds
            binding.btnAddWeightForToday.isVisible = uiState.shouldShowAddWeightForTodayButton
            if (uiState.chartType == ChartType.LINE) {
                lineChart.isVisible = true
                barChart.isVisible = false
                ChartFeeder.setLineChartData(
                    chart = lineChart,
                    histories = uiState.histories,
                    barEntries = uiState.barEntries,
                    context = requireContext()
                )

            } else {
                lineChart.isVisible = false
                barChart.isVisible = true
                ChartFeeder.setBarChartData(
                    chart = barChart,
                    histories = uiState.histories,
                    barEntries = uiState.barEntries,
                    context = requireContext()
                )
            }

            if (uiState.shouldShowLimitLine) {
                LimitLineFeeder.addLimitLineToLineChart(
                    requireContext(),
                    lineChart,
                    uiState.averageWeight?.toFloatOrNull(),
                    uiState.goalWeight?.toFloatOrNull()
                )
                LimitLineFeeder.addLimitLineToBarChart(
                    requireContext(),
                    barChart,
                    uiState.averageWeight?.toFloatOrNull(),
                    uiState.goalWeight?.toFloatOrNull()
                )
            } else {
                LimitLineFeeder.removeLimitLines(lineChart = lineChart, barChart = barChart)
            }
            val infoCardList = HomeInfoCardCreator.create(uiState)
            adapter.submitList(infoCardList)
            uiState.userGoal?.run(tvGoalDescription::setText)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchHome()
        viewModel.checkIsPremiumUser()
        viewModel.hasUserWeightForToday()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(receiver)
    }

    override fun onStop() {
        super.onStop()
        viewModel.cancelJobs()
    }

    private fun initViews() = with(binding) {

        with(rvInfoCard) {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = this@HomeFragment.adapter
            itemAnimator = null
        }

        ChartInitializer.initLineChart(lineChart)
        ChartInitializer.initBarChart(barChart)
        btnRemoveAds.text = "${BuildConfig.VERSION_CODE} / ${BuildConfig.VERSION_NAME}"
        btnRemoveAds.setSafeOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PermissionChecker.PERMISSION_DENIED
                ) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val canBeInstall =
                    context?.getPackageManager()?.canRequestPackageInstalls() ?: false
                Timber.e("canBeInstall :$canBeInstall")
                if (canBeInstall) {
                    ApkDownloadManager.startDownload(
                        requireContext(),
                        Constants.apkUrl,
                        Constants.fileName
                    )
                }else{
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:${requireContext().packageName}")
                        )
                    )

                }
            }

            //    viewModel.startBilling(requireActivity())
        }
        btnAddWeightForToday.setSafeOnClickListener {
            safeNavigate(
                HomeFragmentDirections.actionNavigateAddWeight(
                    selectedDate = null,
                    weight = null
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    private fun initAdListener() {
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                safeNavigate(
                    HomeFragmentDirections.actionNavigateAddWeight(
                        null,
                        null
                    )
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}