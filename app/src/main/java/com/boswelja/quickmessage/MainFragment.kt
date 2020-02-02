package com.boswelja.quickmessage

import android.os.Bundle
import android.telephony.SmsManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class MainFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.main_preferences)

        findPreference<Preference>("send_message_key")!!.apply {
            setOnPreferenceClickListener {
                val number = sharedPreferences.getString("pick_contact_preference_key", "")
                if (!number.isNullOrBlank() && number != "0") {
                    val message = sharedPreferences.getString("message_key", "Hello from Quick Message!")
                    if (!message.isNullOrBlank()) {
                        SmsManager.getDefault().also {
                            it.sendTextMessage(
                                number,
                                null,
                                message,
                                null,
                                null
                            )
                        }
                    }
                }
                true
            }
        }
    }

}