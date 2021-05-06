package web.macro.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*

class LogsActivity: AppCompatActivity() {
    private val TAG = LogsActivity::class.qualifiedName
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

        val savedLogs = db!!.logsDao().getAll()
        Log.i(TAG,""+savedLogs.size)
        if(savedLogs.isNotEmpty()){
            savedLogs.forEach{ row ->
                logs.add(DataLogs(row.strDate,row.strProduct, row.strPurchase));
            }
        }

        val adapter = LogsAdapter(logs)
        logs_list.adapter = adapter

        /* insert
        val log = Logs(0,"date","product","purchase")
        db?.contactsDao()?.insertAll(log)
        */
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}