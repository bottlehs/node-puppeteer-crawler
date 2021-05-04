package web.macro.app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity: AppCompatActivity() {
    private val TAG = "MainActivity"
    var db : AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        val savedLogs = db!!.logsDao().getAll()

        val btnSave: View = findViewById(R.id.btn_save);
        val btnRun: View = findViewById(R.id.btn_run);
        val btnLogs: View = findViewById(R.id.btn_logs);
        val btnClose: View = findViewById(R.id.btn_close);

        btnSave.setOnClickListener(View.OnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Save")
            builder.setMessage("Would you like to save it?")

            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->
                            Toast.makeText(this@MainActivity, "Save", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            builder.setPositiveButton(R.string.positive, listener)
            builder.setNegativeButton(R.string.negative, listener)

            builder.show()
        })
        btnRun.setOnClickListener(View.OnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.activity_main_run_dialog_title)
            builder.setMessage(R.string.activity_main_run_dialog_description)

            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->
                            run()
                    }
                }
            }

            builder.setPositiveButton(R.string.positive, listener)
            builder.setNegativeButton(R.string.negative, listener)
            builder.show()
        })
        btnLogs.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, LogsActivity::class.java)
            startActivity(intent)
        })
        btnClose.setOnClickListener(View.OnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.activity_main_close_dialog_title)
            builder.setMessage(R.string.activity_main_close_dialog_description)

            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->
                            finish()
                    }
                }
            }

            builder.setPositiveButton(R.string.positive, listener)
            builder.setNegativeButton(R.string.negative, listener)
            builder.show()
        })
        Log.i(TAG,"hello1")
        Log.i(TAG,App.prefs.productName)

        App.prefs.productName = "고래상어!!!";
    }

    fun run () {
        var intent = Intent(this, RunActivity::class.java)
        startActivity(intent)
    }
}