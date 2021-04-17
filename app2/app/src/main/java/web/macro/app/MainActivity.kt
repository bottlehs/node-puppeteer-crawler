package web.macro.app

import android.content.DialogInterface
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
        val btnLog: View = findViewById(R.id.btn_log);
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

            builder.setPositiveButton("Positive", listener)
            builder.setNegativeButton("Negative", listener)

            builder.show()
        })
        btnRun.setOnClickListener(View.OnClickListener {
            Toast.makeText(this@MainActivity, "Run", Toast.LENGTH_SHORT).show()
        })
        btnLog.setOnClickListener(View.OnClickListener {
            Toast.makeText(this@MainActivity, "Run", Toast.LENGTH_SHORT).show()
        })
        btnClose.setOnClickListener(View.OnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Quit")
            builder.setMessage("Are you sure that you want to quit the program?")

            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->
                            finish()
                    }
                }
            }

            builder.setPositiveButton("Positive", listener)
            builder.setNegativeButton("Negative", listener)

            builder.show()
        })
    }
}