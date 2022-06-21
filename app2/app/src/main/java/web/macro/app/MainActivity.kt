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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Copyright 2021. BH Macro App All Rights Reserved
 * Apache License
 */

class MainActivity: AppCompatActivity() {

    private val TAG = MainActivity::class.qualifiedName
    var checkTxt = true

    // version
    var versionCode: Int = BuildConfig.VERSION_CODE
    var versionName = BuildConfig.VERSION_NAME

    // firebase
    private lateinit var firebaseAnalytics: FirebaseAnalytics

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
                "1Please check address.txt",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        // 셋팅 (주소) - product 1
        writeAddressTextToFile("product_1_address.txt")
        readAddressTextFromFile("product_1_address.txt")

        // 셋팅 (주소) - product 2
        writeAddressTextToFile("product_2_address.txt")
        readAddressTextFromFile("product_2_address.txt")

        // 셋팅 (주소) - product 3
        writeAddressTextToFile("product_3_address.txt")
        readAddressTextFromFile("product_3_address.txt")

        // 셋팅 (주소) - product 4
        writeAddressTextToFile("product_4_address.txt")
        readAddressTextFromFile("product_4_address.txt")

        // 셋팅 (주소) - product 5
        writeAddressTextToFile("product_5_address.txt")
        readAddressTextFromFile("product_5_address.txt")

        // 셋팅 (주소) - product 6
        writeAddressTextToFile("product_6_address.txt")
        readAddressTextFromFile("product_6_address.txt")

