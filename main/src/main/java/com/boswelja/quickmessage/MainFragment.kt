package com.boswelja.quickmessage

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import androidx.core.net.toUri
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.boswelja.quickmessage.MessageHelper.CONTACT_LOOKUP_KEY
import com.boswelja.quickmessage.MessageHelper.getContactInfo
import com.boswelja.quickmessage.MessageHelper.sendMessage

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
            CONTACT_PICKER_PREFERENCE_KEY -> {
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).also {
                    startActivityForResult(it, 1001)
                }
                true
            }
            SEND_MESSAGE_KEY -> {
                if (!contact?.normalizedNumber.isNullOrEmpty()) {
                    val message = preference.sharedPreferences.getString(MESSAGE_PREFERENCE_KEY, "Hello from Quick Message!")
                    sendMessage(contact, message)
                }
                true
            }
            SOURCE_CODE_KEY -> {
                Intent(Intent.ACTION_VIEW, getString(R.string.source_code_url).toUri()).also {
                    if (it.resolveActivity(activity!!.packageManager) != null) {
                        startActivity(it)
                    }
                }
                true
            }
            else -> false
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return when (preference?.key) {
            MESSAGE_PREFERENCE_KEY -> {
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

        contact = getContactInfo(context!!)

        initAboutSection()

        findPreference<Preference>(SEND_MESSAGE_KEY)!!.apply {
            onPreferenceClickListener = this@MainFragment
        }

        messageEditTextPreference = findPreference<EditTextPreference>(MESSAGE_PREFERENCE_KEY)!!.apply {
            onPreferenceChangeListener = this@MainFragment
        }

        contactPickerPreference = findPreference<Preference>(CONTACT_PICKER_PREFERENCE_KEY)!!.apply {
            onPreferenceClickListener = this@MainFragment
        }
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
                            sharedPreferences.edit().putString(CONTACT_LOOKUP_KEY, lookupKey).apply()
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

    private fun initAboutSection() {
        findPreference<Preference>(SOURCE_CODE_KEY)!!.apply {
            onPreferenceClickListener = this@MainFragment
        }

        findPreference<Preference>(APP_VERSION_KEY)!!.summary = BuildConfig.VERSION_NAME
    }

    private fun updateMessageSummary() {
        messageEditTextPreference.summary = sharedPreferences.getString(messageEditTextPreference.key, "Hello from Quick Message!")
    }

    private fun updateContactPickerSummary() {
        contactPickerPreference.summary = "${contact?.name} (${contact?.normalizedNumber})"
    }

    companion object {
        private const val CONTACT_PICKER_PREFERENCE_KEY = "pick_contact_preference_key"
        private const val MESSAGE_PREFERENCE_KEY = "message_key"
        private const val SEND_MESSAGE_KEY = "send_message_key"

        private const val SOURCE_CODE_KEY = "source_code_key"
        private const val APP_VERSION_KEY = "app_version_key"
    }
}