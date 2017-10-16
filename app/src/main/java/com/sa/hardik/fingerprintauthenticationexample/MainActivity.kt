package com.sa.hardik.fingerprintauthenticationexample

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.sa.hardik.fingerprintauthentication.FingerprintHandlerCallback
import com.sa.hardik.fingerprintauthentication.FingerprintManagerHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), FingerprintHandlerCallback {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val pref = getSharedPreferences(AppConstants.PREF_KEY, Context.MODE_PRIVATE)
        val pin = pref.getString(AppConstants.PIN, "")

        tvCancel.setOnClickListener {
            finish()
        }

        edtPin.setOnEditorActionListener { _, _, _ ->

            when {
                edtPin.text.length < 4 -> {
                    edtPin.error = "Enter 4 digit pin"
                    false
                }
                edtPin.text.toString() != pin -> {
                    edtPin.error = "Incorrect pin"
                    false
                }
                else -> {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    true
                }
            }
        }


        val fManager = FingerprintManagerHelper(this, "MYKEY", this)
        fManager.initFingerprint()
    }


    override fun success() {
//        Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show()
        tvAuthMsg.visibility = View.VISIBLE
        tvAuthMsg.setTextColor(Color.GREEN)
        tvAuthMsg.text = "Fingerprint Recognised"
        fingerprintimg.setImageResource(R.drawable.ic_check_circle_black_24dp)
        Handler().postDelayed({
            finish()
            startActivity(Intent(this, WelcomeActivity::class.java))
        }, 1000)
    }

    override fun error(errorMsg: String) {

        tvAuthMsg.visibility = View.VISIBLE
        tvAuthMsg.setTextColor(Color.RED)
        tvAuthMsg.text = errorMsg
        DrawableCompat.setTint(fingerprintimg.drawable, ContextCompat.getColor(this, R.color.red))
//        Handler().postDelayed({
//            tvAuthMsg.visibility = View.INVISIBLE
//            DrawableCompat.setTint(fingerprintimg.drawable, ContextCompat.getColor(this, R.color.black))
//        }, 3000)
    }
}