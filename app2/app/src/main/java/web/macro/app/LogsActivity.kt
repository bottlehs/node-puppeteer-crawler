package web.macro.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_log.*

class LogsActivity: AppCompatActivity() {
    private val TAG = LogsActivity::class.qualifiedName

    // firebase
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    var db : AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        // set toolbar as support action bar
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = getString(R.string.activity_logs_title)

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        db = AppDatabase.getInstance(this)
        val logs = ArrayList<DataLogs>()

        Log.d(TAG,db!!.logsDao().toString());

        val savedLogs = db!!.logsDao().getAll()
        Log.i(TAG,""+savedLogs.size)
        if(savedLogs.isNotEmpty()){
            savedLogs.forEach{ row ->
                logs.add(DataLogs(row.strDate,row.strProduct, row.strPurchase, row.strIp, row.strAddress, row.strSearch));
            }
        }

        val adapter = LogsAdapter(logs)
        logs_list.adapter = adapter

        /*
        val log = Logs(0,"date","product","purchase")
        db?.logsDao()?.insertAll(log)
        */

        // firebase
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, TAG.toString())
            param(FirebaseAnalytics.Param.SCREEN_NAME, "로그")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}