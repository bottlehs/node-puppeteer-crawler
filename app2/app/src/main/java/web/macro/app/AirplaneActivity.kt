package web.macro.app

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_airplane.*
import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.android.synthetic.main.activity_log.toolbar
import web.macro.app.service.FloatingClickService

class AirplaneActivity: AppCompatActivity() {
    private val TAG = AirplaneActivity::class.qualifiedName

    // firebase
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var serviceIntent: Intent? = null

    private val PERMISSION_CODE = 110
    private val PERMISSION_AIRPLANE_CODE = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_airplane)

        // set toolbar as support action bar
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = getString(R.string.activity_airplane_title)

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val btnStart: View = findViewById(R.id.btn_start);

        btnStart.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                || Settings.canDrawOverlays(this)) {

                airplaneMode()
                serviceIntent = Intent(
                    this@AirplaneActivity,
                    FloatingClickService::class.java
                )
                startService(serviceIntent)
                // onBackPressed()
            } else {
                askPermission()
                shortToast("You need System Alert Window Permission to do this")
            }
        }

        /*
        var isStart = false;
        serviceIntent?.let {
            isStart = true
        }
        autoClickService?.let {
            isStart = true
        }

        if ( isStart ) {
            btn_start.setImageDrawable(getDrawable(R.drawable.ic_stop))
        } else {
            btn_start.setImageDrawable(getDrawable(R.drawable.ic_play))
        }
        */

        // firebase
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, TAG.toString())
            param(FirebaseAnalytics.Param.SCREEN_NAME, "비행기모드")
        }
    }

    private fun airplaneMode() {
        App.prefs.airplaneMode = "1";

        val display = windowManager.defaultDisplay // in case of Activity
        val size = Point()
        display.getRealSize(size) // or getSize(size)
        val width = size.x
        val height = size.y
        Log.d(TAG, "heightheightheightheight :::" + height);

        var bottomBarHeight = 0
        val resourceIdBottom = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceIdBottom > 0) bottomBarHeight =
            resources.getDimensionPixelSize(resourceIdBottom)

        Log.d(TAG,"bottomBarHeightbottomBarHeight:::::::::::: "+bottomBarHeight)

        val sizeX = height - (bottomBarHeight/2);
        App.prefs.backButtonSizeX = sizeX.toString();

        val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
        startActivity(intent)
    }

    private fun checkAccess(): Boolean {
        val string = getString(R.string.accessibility_service_id)
        val manager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val list = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (id in list) {
            if (string == id.id) {
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        val hasPermission = checkAccess()
        "has access? $hasPermission".logd()
        if (!hasPermission) {
            Log.d(TAG, "권한 받기 실행1");
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !Settings.canDrawOverlays(this)) {
            askPermission()
            Log.d(TAG, "권한 받기 실행2");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun askPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, PERMISSION_CODE)
    }

    override fun onDestroy() {
        super.onDestroy()
        /*
        serviceIntent?.let {
            "stop floating click service".logd()
            stopService(it)
        }
        autoClickService?.let {
            "stop auto click service".logd()
            it.stopSelf()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return it.disableSelf()
            autoClickService = null
        }
        */
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressedonBackPressed");
        App.prefs.airplaneMode = "0";
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}