        if ( !checkTxt ) {
            Toast.makeText(
                this@MainActivity,
                "2Please check address.txt",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        val appVersion: TextView = findViewById(R.id.appVersion);
        appVersion.setText(versionName+"("+versionCode+")");

        val airplaneMode: View = findViewById(R.id.airplaneMode);
        airplaneMode.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, AirplaneActivity::class.java)
            startActivity(intent)
        })

        val btnExecutionCdoeGenerate: View = findViewById(R.id.btn_execution_cdoe_generate);
        btnExecutionCdoeGenerate.setOnClickListener(View.OnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Execution Code")
            builder.setMessage("Do you want to create an execution code?")

            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->
                            saveExecutionCdoe();
                    }
                }
            }

            builder.setPositiveButton(R.string.positive, listener)
            builder.setNegativeButton(R.string.negative, listener)

            builder.show()
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

        if ( 0 < App.prefs.executionCdoe.toString().length ) {
            setExecutionCdoe(App.prefs.executionCdoe.toString())
        } else {
            this.saveExecutionCdoe();
        }

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

        // productName
        val productName1 : TextView = findViewById(R.id.productName1) as TextView;
        val productName2 : TextView = findViewById(R.id.productName2) as TextView;
        val productName3 : TextView = findViewById(R.id.productName3) as TextView;
        val productName4 : TextView = findViewById(R.id.productName4) as TextView;
        val productName5 : TextView = findViewById(R.id.productName5) as TextView;
        val productName6 : TextView = findViewById(R.id.productName6) as TextView;

        // productName > productName1
        productName1.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_name_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productName1.text.toString() != "-") {
                input.setText(productName1.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductName1(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productName1.toString().length ) {
            setProductName1(App.prefs.productName1.toString())
        } else {
            setProductName1("-")
        }

        // productName > productName2
        productName2.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_name_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productName2.text.toString() != "-") {
                input.setText(productName2.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductName2(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productName2.toString().length ) {
            setProductName2(App.prefs.productName2.toString())
        } else {
            setProductName2("-")
        }

        // productName > productName3
        productName3.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_name_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productName3.text.toString() != "-") {
                input.setText(productName3.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductName3(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productName3.toString().length ) {
            setProductName3(App.prefs.productName3.toString())
        } else {
            setProductName3("-")
        }

        // productName > productName4
        productName4.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_name_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productName4.text.toString() != "-") {
                input.setText(productName4.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductName4(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productName4.toString().length ) {
            setProductName4(App.prefs.productName4.toString())
        } else {
            setProductName4("-")
        }

        // productName > productName5
        productName5.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_name_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productName5.text.toString() != "-") {
                input.setText(productName5.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductName5(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productName5.toString().length ) {
            setProductName5(App.prefs.productName5.toString())
        } else {
            setProductName5("-")
        }

        // productName > productName6
        productName6.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_name_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productName6.text.toString() != "-") {
                input.setText(productName6.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductName6(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productName6.toString().length ) {
            setProductName6(App.prefs.productName6.toString())
        } else {
            setProductName6("-")
        }

        // productId
        val productId1 : TextView = findViewById(R.id.productId1) as TextView;
        val productId2 : TextView = findViewById(R.id.productId2) as TextView;
        val productId3 : TextView = findViewById(R.id.productId3) as TextView;
        val productId4 : TextView = findViewById(R.id.productId4) as TextView;
        val productId5 : TextView = findViewById(R.id.productId5) as TextView;
        val productId6 : TextView = findViewById(R.id.productId6) as TextView;

        // productId > productId1
        productId1.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productId1.text.toString() != "-") {
                input.setText(productId1.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductId1(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productId1.toString().length ) {
            setProductId1(App.prefs.productId1.toString())
        } else {
            setProductId1("-")
        }

        // productId > productId2
        productId2.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productId2.text.toString() != "-") {
                input.setText(productId2.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductId2(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productId2.toString().length ) {
            setProductId2(App.prefs.productId2.toString())
        } else {
            setProductId2("-")
        }

        // productId > productId3
        productId3.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productId3.text.toString() != "-") {
                input.setText(productId3.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductId3(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productId3.toString().length ) {
            setProductId3(App.prefs.productId3.toString())
        } else {
            setProductId3("-")
        }

        // productId > productId4
        productId4.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productId4.text.toString() != "-") {
                input.setText(productId4.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductId4(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productId4.toString().length ) {
            setProductId4(App.prefs.productId4.toString())
        } else {
            setProductId4("-")
        }

        // productId > productId5
        productId5.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productId5.text.toString() != "-") {
                input.setText(productId5.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductId5(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productId5.toString().length ) {
            setProductId5(App.prefs.productId5.toString())
        } else {
            setProductId5("-")
        }

        // productId > productId6
        productId6.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (productId6.text.toString() != "-") {
                input.setText(productId6.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setProductId6(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.productId6.toString().length ) {
            setProductId6(App.prefs.productId6.toString())
        } else {
            setProductId6("-")
        }

        // purchaseId
        val purchaseId1 : TextView = findViewById(R.id.purchaseId1) as TextView;
        val purchaseId2 : TextView = findViewById(R.id.purchaseId2) as TextView;
        val purchaseId3 : TextView = findViewById(R.id.purchaseId3) as TextView;
        val purchaseId4 : TextView = findViewById(R.id.purchaseId4) as TextView;
        val purchaseId5 : TextView = findViewById(R.id.purchaseId5) as TextView;
        val purchaseId6 : TextView = findViewById(R.id.purchaseId6) as TextView;

        // purchaseId > purchaseId1
        purchaseId1.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_buy_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (purchaseId1.text.toString() != "-") {
                input.setText(purchaseId1.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchaseId1(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.purchaseId1.toString().length ) {
            setPurchaseId1(App.prefs.purchaseId1.toString())
        } else {
            setPurchaseId1("-")
        }

        // purchaseId > purchaseId2
        purchaseId2.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_buy_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (purchaseId2.text.toString() != "-") {
                input.setText(purchaseId2.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchaseId2(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.purchaseId2.toString().length ) {
            setPurchaseId2(App.prefs.purchaseId2.toString())
        } else {
            setPurchaseId2("-")
        }

        // purchaseId > purchaseId3
        purchaseId3.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_buy_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (purchaseId3.text.toString() != "-") {
                input.setText(purchaseId3.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchaseId3(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.purchaseId3.toString().length ) {
            setPurchaseId3(App.prefs.purchaseId3.toString())
        } else {
            setPurchaseId3("-")
        }

        // purchaseId > purchaseId4
        purchaseId4.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_buy_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (purchaseId4.text.toString() != "-") {
                input.setText(purchaseId4.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchaseId4(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.purchaseId4.toString().length ) {
            setPurchaseId4(App.prefs.purchaseId4.toString())
        } else {
            setPurchaseId4("-")
        }

        // purchaseId > purchaseId5
        purchaseId5.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_buy_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (purchaseId5.text.toString() != "-") {
                input.setText(purchaseId5.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchaseId5(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.purchaseId5.toString().length ) {
            setPurchaseId5(App.prefs.purchaseId5.toString())
        } else {
            setPurchaseId5("-")
        }

        // purchaseId > purchaseId6
        purchaseId6.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.activity_main_product_buy_link_id_label))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (purchaseId6.text.toString() != "-") {
                input.setText(purchaseId6.text.toString())
            }

            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setPurchaseId6(input.text.toString()) }
            builder.setNegativeButton(
                getString(R.string.negative)
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        })

        if ( 0 < App.prefs.purchaseId6.toString().length ) {
            setPurchaseId6(App.prefs.purchaseId6.toString())
        } else {
            setPurchaseId6("-")
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

        // firebase
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, TAG.toString())
            param(FirebaseAnalytics.Param.SCREEN_NAME, "설정")
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

    fun saveExecutionCdoe() {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        var code = (1..5)
            .map { charset.random() }
            .joinToString("");

        executionCdoe.setText(code)
    }

    fun setExecutionCdoe(value: String) {
        if ( 0 < value.length ) {
            executionCdoe.setText(value)
        } else {
            executionCdoe.setText("-")
        }
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

    // setProductName
    // setProductName > setProductName1
    fun setProductName1(value: String) {
        if ( 0 < value.length ) {
            productName1.setText(value)
        } else {
            productName1.setText("-")
        }
    }

    // setProductName > setProductName2
    fun setProductName2(value: String) {
        if ( 0 < value.length ) {
            productName2.setText(value)
        } else {
            productName2.setText("-")
        }
    }

    // setProductName > setProductName3
    fun setProductName3(value: String) {
        if ( 0 < value.length ) {
            productName3.setText(value)
        } else {
            productName3.setText("-")
        }
    }

    // setProductName > setProductName4
    fun setProductName4(value: String) {
        if ( 0 < value.length ) {
            productName4.setText(value)
        } else {
            productName4.setText("-")
        }
    }

    // setProductName > setProductName5
    fun setProductName5(value: String) {
        if ( 0 < value.length ) {
            productName5.setText(value)
        } else {
            productName5.setText("-")
        }
    }

    // setProductName > setProductName6
    fun setProductName6(value: String) {
        if ( 0 < value.length ) {
            productName6.setText(value)
        } else {
            productName6.setText("-")
        }
    }

    // setProductId
    // setProductId > setProductId1
    fun setProductId1(value: String) {
        if ( 0 < value.length ) {
            productId1.setText(value)
        } else {
            productId1.setText("-")
        }
    }

    // setProductId > setProductId2
    fun setProductId2(value: String) {
        if ( 0 < value.length ) {
            productId2.setText(value)
        } else {
            productId2.setText("-")
        }
    }

    // setProductId > setProductId3
    fun setProductId3(value: String) {
        if ( 0 < value.length ) {
            productId3.setText(value)
        } else {
            productId3.setText("-")
        }
    }

    // setProductId > setProductId4
    fun setProductId4(value: String) {
        if ( 0 < value.length ) {
            productId4.setText(value)
        } else {
            productId4.setText("-")
        }
    }

    // setProductId > setProductId5
    fun setProductId5(value: String) {
        if ( 0 < value.length ) {
            productId5.setText(value)
        } else {
            productId5.setText("-")
        }
    }

    // setProductId > setProductId6
    fun setProductId6(value: String) {
        if ( 0 < value.length ) {
            productId6.setText(value)
        } else {
            productId6.setText("-")
        }
    }

    // setPurchaseId
    // setPurchaseId > setPurchaseId1
    fun setPurchaseId1(value: String) {
        if ( 0 < value.length ) {
            purchaseId1.setText(value)
        } else {
            purchaseId1.setText("-")
        }
    }

    // setPurchaseId > setPurchaseId1
    fun setPurchaseId2(value: String) {
        if ( 0 < value.length ) {
            purchaseId2.setText(value)
        } else {
            purchaseId2.setText("-")
        }
    }

    // setPurchaseId > setPurchaseId1
    fun setPurchaseId3(value: String) {
        if ( 0 < value.length ) {
            purchaseId3.setText(value)
        } else {
            purchaseId3.setText("-")
        }
    }

    // setPurchaseId > setPurchaseId1
    fun setPurchaseId4(value: String) {
        if ( 0 < value.length ) {
            purchaseId4.setText(value)
        } else {
            purchaseId4.setText("-")
        }
    }

    // setPurchaseId > setPurchaseId1
    fun setPurchaseId5(value: String) {
        if ( 0 < value.length ) {
            purchaseId5.setText(value)
        } else {
            purchaseId5.setText("-")
        }
    }

    // setPurchaseId > setPurchaseId1
    fun setPurchaseId6(value: String) {
        if ( 0 < value.length ) {
            purchaseId6.setText(value)
        } else {
            purchaseId6.setText("-")
        }
    }

    fun saveForm(isRun: Boolean) {
        Log.i(TAG, "productName.text.toString() : " + productName1.text.toString())

        var executionCdoeValue = "";

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

        // product 1
        var productName1Value = ""
        var productId1Value = ""
        var purchaseId1Value = ""

        // product 2
        var productName2Value = ""
        var productId2Value = ""
        var purchaseId2Value = ""

        // product 3
        var productName3Value = ""
        var productId3Value = ""
        var purchaseId3Value = ""

        // product 4
        var productName4Value = ""
        var productId4Value = ""
        var purchaseId4Value = ""

        // product 5
        var productName5Value = ""
        var productId5Value = ""
        var purchaseId5Value = ""

        // product 6
        var productName6Value = ""
        var productId6Value = ""
        var purchaseId6Value = ""

        // executionCdoe
        var isExecutionCdoe = true;
        if ( executionCdoe.text.toString().length == 0 || executionCdoe.text.toString() == "-" ) {
            isExecutionCdoe = false;
        } else {
            executionCdoeValue = executionCdoe.text.toString();
        }

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

        // product 1
        var isProduct1 = true;
        if ( productName1.text.toString().length == 0 || productName1.text.toString() == "-" ) {
            isProduct1 = false;
        } else {
            productName1Value = productName1.text.toString();
        }

        if ( productId1.text.toString().length == 0 || productId1.text.toString() == "-" ) {
            isProduct1 = false;
        } else {
            productId1Value = productId1.text.toString();
        }

        if ( purchaseId1.text.toString().length == 0 || purchaseId1.text.toString() == "-" ) {
            isProduct1 = false;
        } else {
            purchaseId1Value = purchaseId1.text.toString();
        }

        // product 2
        var isProduct2 = true;
        if ( productName2.text.toString().length == 0 || productName2.text.toString() == "-" ) {
            isProduct2 = false;
        } else {
            productName2Value = productName2.text.toString();
        }

        if ( productId2.text.toString().length == 0 || productId2.text.toString() == "-" ) {
            isProduct2 = false;
        } else {
            productId2Value = productId2.text.toString();
        }

        if ( purchaseId2.text.toString().length == 0 || purchaseId2.text.toString() == "-" ) {
            isProduct2 = false;
        } else {
            purchaseId2Value = purchaseId2.text.toString();
        }

        // product 3
        var isProduct3 = true;
        if ( productName3.text.toString().length == 0 || productName3.text.toString() == "-" ) {
            isProduct3 = false;
        } else {
            productName3Value = productName3.text.toString();
        }

        if ( productId3.text.toString().length == 0 || productId3.text.toString() == "-" ) {
            isProduct3 = false;
        } else {
            productId3Value = productId3.text.toString();
        }

        if ( purchaseId3.text.toString().length == 0 || purchaseId3.text.toString() == "-" ) {
            isProduct3 = false;
        } else {
            purchaseId3Value = purchaseId3.text.toString();
        }

        // product 4
        var isProduct4 = true;
        if ( productName4.text.toString().length == 0 || productName4.text.toString() == "-" ) {
            isProduct4 = false;
        } else {
            productName4Value = productName4.text.toString();
        }

        if ( productId4.text.toString().length == 0 || productId4.text.toString() == "-" ) {
            isProduct4 = false;
        } else {
            productId4Value = productId4.text.toString();
        }

        if ( purchaseId4.text.toString().length == 0 || purchaseId4.text.toString() == "-" ) {
            isProduct4 = false;
        } else {
            purchaseId4Value = purchaseId4.text.toString();
        }

        // product 5
        var isProduct5 = true;
        if ( productName5.text.toString().length == 0 || productName5.text.toString() == "-" ) {
            isProduct5 = false;
        } else {
            productName5Value = productName5.text.toString();
        }

        if ( productId5.text.toString().length == 0 || productId5.text.toString() == "-" ) {
            isProduct5 = false;
        } else {
            productId5Value = productId5.text.toString();
        }

        if ( purchaseId5.text.toString().length == 0 || purchaseId5.text.toString() == "-" ) {
            isProduct5 = false;
        } else {
            purchaseId5Value = purchaseId5.text.toString();
        }

        // product 6
        var isProduct6 = true;
        if ( productName6.text.toString().length == 0 || productName6.text.toString() == "-" ) {
            isProduct6 = false;
        } else {
            productName6Value = productName6.text.toString();
        }

        if ( productId6.text.toString().length == 0 || productId6.text.toString() == "-" ) {
            isProduct6 = false;
        } else {
            productId6Value = productId6.text.toString();
        }

        if ( purchaseId6.text.toString().length == 0 || purchaseId6.text.toString() == "-" ) {
            isProduct6 = false;
        } else {
            purchaseId6Value = purchaseId6.text.toString();
        }

        if ( (isProduct1 || isProduct2 || isProduct3 || isProduct4 || isProduct5 || isProduct6) && isSaveValidation && isExecutionCdoe ) {
            isSaveValidation = true;
        } else {
            Toast.makeText(
                this@MainActivity,
                "The " + getString(R.string.activity_main_product_name_label) + " field is required",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // isSaveValidation
        if ( isSaveValidation ) {
            if ( 0 < executionCdoeValue.length) {
                App.prefs.executionCdoe = executionCdoeValue
            }

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

            // product 1
            App.prefs.productName1 = "";
            App.prefs.productId1 = ""
            App.prefs.purchaseId1 = ""
            if ( 0 < productName1Value.length ) {
                App.prefs.productName1 = productName1Value
            }
            if ( 0 < productId1Value.length ) {
                App.prefs.productId1 = productId1Value
            }
            if ( 0 < purchaseId1Value.length ) {
                App.prefs.purchaseId1 = purchaseId1Value
            }

            // product 2
            App.prefs.productName2 = "";
            App.prefs.productId2 = ""
            App.prefs.purchaseId2 = ""
            if ( 0 < productName2Value.length ) {
                App.prefs.productName2 = productName2Value
            }
            if ( 0 < productId2Value.length ) {
                App.prefs.productId2 = productId2Value
            }
            if ( 0 < purchaseId2Value.length ) {
                App.prefs.purchaseId2 = purchaseId2Value
            }

            // product 3
            App.prefs.productName3 = "";
            App.prefs.productId3 = ""
            App.prefs.purchaseId3 = ""
            if ( 0 < productName3Value.length ) {
                App.prefs.productName3 = productName3Value
            }
            if ( 0 < productId3Value.length ) {
                App.prefs.productId3 = productId3Value
            }
            if ( 0 < purchaseId3Value.length ) {
                App.prefs.purchaseId3 = purchaseId3Value
            }

            // product 4
            App.prefs.productName4 = "";
            App.prefs.productId4 = ""
            App.prefs.purchaseId4 = ""
            if ( 0 < productName4Value.length ) {
                App.prefs.productName4 = productName4Value
            }
            if ( 0 < productId4Value.length ) {
                App.prefs.productId4 = productId4Value
            }
            if ( 0 < purchaseId4Value.length ) {
                App.prefs.purchaseId4 = purchaseId4Value
            }

            // product 5
            App.prefs.productName5 = "";
            App.prefs.productId5 = ""
            App.prefs.purchaseId5 = ""
            if ( 0 < productName5Value.length ) {
                App.prefs.productName5 = productName5Value
            }
            if ( 0 < productId5Value.length ) {
                App.prefs.productId5 = productId5Value
            }
            if ( 0 < purchaseId5Value.length ) {
                App.prefs.purchaseId5 = purchaseId5Value
            }

            // product 6
            App.prefs.productName6 = "";
            App.prefs.productId6 = ""
            App.prefs.purchaseId6 = ""
            if ( 0 < productName6Value.length ) {
                App.prefs.productName6 = productName6Value
            }
            if ( 0 < productId6Value.length ) {
                App.prefs.productId6 = productId6Value
            }
            if ( 0 < purchaseId6Value.length ) {
                App.prefs.purchaseId6 = purchaseId6Value
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

                if ( path.equals("product_1_address.txt") ) {
                    fileOutPutStream.write("신나라1,06035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,bank_03:92300708301012:유현목:기업은행:www.ibk.co.kr\n".toByteArray())
                    fileOutPutStream.write("신나라2,06035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,bank_03:92300708301012:유현목:기업은행:www.ibk.co.kr".toByteArray())
                } else if ( path.equals("product_2_address.txt") ) {
                    fileOutPutStream.write("신나라1,06035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,bank_04:265701-04-355542:신성수(애드닷컴):국민은행:www.kbstar.com\n".toByteArray())
                    fileOutPutStream.write("신나라2,06035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,bank_04:265701-04-355542:신성수(애드닷컴):국민은행:www.kbstar.com".toByteArray())
                } else if ( path.equals("product_3_address.txt") ) {
                    fileOutPutStream.write("신나라1,06035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,bank_03:92300770404032:주식회사 아리상사:기업은행:www.ibk.co.kr\n".toByteArray())
                    fileOutPutStream.write("신나라2,06035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,bank_03:92300770404032:주식회사 아리상사:기업은행:www.ibk.co.kr".toByteArray())
                } else {
                    fileOutPutStream.write("신나라1,06035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,bank_03:92300708301012:유현목:기업은행:www.ibk.co.kr\n".toByteArray())
                    fileOutPutStream.write("신나라2,06035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,bank_03:92300708301012:유현목:기업은행:www.ibk.co.kr".toByteArray())
                }
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