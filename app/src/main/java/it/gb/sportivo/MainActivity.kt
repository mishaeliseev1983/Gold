package it.gb.sportivo

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import it.gb.sportivo.databinding.ActivityMainBinding
import it.gb.sportivo.presentation.dummy_activity.DummyActivity
import it.gb.sportivo.presentation.message_fragment.MessageFragment
import it.gb.sportivo.presentation.webview_fragment.WebViewFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


const val TAG = "MainActivity Log"
const val REMOTE_URL = "url"
const val CHECK_VPN = "to"

class MainActivity : AppCompatActivity() {

    private var mUploadMessage: ValueCallback<Uri?>? = null
    private var mCapturedImageURI: Uri? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraPhotoPath: String? = null


    companion object {
        // const val SP_LINK = "SP_LINK"
        // const val SP_LINK_VALUE = "SP_LINK_VALUE"

        private const val INPUT_FILE_REQUEST_CODE = 1
        private const val FILECHOOSER_RESULTCODE = 1
    }



    @Inject
    lateinit var viewModelFactory: ViewModuleFactory



    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw RuntimeException("Object ActivityMainBinding is null")

    private lateinit var remoteConfig: FirebaseRemoteConfig


    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val daggerApplicationComponent by lazy {
        (application as App).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daggerApplicationComponent.inject(this)

        if (savedInstanceState != null) return
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 360
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        viewModel.liveData.observe(this) { url ->
            if (url.isEmpty()) {
                showRemoteUrlOrDUMMY()
            }
            else{
                //connectivityManager.isNetworkAvailable.observe(this) {
                //        haveInternet ->
                    showLocalUrlOrNotInternet(url, internet = true)
                //}
            }
        }

        viewModel.liveDataWithVPN.observe(this) { url ->
            if (url.isEmpty()) {
                showRemoteUrlWithVPNOrDUMMY()
            }
            else{
                //connectivityManager.isNetworkAvailable.observe(this) {
                //        haveInternet ->
                    showLocalUrlOrNotInternet(url, internet = true)
                //}
            }
        }


        letsStarted()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



    private fun letsStarted() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    val checkVpn = remoteConfig.getBoolean(CHECK_VPN)
                    if(checkVpn)
                    //2 params check from firebase - url, to
                        viewModel.readLocalUrlWithVPN()
                    else
                    //1 param - url
                        viewModel.readLocalUrl()

                } else {
                    viewModel.readLocalUrl()
                }

            }
    }

    private fun showRemoteUrlOrDUMMY() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    val remoteUrl = remoteConfig.getString(REMOTE_URL)

                    val itsEMU = checkIsEmu()
                    val hasNoSim = !isSIMInserted(applicationContext)
                    if (remoteUrl.isEmpty() || itsEMU || hasNoSim) {

                        val intent = Intent(this, DummyActivity::class.java)
                        startActivity(intent)
                        finish()
//                        val startFragment = WelcomeFragment.newInstance()
//                        supportFragmentManager.beginTransaction()
//                            .add(R.id.container, startFragment).commit()
                    } else {
                        viewModel.writeUrl(remoteUrl)
                        val fragmentWebView = WebViewFragment.newInstance(remoteUrl)
                        supportFragmentManager.beginTransaction()
                            .add(R.id.container, fragmentWebView).commit()
                    }

                } else {
                    val messageFragment =
                        MessageFragment.newInstance(getString(R.string.param_did_not_read_from_remote))
                    supportFragmentManager.beginTransaction()
                        .add(R.id.container, messageFragment).commit()
                }
            }
    }


    private fun showRemoteUrlWithVPNOrDUMMY() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    val remoteUrl = remoteConfig.getString(REMOTE_URL)

                    val itsEMU = checkIsEmu()
                    val hasNoSim = !isSIMInserted(applicationContext)

                    val vpn = vpnActive(applicationContext)
                    if (remoteUrl.isEmpty() || itsEMU || hasNoSim || vpn) {
                        //val welcomeFragment = WelcomeFragment.newInstance()
                        //supportFragmentManager.beginTransaction()
                        //    .add(R.id.container, welcomeFragment).commit()
//                        val startFragment = WelcomeFragment.newInstance()
//                        supportFragmentManager.beginTransaction()
//                            .add(R.id.container, startFragment).commit()

                        val intent = Intent(this, DummyActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        viewModel.writeUrl(remoteUrl)
                        val fragmentWebView = WebViewFragment.newInstance(remoteUrl)
                        supportFragmentManager.beginTransaction()
                            .add(R.id.container, fragmentWebView).commit()
                    }

                } else {
                    val messageFragment =
                        MessageFragment.newInstance(getString(R.string.param_did_not_read_from_remote))
                    supportFragmentManager.beginTransaction()
                        .add(R.id.container, messageFragment).commit()
                }
            }
    }


    private fun showLocalUrlOrNotInternet(urlLocal: String, internet: Boolean) {

        //if (internet) {
        val webViewFragment = WebViewFragment.newInstance(urlLocal)
        supportFragmentManager.beginTransaction()
            .add(R.id.container, webViewFragment).commit()
        /*} else {
            val messageFragment =
                MessageFragment.newInstance(getString(R.string.no_internet))
            supportFragmentManager.beginTransaction()
                .add(R.id.container, messageFragment).commit()
        }*/
    }


    override fun onBackPressed() {
        super.onBackPressed()
        WebViewFragment?.backpressedlistener?.onBackPressed()
    }





    private fun checkIsEmu(): Boolean {
        if (BuildConfig.DEBUG) return false // when developer use this build on emulator
        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE

        var result = (Build.FINGERPRINT.startsWith("generic")
                || phoneModel.contains("google_sdk")
                || phoneModel.lowercase(Locale.getDefault()).contains("droid4x")
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || buildHardware == "goldfish"
                || Build.BRAND.contains("google")
                || buildHardware == "vbox86"
                || buildProduct == "sdk"
                || buildProduct == "google_sdk"
                || buildProduct == "sdk_x86"
                || buildProduct == "vbox86p"
                || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
                || buildHardware.lowercase(Locale.getDefault()).contains("nox")
                || buildProduct.lowercase(Locale.getDefault()).contains("nox"))
        if (result) return true
        result = result or (Build.BRAND.startsWith("generic") &&
                Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == buildProduct)
        return result
    }

    fun isSIMInserted(context: Context): Boolean {
        return TelephonyManager.SIM_STATE_ABSENT != (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simState
    }




    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    inner class ChromeClient : WebChromeClient() {
        // For Android 5.0
        override fun onShowFileChooser(
            view: WebView,
            filePath: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback!!.onReceiveValue(null)
            }
            mFilePathCallback = filePath
            var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent!!.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.e("ErrorCreatingFile", "Unable to create Image File", ex)
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.absolutePath
                    takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile)
                    )
                } else {
                    takePictureIntent = null
                }
            }
            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = "image/*"
            val intentArray: Array<Intent?> = takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)
            return true
        }

        // openFileChooser for Android 3.0+
        // openFileChooser for Android < 3.0
        fun openFileChooser(uploadMsg: ValueCallback<Uri?>?, acceptType: String? = "") {
            mUploadMessage = uploadMsg
            // Create AndroidExampleFolder at sdcard
            val imageStorageDir = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ), "AndroidExampleFolder"
            )
            if (!imageStorageDir.exists()) {
                imageStorageDir.mkdirs()
            }

            // Create camera captured image file path and name
            val file = File(
                imageStorageDir.toString() + File.separator + "IMG_"
                        + System.currentTimeMillis().toString() + ".jpg"
            )
            mCapturedImageURI = Uri.fromFile(file)

            // Camera capture image intent
            val captureIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"

            // Create file chooser intent
            val chooserIntent = Intent.createChooser(i, "Image Chooser")

            // Set camera intent to file chooser
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent)
            )

            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE)
        }

        //openFileChooser for other Android versions
        fun openFileChooser(
            uploadMsg: ValueCallback<Uri?>?,
            acceptType: String?,
            capture: String?
        ) {
            openFileChooser(uploadMsg, acceptType)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            var results: Array<Uri>? = null

            // Check that the response is a good one
            if (resultCode == AppCompatActivity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = arrayOf(Uri.parse(mCameraPhotoPath))
                    }
                } else {
                    val dataString = data.dataString
                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }
                }
            }
            mFilePathCallback!!.onReceiveValue(results)
            mFilePathCallback = null
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == mUploadMessage) {
                    return
                }
                var result: Uri? = null
                try {
                    result = if (resultCode != AppCompatActivity.RESULT_OK) {
                        null
                    } else {

                        // retrieve from the private variable if the intent is null
                        if (data == null) mCapturedImageURI else data.data
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext, "activity :$e",
                        Toast.LENGTH_LONG
                    ).show()
                }
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        }
        return
    }


    fun vpnActive(context: Context): Boolean {
        //this method doesn't work below API 21
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false
        var vpnInUse = false
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork
            val caps = connectivityManager.getNetworkCapabilities(activeNetwork)
            return caps!!.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        }
        val networks = connectivityManager.allNetworks
        for (i in networks.indices) {
            val caps = connectivityManager.getNetworkCapabilities(networks[i])
            if (caps!!.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                vpnInUse = true
                break
            }
        }
        return vpnInUse
    }

}