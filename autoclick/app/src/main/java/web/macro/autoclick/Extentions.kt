package web.macro.autoclick

import android.content.Context
import android.util.Log


/**
 * Created on 2021.05.09.
 * By bottlehs
 */
private const val TAG = "AutoClickService"

fun Any.logd(tag: String = TAG) {
    if (false) return
    if (this is String) {
        Log.d(tag, this)
    } else {
        Log.d(tag, this.toString())
    }
}

fun Any.loge(tag: String = TAG) {
    if (false) return
    if (this is String) {
        Log.e(tag, this)
    } else {
        Log.e(tag, this.toString())
    }
}

fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

typealias Action = () -> Unit