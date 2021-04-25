package web.macro.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*

class LogsActivity: AppCompatActivity() {
    private val TAG = "LogActivity"

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

        val lgos = ArrayList<DataLogs>()
        lgos.add(DataLogs("date 1","product 1", "purchase 1"));
        lgos.add(DataLogs("date 2","product 2", "purchase 2"));
        lgos.add(DataLogs("date 3","product 3", "purchase 3"));
        lgos.add(DataLogs("date 4","product 4", "purchase 4"));
        lgos.add(DataLogs("date 5","product 5", "purchase 5"));
        lgos.add(DataLogs("date 6","product 6", "purchase 6"));
        lgos.add(DataLogs("date 7","product 7", "purchase 7"));
        lgos.add(DataLogs("date 8","product 8", "purchase 8"));
        lgos.add(DataLogs("date 9","product 9", "purchase 9"));
        lgos.add(DataLogs("date 10","product 10", "purchase 10"));
        lgos.add(DataLogs("date 11","product 11", "purchase 11"));
        lgos.add(DataLogs("date 12","product 12", "purchase 12"));
        lgos.add(DataLogs("date 13","product 13", "purchase 13"));
        lgos.add(DataLogs("date 14","product 14", "purchase 14"));
        lgos.add(DataLogs("date 15","product 15", "purchase 15"));
        lgos.add(DataLogs("date 16","product 16", "purchase 16"));
        lgos.add(DataLogs("date 17","product 17", "purchase 17"));
        lgos.add(DataLogs("date 18","product 18", "purchase 18"));
        lgos.add(DataLogs("date 19","product 19", "purchase 19"));
        lgos.add(DataLogs("date 20","product 20", "purchase 20"));
        lgos.add(DataLogs("date 21","product 21", "purchase 21"));
        lgos.add(DataLogs("date 22","product 22", "purchase 22"));
        lgos.add(DataLogs("date 23","product 23", "purchase 23"));
        lgos.add(DataLogs("date 24","product 24", "purchase 24"));
        lgos.add(DataLogs("date 25","product 25", "purchase 25"));
        lgos.add(DataLogs("date 26","product 26", "purchase 26"));
        lgos.add(DataLogs("date 27","product 27", "purchase 27"));
        lgos.add(DataLogs("date 28","product 28", "purchase 28"));
        lgos.add(DataLogs("date 29","product 29", "purchase 29"));
        lgos.add(DataLogs("date 30","product 30", "purchase 30"));
        lgos.add(DataLogs("date 31","product 31", "purchase 31"));
        lgos.add(DataLogs("date 32","product 32", "purchase 32"));
        lgos.add(DataLogs("date 33","product 33", "purchase 33"));
        lgos.add(DataLogs("date 34","product 34", "purchase 34"));
        lgos.add(DataLogs("date 35","product 35", "purchase 35"));
        lgos.add(DataLogs("date 36","product 36", "purchase 36"));


        val adapter = LogsAdapter(lgos)
        logs_list.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}