package web.macro.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LogActivity: AppCompatActivity() {
    private val TAG = "LogActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
    }
}