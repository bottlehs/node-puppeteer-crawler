package web.macro.app

import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
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
import java.util.*


class MainActivity: AppCompatActivity() {
    private val TAG = "MainActivity"
    var db : AppDatabase? = null
    var checkTxt = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 셋팅
        val searchTxtFilePath = filesDir.path+"/search.txt"
        writeSearchTextToFile(searchTxtFilePath)
        readSearchTextFromFile(searchTxtFilePath)

        val addressTxtFilePath = filesDir.path+"/address.txt"
        writeAddressTextToFile(addressTxtFilePath)
        readAddressTextFromFile(addressTxtFilePath)

        Log.i(TAG,"checkTxt : "+checkTxt)

        if ( !checkTxt ) {
            Toast.makeText(this@MainActivity, "Please check search.txt, address.txt", Toast.LENGTH_SHORT).show()
            finish()
        }


        db = AppDatabase.getInstance(this)
        val savedLogs = db!!.logsDao().getAll()

        val btnSave: View = findViewById(R.id.btn_save);
        val btnRun: View = findViewById(R.id.btn_run);
        val btnLogs: View = findViewById(R.id.btn_logs);
        val btnClose: View = findViewById(R.id.btn_close);

        btnSave.setOnClickListener(View.OnClickListener {
            if ( App.prefs.productName.toString().length != 0 && App.prefs.productId.toString().length != 0 && App.prefs.purchaseId.toString().length != 0 ) {
                var builder = AlertDialog.Builder(this)
                builder.setTitle("Save")
                builder.setMessage("Would you like to save it?")

                var listener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when (p1) {
                            DialogInterface.BUTTON_POSITIVE ->
                                saveForm()
                        }
                    }
                }

                builder.setPositiveButton(R.string.positive, listener)
                builder.setNegativeButton(R.string.negative, listener)

                builder.show()
            } else {
                if ( App.prefs.productName.toString().length == 0 ) {
                    Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_product_name_label)+" field is required", Toast.LENGTH_SHORT).show()
                } else if ( App.prefs.productId.toString().length == 0 ) {
                    Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_product_link_id_label)+" field is required", Toast.LENGTH_SHORT).show()
                } else if ( App.prefs.purchaseId.toString().length == 0 ) {
                    Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_product_buy_link_id_label)+" field is required", Toast.LENGTH_SHORT).show()
                }
            }
        })
        btnRun.setOnClickListener(View.OnClickListener {
            run()
            /*
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
            */
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
        Log.i(TAG, "hello1")
        Log.i(TAG, App.prefs.productName)

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
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
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
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
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
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
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
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
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
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
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
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
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
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
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
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
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
            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setQueue1(input.text.toString()) }
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
            builder.setView(input)
            builder.setPositiveButton(
                getString(R.string.positive)
            ) { dialog, which -> setQueue2(input.text.toString()) }
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

    fun saveForm() {
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
                Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_hour_label)+" field is required", Toast.LENGTH_SHORT).show()
                isSaveValidation = false;
            }
        } else {
            // 저장 가능
            time1Value = time11.text.toString()+"/"+time12.text.toString();
            purchase1Value = purchase1.text.toString();
        }
        if ( (0 == time21.text.toString().length || time21.text.toString() == "-:-") || (0 == time22.text.toString().length || time22.text.toString() == "-:-") || purchase2.text.toString() == "-" ) {
            Log.i(TAG, "time2 시간 저장불가능")
            if ( time21.text.toString() != "-:-" || time22.text.toString() != "-:-" || purchase2.text.toString() != "-" ) {
                Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_hour_label)+" field is required", Toast.LENGTH_SHORT).show()
                isSaveValidation = false;
            }
        } else {
            time2Value = time21.text.toString()+"/"+time22.text.toString();
            purchase2Value = purchase2.text.toString();
        }
        if ( (0 == time31.text.toString().length || time31.text.toString() == "-:-") || (0 == time32.text.toString().length || time32.text.toString() == "-:-") || purchase3.text.toString() == "-" ) {
            Log.i(TAG, "time3 시간 저장불가능")
            if ( time31.text.toString() != "-:-" || time32.text.toString() != "-:-" || purchase3.text.toString() != "-" ) {
                Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_hour_label)+" field is required", Toast.LENGTH_SHORT).show()
                isSaveValidation = false;
            }
        } else {
            time3Value = time31.text.toString()+"/"+time32.text.toString();
            purchase3Value = purchase3.text.toString();
        }
        if ( (0 == time41.text.toString().length || time41.text.toString() == "-:-") || (0 == time42.text.toString().length || time42.text.toString() == "-:-") || purchase4.text.toString() == "-" ) {
            Log.i(TAG, "time4 시간 저장불가능")
            if ( time41.text.toString() != "-:-" || time42.text.toString() != "-:-" || purchase4.text.toString() != "-" ) {
                isSaveValidation = false;
                Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_hour_label)+" field is required", Toast.LENGTH_SHORT).show()
            }
        } else {
            time4Value = time41.text.toString()+"/"+time42.text.toString();
            purchase4Value = purchase4.text.toString();
        }

        if ( (queue1.text.toString().length == 0 || queue1.text.toString() == "-") || (queue2.text.toString().length == 0 || queue2.text.toString() == "-") ) {
            Log.i(TAG, "queue 저장불가능")
            if ( queue1.text.toString() != "-" || queue2.text.toString() != "-" ) {
                isSaveValidation = false;
                Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_queue_label)+" field is required", Toast.LENGTH_SHORT).show()
            }
        } else {
            queueValue = queue1.text.toString()+"/"+queue2.text.toString();
        }

        if ( productName.text.toString().length == 0 || productName.text.toString() == "-" ) {
            isSaveValidation = false;
            Log.i(TAG, "productName 저장불가능")
            Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_product_name_label)+" field is required", Toast.LENGTH_SHORT).show()
        } else {
            productNameValue = productName.text.toString();
        }

        if ( productId.text.toString().length == 0 || productId.text.toString() == "-" ) {
            isSaveValidation = false;
            Log.i(TAG, "productId 저장불가능")
            Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_product_link_id_label)+" field is required", Toast.LENGTH_SHORT).show()
        } else {
            productIdValue = productId.text.toString();
        }

        if ( purchaseId.text.toString().length == 0 || purchaseId.text.toString() == "-" ) {
            isSaveValidation = false;
            Log.i(TAG, "purchaseId 저장불가능")
            Toast.makeText(this@MainActivity, "The "+getString(R.string.activity_main_product_buy_link_id_label)+" field is required", Toast.LENGTH_SHORT).show()
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
        } else {
            // Toast.makeText(this@MainActivity, "Save failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun writeSearchTextToFile(path: String) {
        val file = File(path)
        if ( !file.exists() ) {
            Log.d(TAG,"writeSearchTextToFile 파일이 없으므로 파일을 생성 합니다.")
            val fileWriter = FileWriter(file, false)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.append("해운대,부산,서울,대구,제주도,호수공원")
            bufferedWriter.close()
        }
    }

    fun readSearchTextFromFile(path: String) {
        val file = File(path)
        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)
        var txt = "";
        bufferedReader.readLines().forEach() {
            Log.d(TAG, it)
            txt = txt+it;
        }
        searchTxt.setText(txt)
        if ( txt.trim().length == 0) {
            checkTxt = false;
        }
    }

    fun writeAddressTextToFile(path: String) {
        val file = File(path)
        if ( !file.exists() ) {
            Log.d(TAG,"writeAddressTextToFile 파일이 없으므로 파일을 생성 합니다.")
            val fileWriter = FileWriter(file, false)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.append("신나라,신사동 536-9,1,017-0000-0001,ergjeorgj@test.com")
            bufferedWriter.newLine()
            bufferedWriter.append("신나라,신사동 536-9,1,017-0000-0001,ergjeorgj@test.com")
            bufferedWriter.close()
        }
    }

    fun readAddressTextFromFile(path: String) {
        val file = File(path)
        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)
        var txt = "";
        bufferedReader.readLines().forEach() {
            Log.d(TAG, it)
            if ( it.trim().length != 0 ) {
                txt = txt+"\n"+it;
            }
        }
        addressTxt.setText(txt)

        if ( txt.trim().length == 0) {
            checkTxt = false;
        }
    }

    fun run () {
        var intent = Intent(this, RunActivity::class.java)
        startActivity(intent)
    }
}