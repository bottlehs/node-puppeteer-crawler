package web.macro.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import web.macro.app.App
import web.macro.app.MainActivity
import web.macro.app.been.Event
import web.macro.app.logd

var autoClickService: AutoClickService? = null

class AutoClickService : AccessibilityService() {

    internal val events = mutableListOf<Event>()
    val service = this;

    override fun onInterrupt() {
        // NO-OP
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // NO-OP
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        "onServiceConnected".logd()
        autoClickService = this
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun click(x: Int, y: Int) {
        "click $x $y".logd()
        Log.d(
            "AutoClickService",
            "airplaneModeairplaneMode click ::::::: " + App.prefs.airplaneMode!!.toInt()
        )
        var airplaneMode = App.prefs.airplaneMode.toString().toInt();
        var backButtonSizeX = App.prefs.backButtonSizeX.toString().toFloat();
        if ( 0 < airplaneMode ) {
            Log.d("AutoClickService", "airplaneModeairplaneMode click ::::::: 클릭")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
            val path = Path()

            if ( airplaneMode < 3 ) {
                airplaneMode = airplaneMode + 1;
                path.moveTo(x.toFloat(), y.toFloat())
            } else {
                airplaneMode = 0;
                service.performGlobalAction(1)
            }

            App.prefs.airplaneMode = airplaneMode.toString();

            val builder = GestureDescription.Builder()
            val gestureDescription = builder
                .addStroke(GestureDescription.StrokeDescription(path, 10, 10))
                .build()
            dispatchGesture(gestureDescription, null, null)
        } else {
            Log.d("AutoClickService", "airplaneModeairplaneMode click ::::::: 클릭 하지 않음")
        }
    }

    fun run(newEvents: MutableList<Event>) {
        events.clear()
        events.addAll(newEvents)
        events.toString().logd()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        val builder = GestureDescription.Builder()
        events.forEach { builder.addStroke(it.onEvent()) }
        dispatchGesture(builder.build(), null, null)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        "AutoClickService onUnbind".logd()
        autoClickService = null
        return super.onUnbind(intent)
    }


    override fun onDestroy() {
        "AutoClickService onDestroy".logd()
        autoClickService = null
        super.onDestroy()
    }
}