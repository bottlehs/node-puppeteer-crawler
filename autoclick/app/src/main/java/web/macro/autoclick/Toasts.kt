package web.macro.autoclick

import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.StringRes

/**
 * Created on 2021.05.09.
 * By bottlehs
 */
private var toast: Toast? = null

private fun Context.showToast(text: String, @Duration length: Int = Toast.LENGTH_SHORT) {
    //先检查是否在主线程中运行，再进行处理
    if (Looper.myLooper() == Looper.getMainLooper()) {
        toast = toast ?: Toast.makeText(this.applicationContext, text, length)
        toast?.let {
            it.setText(text)
            it.duration = length
            it.show()
        }
    } else {
        "show toast run in wrong thread".loge()
    }
}

internal fun Context.errorToast(e: Throwable) {
    showToast(e.localizedMessage)
}

internal fun Context.longToast(text: String) {
    showToast(text, Toast.LENGTH_LONG)
}

internal fun Context.longToast(@StringRes id: Int) {
    showToast(getString(id), Toast.LENGTH_LONG)
}

internal fun Context.shortToast(text: String) {
    showToast(text)
}

internal fun Context.shortToast(@StringRes id: Int) {
    shortToast(getString(id))
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(Toast.LENGTH_SHORT, Toast.LENGTH_LONG)
internal annotation class Duration