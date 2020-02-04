package com.boswelja.quickmessage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.preference.PreferenceManager
import com.boswelja.quickmessage.MessageHelper.PHONE_NUMBER_KEY

class PhoneNumberEditTextDialog(context: Context) : AlertDialog.Builder(context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun createAndShow() : AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_phone_number, null)
        val phoneNumberField = view.findViewById<AppCompatEditText>(R.id.phone_number_input)
        val errorMessageView = view.findViewById<AppCompatTextView>(R.id.error_text_view)
        phoneNumberField.setText(sharedPreferences.getString(PHONE_NUMBER_KEY, "") ?: "")
        setTitle(R.string.phone_number_dialog_title)
        setView(view)
        setPositiveButton(R.string.dialog_ok) { _, _ -> }
        setNegativeButton(R.string.dialog_cancel) { _, _ -> }

        return show().also {
            it.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
                val number = phoneNumberField.text.toString()
                if (number.matches("^[+]?[0-9]{10,13}\$".toRegex())) {
                    errorMessageView.visibility = View.GONE
                    sharedPreferences.edit().putString(PHONE_NUMBER_KEY, number).apply()
                    it.dismiss()
                } else {
                    errorMessageView.visibility = View.VISIBLE
                }
            }
        }
    }
}