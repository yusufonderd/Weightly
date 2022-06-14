package com.yonder.weightly.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentSettingsBinding
import com.yonder.weightly.ui.splash.SplashViewModel
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preference, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        findPreference<Preference>("developer")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                openUrl("https://twitter.com/iamyusufonder")
                true
            }
        findPreference<Preference>("source_code")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                openUrl("https://github.com/yusufonderd/Weightly")
                true
            }
        findPreference<Preference>("send_feedback")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                openUrl("https://forms.gle/kNxxSE4SS1xy2qRt7")
                true
            }
    }
    private fun openUrl(url : String){
        val viewIntent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(url)
        )
        startActivity(viewIntent)
    }

}