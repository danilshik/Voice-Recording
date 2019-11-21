package ru.ddstudio.voicerecording.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ru.ddstudio.voicerecording.R


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener{

    lateinit var sharedPreferences : SharedPreferences


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        onSharedPreferenceChanged(sharedPreferences, getString(R.string.sampling_rate_key))

    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        val preference = findPreference<Preference>(key.toString())
        if (preference is ListPreference) {
            val prefIndex = preference.findIndexOfValue(sharedPreferences.getString(key, ""))
            if (prefIndex >= 0) {
                preference.summary = preference.entries[prefIndex]
            }
        } else {
            preference?.summary = sharedPreferences.getString(key, "")

        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

}