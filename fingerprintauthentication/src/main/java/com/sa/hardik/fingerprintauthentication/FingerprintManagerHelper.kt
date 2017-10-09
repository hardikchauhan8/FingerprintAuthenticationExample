package com.sa.hardik.fingerprintauthentication

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

/**
 * Created by hardik.chauhan
 */
class FingerprintManagerHelper(private val context: Context, private val KEY_NAME: String, private val callback: FingerprintHandlerCallback) {


    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    private var cipher: Cipher? = null
    private var keyStore: KeyStore? = null
    private var keyGenerator: KeyGenerator? = null
    private var cryptoObject: FingerprintManager.CryptoObject? = null
    private var fingerprintManager: FingerprintManager? = null
    private var keyguardManager: KeyguardManager? = null


    fun initFingerprint() {

        // If you’ve set your app’s minSdkVersion to anything lower than 23, then you’ll need to verify that the device is running Marshmallow
        // or higher before executing any fingerprint-related code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get an instance of KeyguardManager and FingerprintManager//
            keyguardManager =
                    context.getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager?
            fingerprintManager =
                    context.getSystemService(AppCompatActivity.FINGERPRINT_SERVICE) as FingerprintManager

            //Check whether the device has a fingerprint sensor//
            if (!fingerprintManager!!.isHardwareDetected) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                callback.error("Your device doesn't support fingerprint authentication")

            }
            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // If your app doesn't have this permission, then display the following text//
                callback.error("Please enable the fingerprint permission")

            }

            //Check that the user has registered at least one fingerprint//
            if (!fingerprintManager!!.hasEnrolledFingerprints()) {
                // If the user hasn’t configured any fingerprints, then display the following message//
                callback.error("No fingerprint configured. Please register at least one fingerprint in your device's Settings")
            }

            //Check that the lockscreen is secured//
            if (!keyguardManager!!.isKeyguardSecure) {
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                callback.error("Please enable lockscreen security in your device's Settings")
            } else {
                try {
                    generateKey()
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.error("Error Generating key")
                }

                if (initCipher()) {
                    //If the cipher is initialized successfully, then create a CryptoObject instance//
                    cryptoObject = FingerprintManager.CryptoObject(cipher)

                    // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                    // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                    val helper = FingerprintHandler(context, callback)
                    helper.startAuth(fingerprintManager!!, cryptoObject!!)
                }
            }
        }
    }


//Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//

    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateKey() {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore")

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

            //Initialize an empty KeyStore//
            keyStore?.load(null)

            //Initialize the KeyGenerator//
            keyGenerator?.init(

                    KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(
                                    KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build())

            //Generate the key//
            keyGenerator?.generateKey()

        } catch (e: Exception) {
            e.printStackTrace()
            callback.error("Error generating key")
        }
    }

    //Create a new method that we’ll use to initialize our cipher//
    private fun initCipher(): Boolean {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cipher = Cipher.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES + "/"
                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.error("Error generating cipher")

        }

        return try {
            keyStore?.load(null)

            val key = keyStore?.getKey(KEY_NAME, null)
            cipher?.init(Cipher.ENCRYPT_MODE, key)

            //Return true if the cipher has been initialized successfully//
            true
        } catch (e: Exception) {
            e.printStackTrace()
            callback.error("Error Loading cipher")
            false
        }
    }
}