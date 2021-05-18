package web.macro.app

import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity: AppCompatActivity() {
    private val TAG = MainActivity::class.qualifiedName
    var checkTxt = true

    private val filepath = "txtFileStorage"
    private var appExternalFile: File?=null
    private val isExternalStorageReadOnly: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)
    }
    private val isExternalStorageAvailable: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED.equals(extStorageState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isExternalStorageAvailable || isExternalStorageReadOnly) {
            Toast.makeText(
                this@MainActivity,
                "Please check search.txt, address.txt",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        // 셋팅
        val searchTxtFilePath = "search.txt"
        writeSearchTextToFile(searchTxtFilePath)
        readSearchTextFromFile(searchTxtFilePath)

        val addressTxtFilePath = "address.txt"
        writeAddressTextToFile(addressTxtFilePath)
        readAddressTextFromFile(addressTxtFilePath)

        if ( !checkTxt ) {
            Toast.makeText(
                this@MainActivity,
                "Please check search.txt, address.txt",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        val airplaneMode: View = findViewById(R.id.airplaneMode);
        airplaneMode.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, AirplaneActivity::class.java)
            startActivity(intent)
        })

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
                            saveForm(false)
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
                            saveForm(true)
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

        val time11 : TextView = findViewById(R.id.time11) as TextView;
        val time12 : TextView = findViewById(R.id.time12) as TextView;
        val time21 : TextView = findViewById(R.id.time21) as TextView;
        val time22 : TextView = findViewById(R.id.time22) as TextView;
        val time31 : TextView = findViewById(R.id.time31) as TextView;
        val time32 : TextView = findViewById(R.id.time32) as TextView;
        val time41 : TextView = findViewById(R.id.time41) as TextView;
        val time42 : TextView = findViewById(R.id.time42) as TextView;

        if ( 0 < App.prefs.time1.toString().length ) {
            val temp = App.prefs.time1.toString().split("/")
            setTime11(temp.get(0).toString())
            setTime12(temp.get(1).toString())
        } else {
            setTime11("-:-")
            setTime12("-:-")
        }

        if ( 0 < App.prefs.time2.toString().length ) {
            val temp = App.prefs.time2.toString().split("/")
            setTime21(temp.get(0).toString())
            setTime22(temp.get(1).toString())
        } else {
            setTime21("-:-")
            setTime22("-:-")
        }

        if ( 0 < App.prefs.time3.toString().length ) {
            val temp = App.prefs.time3.toString().split("/")
            setTime31(temp.get(0).toString())
            setTime32(temp.get(1).toString())
        } else {
            setTime31("-:-")
            setTime32("-:-")
        }

        if ( 0 < App.prefs.time4.toString().length ) {
            val temp = App.prefs.time4.toString().split("/")
            setTime41(temp.get(0).toString())
            setTime42(temp.get(1).toString())
        } else {
            setTime41("-:-")
            setTime42("-:-")
        }

        time11.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                setTime11(SimpleDateFormat("HH:mm").format(cal.time));
            }

            var hour = cal.get(Calendar.HOUR_OF_DAY);
            var minute = cal.get(Calendar.MINUTE);
            if (time11.text.toString() != "-:-") {
                val timeTemp = time11.text.toString().toString().split(":");
                hour = timeTemp.get(0).toInt();
                minute = timeTemp.get(1).toInt();
            }

            TimePickerDialog(
                this,
                timeSetListener,
                hour,
                minute,
                true
            ).show()
        })

        time12.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                setTime12(SimpleDateFormat("HH:mm").format(cal.time));
            }

            var hour = cal.get(Calendar.HOUR_OF_DAY);
            var minute = cal.get(Calendar.MINUTE);
            if (time12.text.toString() != "-:-") {
                val timeTemp = time12.text.toString().toString().split(":");
                hour = timeTemp.get(0).toInt();
                minute = timeTemp.get(1).toInt();
            }

            TimePickerDialog(
                this,
                timeSetListener,
                hour,
                minute,
                true
            ).show()
        })

        time21.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                setTime21(SimpleDateFormat("HH:mm").format(cal.time));
            }

            var hour = cal.get(Calendar.HOUR_OF_DAY);
            var minute = cal.get(Calendar.MINUTE);
            if (time21.text.toString() != "-:-") {
                val timeTemp = time21.text.toString().toString().split(":");
                hour = timeTemp.get(0).toInt();
                minute = timeTemp.get(1).toInt();
            }

            TimePickerDialog(
                this,
                timeSetListener,
                hour,
                minute,
                true
            ).show()
        })

        time22.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                setTime22(SimpleDateFormat("HH:mm").format(cal.time));
            }

            var hour = cal.get(Calendar.HOUR_OF_DAY);
            var minute = cal.get(Calendar.MINUTE);
            if (time22.text.toString() != "-:-") {
                val timeTemp = time22.text.toString().toString().split(":");
                hour = timeTemp.get(0).toInt();
                minute = timeTemp.get(1).toInt();
            }

            TimePickerDialog(
                this,
                timeSetListener,
                hour,
                minute,
                true
            ).show()
        })

        time31.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                setTime31(SimpleDateFormat("HH:mm").format(cal.time));
            }

            var hour = cal.get(Calendar.HOUR_OF_DAY);
            var minute = cal.get(Calendar.MINUTE);
            if (time31.text.toString() != "-:-") {
                val timeTemp = time31.text.toString().toString().split(":");
                hour = timeTemp.get(0).toInt();
                minute = timeTemp.get(1).toInt();
            }

            TimePickerDialog(
                this,
                timeSetListener,
                hour,
                minute,
                true
            ).show()
        })

        time32.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                setTime32(SimpleDateFormat("HH:mm").format(cal.time));
            }

            var hour = cal.get(Calendar.HOUR_OF_DAY);
            var minute = cal.get(Calendar.MINUTE);
            if (time32.text.toString() != "-:-") {
                val timeTemp = time32.text.toString().toString().split(":");
                hour = timeTemp.get(0).toInt();
                minute = timeTemp.get(1).toInt();
            }

            TimePickerDialog(
                this,
                timeSetListener,
                hour,
                minute,
                true
            ).show()
        })

        time41.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                setTime41(SimpleDateFormat("HH:mm").format(cal.time));
            }

            var hour = cal.get(Calendar.HOUR_OF_DAY);
            var minute = cal.get(Calendar.MINUTE);
            if (time41.text.toString() != "-:-") {
                val timeTemp = time41.text.toString().toString().split(":");
                hour = timeTemp.get(0).toInt();
                minute = timeTemp.get(1).toInt();
            }

            TimePickerDialog(
                this,
                timeSetListener,
                hour,
                minute,
                true
            ).show()
        })

        time42.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                setTime42(SimpleDateFormat("HH:mm").format(cal.time));
            }

            var hour = cal.get(Calendar.HOUR_OF_DAY);
            var minute = cal.get(Calendar.MINUTE);
            if (time42.text.toString() != "-:-") {
                val timeTemp = time42.text.toString().toString().split(":");
                hour = timeTemp.get(0).toInt();
                minute = timeTemp.get(1).toInt();
            }

            TimePickerDialog(
                this,
                timeSetListener,
                hour,
                minute,
                true
            ).show()
        })

        val purchase1 : TextView = findViewById(R.id.purchase1) as TextView;
        val purchase2 : TextView = findViewById(R.id.purchase2) as TextView;
        val purchase3 : TextView = findViewById(R.id.purchase3) as TextView;
        val purchase4 : TextView = findViewById(R.id.purchase4) as TextView;

        if ( 0 < App.prefs.purchase1.toString().length ) {
            setPurchase1(App.prefs.purchase1.toString())
        } else {
            setPurchase1("-")
        }
        if ( 0 < App.prefs.purchase2.toString().length ) {
            setPurchase2(App.prefs.purchase2.toString())
        } else {
            setPurchase2("-")
        }
        if ( 0 < App.prefs.purchase3.toString().length ) {
            setPurchase3(App.prefs.purchase3.toString())
        } else {
            setPurchase3("-")
        }
        if ( 0 < App.prefs.purchase4.toString().length ) {
            setPurchase4(App.prefs.purchase4.toString())
        } else {
            setPurchase4("-")
        }

        purchase1.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Buy")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            if (purchase1.text.toString() != "-") {
                input.setText(purchase1.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchase1(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        purchase2.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Buy")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            if (purchase2.text.toString() != "-") {
                input.setText(purchase2.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchase2(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        purchase3.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Buy")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            if (purchase3.text.toString() != "-") {
                input.setText(purchase3.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchase3(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        purchase4.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Buy")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            if (purchase4.text.toString() != "-") {
                input.setText(purchase4.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchase4(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        val queue1 : TextView = findViewById(R.id.queue1) as TextView;
        val queue2 : TextView = findViewById(R.id.queue2) as TextView;

        if ( 0 < App.prefs.queue.toString().length ) {
            val temp = App.prefs.queue.toString().split("/")
            setQueue1(temp.get(0).toString())
            setQueue2(temp.get(1).toString())
        } else {
            setQueue1("-")
            setQueue2("-")
        }

        queue1.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Queue(MIN)")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            if (queue1.text.toString() != "-") {
                input.setText(queue1.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which ->
                var temp = input.text.toString();
                if (5 <= temp.toInt() && temp.toInt() <= 30) {
                    setQueue1(input.text.toString())
                } else {
                    Toast.makeText(this@MainActivity, "Failed: 5 ~ 30", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        queue2.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Queue(MAX)")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            if (queue2.text.toString() != "-") {
                input.setText(queue2.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which ->
                var temp = input.text.toString();
                if (5 <= temp.toInt() && temp.toInt() <= 30) {
                    setQueue2(input.text.toString())
                } else {
                    Toast.makeText(this@MainActivity, "Failed: 5 ~ 30", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        val productName : TextView = findViewById(R.id.productName) as TextView;

        productName.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_name_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productName.text.toString() != "-") {
                input.setText(productName.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductName(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productName.toString().length ) {
            setProductName(App.prefs.productName.toString())
        } else {
            setProductName("-")
        }

        val productId : TextView = findViewById(R.id.productId) as TextView;

        productId.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productId.text.toString() != "-") {
                input.setText(productId.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductId(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productId.toString().length ) {
            setProductId(App.prefs.productId.toString())
        } else {
            setProductId("-")
        }

        val purchaseId : TextView = findViewById(R.id.purchaseId) as TextView;

        purchaseId.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_buy_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (purchaseId.text.toString() != "-") {
                input.setText(purchaseId.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchaseId(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.purchaseId.toString().length ) {
            setPurchaseId(App.prefs.purchaseId.toString())
        } else {
            setPurchaseId("-")
        }

        val nextAuto : CheckBox = findViewById(R.id.nextAuto) as CheckBox;

        if ( 0 < App.prefs.nextAuto.toString().length ) {
            if ( App.prefs.nextAuto.toString() == "Y" ) {
                nextAuto.isChecked = true;
            } else {
                nextAuto.isChecked = false;
            }
        } else {
            nextAuto.isChecked = false;
        }
    }

    /*
    fun execute(cmd: String?): Boolean {
        try {
            if (cmd != null && cmd.length > 0) {
                val p = Runtime.getRuntime().exec("su")
                val dos = DataOutputStream(p.outputStream)
                dos.writeBytes(
                    """
                    $cmd
                    
                    """.trimIndent()
                )
                dos.writeBytes("exit\n")
                dos.flush()
                dos.close()
                p.waitFor()
            } else {
                Log.e(TAG, "command is null or empty")
            }
        } catch (ex: IOException) {
            Log.e(TAG, "IOException")
            ex.printStackTrace()
        } catch (ex: SecurityException) {
            Log.e(TAG, "SecurityException")
            ex.printStackTrace()
        } catch (ex: java.lang.Exception) {
            Log.e(TAG, "Generic Exception")
            ex.printStackTrace()
        }
        return false
    }
    */

    // (x, y) in screen coordinates
    private fun createClick(x: Float, y: Float): GestureDescription? {
        // for a single tap a duration of 1 ms is enough
        val DURATION = 1
        val clickPath = Path()
        clickPath.moveTo(x, y)
        val clickStroke = StrokeDescription(clickPath, 0, DURATION.toLong())
        val clickBuilder = GestureDescription.Builder()
        clickBuilder.addStroke(clickStroke)
        return clickBuilder.build()
    }

    fun setTime11(value: String) {
        if ( 0 < value.length ) {
            time11.setText(value)
        } else {
            time11.setText("-:-")
        }
    }

    fun setTime12(value: String) {
        if ( 0 < value.length ) {
            time12.setText(value)
        } else {
            time12.setText("-:-")
        }
    }

    fun setTime21(value: String) {
        if ( 0 < value.length ) {
            time21.setText(value)
        } else {
            time21.setText("-:-")
        }
    }

    fun setTime22(value: String) {
        if ( 0 < value.length ) {
            time22.setText(value)
        } else {
            time22.setText("-:-")
        }
    }

    fun setTime31(value: String) {
        if ( 0 < value.length ) {
            time31.setText(value)
        } else {
            time31.setText("-:-")
        }
    }

    fun setTime32(value: String) {
        if ( 0 < value.length ) {
            time32.setText(value)
        } else {
            time32.setText("-:-")
        }
    }

    fun setTime41(value: String) {
        if ( 0 < value.length ) {
            time41.setText(value)
        } else {
            time41.setText("-:-")
        }
    }

    fun setTime42(value: String) {
        if ( 0 < value.length ) {
            time42.setText(value)
        } else {
            time42.setText("-:-")
        }
    }

    fun setPurchase1(value: String) {
        if ( 0 < value.length ) {
            purchase1.setText(value)
        } else {
            purchase1.setText("-")
        }
    }

    fun setPurchase2(value: String) {
        if ( 0 < value.length ) {
            purchase2.setText(value)
        } else {
            purchase2.setText("-")
        }
    }

    fun setPurchase3(value: String) {
        if ( 0 < value.length ) {
            purchase3.setText(value)
        } else {
            purchase3.setText("-")
        }
    }

    fun setPurchase4(value: String) {
        if ( 0 < value.length ) {
            purchase4.setText(value)
        } else {
            purchase4.setText("-")
        }
    }

    fun setQueue1(value: String) {
        if ( 0 < value.length ) {
            queue1.setText(value)
        } else {
            queue1.setText("-")
        }
    }

    fun setQueue2(value: String) {
        if ( 0 < value.length ) {
            queue2.setText(value)
        } else {
            queue2.setText("-")
        }
    }

    fun setProductName(value: String) {
        if ( 0 < value.length ) {
            productName.setText(value)
        } else {
            productName.setText("-")
        }
    }

    fun setProductId(value: String) {
        if ( 0 < value.length ) {
            productId.setText(value)
        } else {
            productId.setText("-")
        }
    }

    fun setPurchaseId(value: String) {
        if ( 0 < value.length ) {
            purchaseId.setText(value)
        } else {
            purchaseId.setText("-")
        }
    }

    fun saveForm(isRun: Boolean) {
        Log.i(TAG, "productName.text.toString() : " + productName.text.toString())

        var isSaveValidation =  true;
        var time1Value = "";
        var time2Value = "";
        var time3Value = "";
        var time4Value = "";
        var purchase1Value = ""
        var purchase2Value = ""
        var purchase3Value = ""
        var purchase4Value = ""
        var queueValue = ""
        var productNameValue = ""
        var productIdValue = ""
        var purchaseIdValue = ""

        if ( (0 == time11.text.toString().length || time11.text.toString() == "-:-") || (0 == time12.text.toString().length || time12.text.toString() == "-:-") || purchase1.text.toString() == "-" ) {
            Log.i(TAG, "time1 시간 저장불가능")
            if ( time11.text.toString() != "-:-" || time12.text.toString() != "-:-" || purchase1.text.toString() != "-" ) {
                isSaveValidation = false;
                Toast.makeText(
                    this@MainActivity,
                    "The " + getString(R.string.activity_main_hour_label) + " field is required",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        } else {
            // 저장 가능
            time1Value = time11.text.toString()+"/"+time12.text.toString();
            purchase1Value = purchase1.text.toString();
        }
        if ( (0 == time21.text.toString().length || time21.text.toString() == "-:-") || (0 == time22.text.toString().length || time22.text.toString() == "-:-") || purchase2.text.toString() == "-" ) {
            Log.i(TAG, "time2 시간 저장불가능")
            if ( time21.text.toString() != "-:-" || time22.text.toString() != "-:-" || purchase2.text.toString() != "-" ) {
                isSaveValidation = false;
                Toast.makeText(
                    this@MainActivity,
                    "The " + getString(R.string.activity_main_hour_label) + " field is required",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        } else {
            time2Value = time21.text.toString()+"/"+time22.text.toString();
            purchase2Value = purchase2.text.toString();
        }
        if ( (0 == time31.text.toString().length || time31.text.toString() == "-:-") || (0 == time32.text.toString().length || time32.text.toString() == "-:-") || purchase3.text.toString() == "-" ) {
            Log.i(TAG, "time3 시간 저장불가능")
            if ( time31.text.toString() != "-:-" || time32.text.toString() != "-:-" || purchase3.text.toString() != "-" ) {
                isSaveValidation = false;
                Toast.makeText(
                    this@MainActivity,
                    "The " + getString(R.string.activity_main_hour_label) + " field is required",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        } else {
            time3Value = time31.text.toString()+"/"+time32.text.toString();
            purchase3Value = purchase3.text.toString();
        }
        if ( (0 == time41.text.toString().length || time41.text.toString() == "-:-") || (0 == time42.text.toString().length || time42.text.toString() == "-:-") || purchase4.text.toString() == "-" ) {
            Log.i(TAG, "time4 시간 저장불가능")
            if ( time41.text.toString() != "-:-" || time42.text.toString() != "-:-" || purchase4.text.toString() != "-" ) {
                isSaveValidation = false;
                Toast.makeText(
                    this@MainActivity,
                    "The " + getString(R.string.activity_main_hour_label) + " field is required",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        } else {
            time4Value = time41.text.toString()+"/"+time42.text.toString();
            purchase4Value = purchase4.text.toString();
        }

        if ( (queue1.text.toString().length == 0 || queue1.text.toString() == "-") || (queue2.text.toString().length == 0 || queue2.text.toString() == "-") ) {
            Log.i(TAG, "queue 저장불가능")
            if ( queue1.text.toString() != "-" || queue2.text.toString() != "-" ) {
                isSaveValidation = false;
                Toast.makeText(
                    this@MainActivity,
                    "The " + getString(R.string.activity_main_queue_label) + " field is required",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        } else {
            queueValue = queue1.text.toString()+"/"+queue2.text.toString();
        }
        if ( productName.text.toString().length == 0 || productName.text.toString() == "-" ) {
            isSaveValidation = false;
            Log.i(TAG, "productName 저장불가능")
            Toast.makeText(
                this@MainActivity,
                "The " + getString(R.string.activity_main_product_name_label) + " field is required",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            productNameValue = productName.text.toString();
        }

        if ( productId.text.toString().length == 0 || productId.text.toString() == "-" ) {
            isSaveValidation = false;
            Log.i(TAG, "productId 저장불가능")
            Toast.makeText(
                this@MainActivity,
                "The " + getString(R.string.activity_main_product_link_id_label) + " field is required",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            productIdValue = productId.text.toString();
        }

        if ( purchaseId.text.toString().length == 0 || purchaseId.text.toString() == "-" ) {
            isSaveValidation = false;
            Log.i(TAG, "purchaseId 저장불가능")
            Toast.makeText(
                this@MainActivity,
                "The " + getString(R.string.activity_main_product_buy_link_id_label) + " field is required",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            purchaseIdValue = purchaseId.text.toString();
        }

        if ( isSaveValidation ) {
            if ( 0 < time1Value.length && 0 < purchase1Value.length ) {
                App.prefs.time1 = time1Value
                App.prefs.purchase1 = purchase1Value
            }
            if ( 0 < time2Value.length && 0 < purchase2Value.length ) {
                App.prefs.time2 = time2Value
                App.prefs.purchase2 = purchase2Value
            }
            if ( 0 < time3Value.length && 0 < purchase3Value.length ) {
                App.prefs.time3 = time3Value
                App.prefs.purchase3 = purchase3Value
            }
            if ( 0 < time4Value.length && 0 < purchase4Value.length ) {
                App.prefs.time4 = time4Value
                App.prefs.purchase4 = purchase4Value
            }
            if ( 0 < queueValue.length ) {
                App.prefs.queue = queueValue
            }
            if ( 0 < productNameValue.length ) {
                App.prefs.productName = productNameValue
            }
            if ( 0 < productIdValue.length ) {
                App.prefs.productId = productIdValue
            }
            if ( 0 < purchaseIdValue.length ) {
                App.prefs.purchaseId = purchaseIdValue
            }

            if ( nextAuto.isChecked ) {
                App.prefs.nextAuto = "Y"
            } else {
                App.prefs.nextAuto = "N"
            }

            Toast.makeText(this@MainActivity, "Save Success", Toast.LENGTH_SHORT).show()

            if ( isRun ) {
                run()
            }
        } else {
            Toast.makeText(this@MainActivity, "Save failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun writeSearchTextToFile(path: String) {
        appExternalFile = File(getExternalFilesDir(filepath), path)
        try {
            val file = File(getExternalFilesDir(filepath), path)
            if (file.exists()) {
                Log.d(TAG,"writeSearchTextToFile 파일이 존재 한다.");
                //Do something
            } else {
                Log.d(TAG,"writeSearchTextToFile 파일이 존재 안한다.");
                val fileOutPutStream = FileOutputStream(appExternalFile)
                fileOutPutStream.write("해운대,부산,서울,대구,제주도,호수공원".toByteArray())
                fileOutPutStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readSearchTextFromFile(path: String) {
        appExternalFile = File(getExternalFilesDir(filepath), path)

        var fileInputStream =FileInputStream(appExternalFile)
        var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
        var text: String? = null
        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        fileInputStream.close()
        if ( stringBuilder.toString().trim().length == 0 ) {
            checkTxt = false;
        }
    }

    fun writeAddressTextToFile(path: String) {
        appExternalFile = File(getExternalFilesDir(filepath), path)
        try {
            val file = File(getExternalFilesDir(filepath), path)
            if (file.exists()) {
                Log.d(TAG,"writeAddressTextToFile 파일이 존재 한다.");
                //Do something
            } else {
                Log.d(TAG,"writeAddressTextToFile 파일이 존재 안한다.");
                val fileOutPutStream = FileOutputStream(appExternalFile)
                fileOutPutStream.write("신나라1,06035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,bank_81:010-714471-56107:오미라:하나은행:www.hanabank.com\n".toByteArray())
                fileOutPutStream.write("신나라2,06035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,bank_81:010-714471-56107:오미라:하나은행:www.hanabank.com".toByteArray())
                fileOutPutStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readAddressTextFromFile(path: String) {
        appExternalFile = File(getExternalFilesDir(filepath), path)

        var fileInputStream =FileInputStream(appExternalFile)
        var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
        var text: String? = null
        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        fileInputStream.close()
        if ( stringBuilder.toString().trim().length == 0 ) {
            checkTxt = false;
        }
    }

    fun run () {
        val current = LocalDateTime.now()
        val currentDate = current.format(DateTimeFormatter.ISO_LOCAL_DATE);
        App.prefs.playDate = currentDate
        var intent = Intent(this, RunActivity::class.java)
        startActivity(intent)
    }
}