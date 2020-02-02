package com.boswelja.quickmessage

import android.content.Context
import android.provider.ContactsContract
import androidx.preference.PreferenceManager

object MessageHelper {
    private val CONTACTS_PROJECTION: Array<out String> = arrayOf(
        ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
        ContactsContract.Contacts.DISPLAY_NAME
    )

    fun getContactInfo(context: Context): Contact? {
        var contact: Contact? = null
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val lookupKey = sharedPreferences.getString("contact_lookup_key", "")
        if (!lookupKey.isNullOrEmpty()) {
            val contactWhere = ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
            val contactWhereParams = arrayOf(lookupKey, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            val cursor = context.contentResolver!!.query(
                ContactsContract.Data.CONTENT_URI,
                CONTACTS_PROJECTION, contactWhere, contactWhereParams, null)
            if (cursor!!.count > 0 && cursor.moveToFirst()) {
                val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))
                contact = Contact(contactName, contactNumber)
            }
            cursor.close()
        }
        return contact
    }
}