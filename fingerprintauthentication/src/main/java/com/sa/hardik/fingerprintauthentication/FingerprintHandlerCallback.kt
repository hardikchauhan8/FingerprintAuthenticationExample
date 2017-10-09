package com.sa.hardik.fingerprintauthentication

/**
 * Created by hardik.chauhan on 05/10/17.
 */
interface FingerprintHandlerCallback {

    fun success()

    fun error(errorMsg: String)
}