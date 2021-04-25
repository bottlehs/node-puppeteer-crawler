package web.macro.app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity: AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            var intent = Intent(this, RunActivity::class.java)
            startActivity(intent)
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
    }
}