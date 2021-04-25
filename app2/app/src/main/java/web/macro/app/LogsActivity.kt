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
        for (i in 1..10000) {
            lgos.add(DataLogs("date "+i,"product "+i, "purchase "+i));
        }

        val adapter = LogsAdapter(lgos)
        logs_list.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}