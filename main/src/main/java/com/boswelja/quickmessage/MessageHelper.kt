package com.boswelja.quickmessage

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.ContactsContract
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.boswelja.quickmessage.MainFragment.Companion.MESSAGE_PREFERENCE_KEY
import com.boswelja.quickmessage.MainFragment.Companion.REQUIRE_CONFIRMATION_PREFERENCE_KEY

object MessageHelper {

    private val CONTACTS_PROJECTION: Array<out String> = arrayOf(
        ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
        ContactsContract.Contacts.DISPLAY_NAME
    )

    const val CONTACT_LOOKUP_KEY = "contact_lookup_key"
    const val PHONE_NUMBER_KEY = "phone_number_key"

    fun getContactInfo(context: Context): Contact? {
        var contact: Contact? = null
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        try {
            val lookupKey = sharedPreferences.getString(CONTACT_LOOKUP_KEY, "")
            if (!lookupKey.isNullOrEmpty()) {
                val contactWhere = ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
                val contactWhereParams = arrayOf(lookupKey, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                val cursor = context.contentResolver!!.query(
                    ContactsContract.Data.CONTENT_URI,
                    CONTACTS_PROJECTION, contactWhere, contactWhereParams, null)
                if (cursor!!.count > 0 && cursor.moveToFirst()) {
                    val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))
                    contact = Contact(contactNumber, contactName)
                }
                cursor.close()
            }
        } catch (e: SecurityException) {
            val phoneNumber = sharedPreferences.getString(PHONE_NUMBER_KEY, "")
            if (!phoneNumber.isNullOrEmpty()) {
                contact = Contact(phoneNumber)
            }
        }

        return contact
    }

    fun sendMessage(context: Context, contact: Contact?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val message = sharedPreferences.getString(MESSAGE_PREFERENCE_KEY, "Hello from Quick Message!")
        if (contact != null && !message.isNullOrEmpty()) {
            if (sharedPreferences.getBoolean(REQUIRE_CONFIRMATION_PREFERENCE_KEY, true)) {

                Intent(context, ActionService::class.java).apply {
                    action = ActionService.ACTION_SEND_MESSAGE
                }.also {
                    ContextCompat.startForegroundService(context, it)
                }
            } else {
                sendSms(contact.normalizedNumber, message)
            }
        }
    }

    fun sendSms(number: String, message: String) {
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