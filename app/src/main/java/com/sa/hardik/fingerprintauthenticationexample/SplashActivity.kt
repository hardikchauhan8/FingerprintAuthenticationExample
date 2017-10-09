package com.sa.hardik.fingerprintauthenticationexample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.sa.hardik.fingerprintauthenticationexample.AppConstants

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(mainLooper).postDelayed({
            val pref = getSharedPreferences(AppConstants.PREF_KEY, Context.MODE_PRIVATE)
            if (pref.getBoolean(AppConstants.IS_PIN_SET, false)) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, PinSetActivity::class.java))
            }
            finish()
        }, 2000)

    }
}
