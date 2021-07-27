package com.example.fingerprint.fingerprintfragment

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.fingerprint.fingerprinthelperclass.FingerPrintHelper
import com.example.fingerprint.databinding.FragmentFingerPrintBinding
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@Suppress("DEPRECATION")
class FingerPrintFragment : Fragment() {

    lateinit var binding : FragmentFingerPrintBinding

    lateinit var fm             : FingerprintManager
    lateinit var km             : KeyguardManager
    lateinit var keyStore       : KeyStore
    lateinit var keyGenerator   : KeyGenerator
    lateinit var cipher         : Cipher
    lateinit var cryptoObject   : FingerprintManager.CryptoObject

    var KEY_NAME = "my_key"

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFingerPrintBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        km = activity?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        fm = activity?.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager

        if(!km.isKeyguardSecure){
            Toast.makeText(requireActivity() , "Lock Screen Security not enabled in settings", Toast.LENGTH_SHORT).show()
            return
        }

        if(!fm.hasEnrolledFingerprints()){
            Toast.makeText(requireActivity(),"Register atLeast one fingerprint in settings", Toast.LENGTH_SHORT).show()
            return
        }

        if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity() , arrayOf(android.Manifest.permission.USE_FINGERPRINT) , 111)
        }else{
            validateFingerPrint()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if( requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            validateFingerPrint()
        }
    }

    // fun validate for finger print.
    @RequiresApi(Build.VERSION_CODES.M)
    private fun validateFingerPrint() {
        // generator key.
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore")
            keyStore.load(null)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build())
            keyGenerator.generateKey()
        }catch (e:Exception){}

        // Initialization of Cryptography
        if(initCipher()){
            cipher.let {
                cryptoObject = FingerprintManager.CryptoObject(it)
            }
        }
        val helper = FingerPrintHelper(requireActivity(),this)
        if(fm != null && cryptoObject!= null){
            helper.startAuth(fm,cryptoObject)
        }
    }

    // fun init cipher.
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCipher(): Boolean {
        try {
            cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES+"/"
                    + KeyProperties.BLOCK_MODE_CBC+"/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        }catch (e:Exception){}

        try {
            keyStore.load(null)
            val key = keyStore.getKey(KEY_NAME,null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE,key)
            return true
        }catch (e:Exception){
            return false
        }
    }
}