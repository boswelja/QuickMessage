package com.boswelja.quickmessage

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.boswelja.quickmessage.MessageHelper.getContactInfo

class MainFragment :
    PreferenceFragmentCompat(),
    Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

    private lateinit var sharedPreferences: SharedPreferences

    private var contact: Contact? = null

    private lateinit var contactPickerPreference: Preference
    private lateinit var messageEditTextPreference: EditTextPreference

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            "pick_contact_preference_key" -> {
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).also {
                    startActivityForResult(it, 1001)
                }
                true
            }
            "send_message_key" -> {
                if (!contact?.normalizedNumber.isNullOrEmpty()) {
                    val message = preference.sharedPreferences.getString("message_key", "Hello from Quick Message!")
                    if (!message.isNullOrBlank()) {
                        SmsManager.getDefault().also {
                            it.sendTextMessage(
                                contact?.normalizedNumber,
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
            else -> false
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return when (preference?.key) {
            "message_key" -> {
                if (newValue is String && newValue.isNotEmpty()) {
                    sharedPreferences.edit().putString(preference.key, newValue).apply()
                    updateMessageSummary()
                }
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        sharedPreferences = preferenceManager.sharedPreferences

        addPreferencesFromResource(R.xml.main_preferences)

        findPreference<Preference>("send_message_key")!!.apply {
            onPreferenceClickListener = this@MainFragment
        }

        messageEditTextPreference = findPreference<EditTextPreference>("message_key")!!.apply {
            onPreferenceChangeListener = this@MainFragment
        }

        contactPickerPreference = findPreference<Preference>("pick_contact_preference_key")!!.apply {
            onPreferenceClickListener = this@MainFragment
        }
        contact = getContactInfo(context!!)
        updateMessageSummary()
        updateContactPickerSummary()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1001 -> {
                if (resultCode == RESULT_OK) {
                    val result = data?.data
                    val cursor = context?.contentResolver?.query(result!!, arrayOf(ContactsContract.Data.LOOKUP_KEY), null, null, null)
                    if (cursor?.moveToFirst() == true) {
                        val lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY))
                        if (!lookupKey.isNullOrEmpty()) {
                            sharedPreferences.edit().putString("contact_lookup_key", lookupKey).apply()
                        }
                    }
                    cursor?.close()
                    contact = getContactInfo(context!!)
                    updateContactPickerSummary()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateMessageSummary() {
        messageEditTextPreference.summary = sharedPreferences.getString(messageEditTextPreference.key, "Hello from Quick Message!")
    }

    private fun updateContactPickerSummary() {
        contactPickerPreference.summary = "${contact?.name} (${contact?.normalizedNumber})"
    }

}