package com.example.fingerprint

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

@RequiresApi(Build.VERSION_CODES.M)
@Suppress("DEPRECATION")
class FingerPrintHelper( private val context: Context) : FingerprintManager.AuthenticationCallback(){

    lateinit var cancellationSignal: CancellationSignal

    fun startAuth( manger : FingerprintManager , cryptoObject: FingerprintManager.CryptoObject){
        cancellationSignal = CancellationSignal()
        if(ActivityCompat.checkSelfPermission(context , android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            return
        }
        manger.authenticate(cryptoObject,cancellationSignal,0,this,null)
    }
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        super.onAuthenticationError(errorCode, errString)
        Toast.makeText(context,"Authentication Error",Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        super.onAuthenticationHelp(helpCode, helpString)
        Toast.makeText(context,"Authentication Success",Toast.LENGTH_SHORT).show()
        // start new activity hear
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        super.onAuthenticationSucceeded(result)
        Toast.makeText(context,"Authentication Help",Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        Toast.makeText(context,"Authentication Failed",Toast.LENGTH_SHORT).show()
    }
}