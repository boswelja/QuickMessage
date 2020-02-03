package com.boswelja.quickmessage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.preference.PreferenceManager
import com.boswelja.quickmessage.MainFragment.Companion.MESSAGE_PREFERENCE_KEY
import com.boswelja.quickmessage.MessageHelper.getContactInfo
import com.boswelja.quickmessage.MessageHelper.sendSms

class ConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm_send_message)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val message = sharedPreferences.getString(MESSAGE_PREFERENCE_KEY, "Hello from Quick Message!")

        val contact = getContactInfo(this)
        findViewById<AppCompatTextView>(R.id.dialog_message).apply {
            text = getString(R.string.confirm_dialog_message, message, contact?.name ?: contact?.normalizedNumber)
        }
        findViewById<AppCompatButton>(R.id.positive_button).apply {
            setOnClickListener {
                sendSms(contact?.normalizedNumber!!, message!!)
                finish()
            }
        }
        findViewById<AppCompatButton>(R.id.negative_button).apply {
            setOnClickListener {
                finish()
            }
        }
    }
}