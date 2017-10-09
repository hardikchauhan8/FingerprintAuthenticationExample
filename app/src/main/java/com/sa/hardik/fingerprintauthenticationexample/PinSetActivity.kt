package com.sa.hardik.fingerprintauthenticationexample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sa.hardik.fingerprintauthenticationexample.AppConstants
import kotlinx.android.synthetic.main.activity_pin_set.*

class PinSetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_set)

        val pref = getSharedPreferences(AppConstants.PREF_KEY, Context.MODE_PRIVATE)
        val editor = pref.edit()

        editText.setOnEditorActionListener { _, _, keyEvent ->

            if (editText.text.length < 4) {
                editText.error = "Enter 4 digit pin"
                false
            } else {

                editor.putBoolean(AppConstants.IS_PIN_SET, true)
                editor.putString(AppConstants.PIN, editText.text.toString())
                editor.apply()

                startActivity(Intent(this@PinSetActivity, MainActivity::class.java))

                true
            }
        }
    }
}
