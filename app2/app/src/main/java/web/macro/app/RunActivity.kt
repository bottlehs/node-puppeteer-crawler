package web.macro.app

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_log.toolbar
import kotlinx.android.synthetic.main.activity_run.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.NetworkInterface
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.random.Random


class RunActivity : AppCompatActivity() {
    private val TAG = RunActivity::class.qualifiedName

    // firebase
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    var db : AppDatabase? = null

    /*
    actionStep : 동작 카운드
    actions : 전체 동작 JSON
    timeMillis : 동작당 지연 시간 ( 마이크로초 )
    currentUrl : 현재 URL ( Webview page loading 시 업데이트 되도록 되어 있음 )
    */
    var isProgress = false;
    var isBuy = false;
    var actionStep = 0
    var actions = JSONArray()
    val timeMillis = 1000L
    var currentUrl = ""
    var globalIp = ""
    val rootUrl = "https://m.naver.com" // 첫 홈페이지
    val rootShopUrl = "https://m.shopping.naver.com/home/m/index.nhn" // 쇼핑 홈페이지

    val productName = App.prefs.productName;
    val productId = App.prefs.productId;
    val purchaseId = App.prefs.purchaseId;
    val productIdUrl = "https://msearch.shopping.naver.com/catalog/"+productId+"/products";

    var timeBuy : ArrayList<String> = ArrayList();
    var timeBuy1 : ArrayList<String> = ArrayList();
    var timeBuy2 : ArrayList<String> = ArrayList();
    var timeBuy3 : ArrayList<String> = ArrayList();
    var timeBuy4 : ArrayList<String> = ArrayList();
    var queue = 5;
    var second = 0;

    var search : ArrayList<String> = ArrayList();
    var address : ArrayList<String> = ArrayList();

    var playDate = App.prefs.playDate.toString();
    var nextAuto = App.prefs.nextAuto.toString();

    var timer = Timer()
    val buyPassword = "@123@123";

    private val filepath = "txtFileStorage"
    internal var appExternalFile: File?=null

    private val isExternalStorageReadOnly: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            true
        } else {
            false
        }
    }
    private val isExternalStorageAvailable: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            true
        } else{
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)
        db = AppDatabase.getInstance(this)

        if (!isExternalStorageAvailable || isExternalStorageReadOnly) {
            Toast.makeText(
                this@RunActivity,
                "Please check search.txt, address.txt",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        // 셋팅
        val searchTxtFilePath = "search.txt"
        readSearchTextFromFile(searchTxtFilePath)

        val addressTxtFilePath = "address.txt"
        readAddressTextFromFile(addressTxtFilePath)

        if ( search.size == 0 || address.size == 0 ) {
            Toast.makeText(
                this@RunActivity,
                "Please check search.txt, address.txt",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        if ( 0 < App.prefs.queue.toString().length ) {
            val temp = App.prefs.queue.toString().split("/")
            if ( temp.get(0).toInt() == temp.get(1).toInt() ) {
                queue = temp.get(0).toInt();
            } else {
                queue = Random.nextInt(temp.get(0).toInt(), temp.get(1).toInt());
            }
        }

        // set toolbar as support action bar
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = getString(R.string.activity_run_title)

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        /*
        btnRun (View) : 실행 버튼 ( res/layout/activity_main.xml btn_run 을 참조함 )
        webView (WebView) : 웹뷰 ( res/layout/activity_main.xml web_view 을 참조함 )
        rootUrl : 첫 홈페이지
        rootShopUrl : 쇼핑 홈페이지                
        */
        val btnRun: View = findViewById(R.id.btn_run);
        val progressBar: ProgressBar = findViewById(R.id.progress_bar);
        val webView: WebView = findViewById(R.id.web_view)

        // Enable Javascript in web view
        webView.settings.javaScriptEnabled = true

        // Enable ans setup web view cashe
        webView.settings.setAppCacheEnabled(false)
        // webView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        // Enable zooming in web view
        webView.settings.setSupportZoom(true)

        // More optional settings
        webView.settings.setCacheMode(WebSettings.LOAD_NO_CACHE)
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.loadWithOverviewMode = true
        webView.settings.allowContentAccess = true
        webView.settings.setGeolocationEnabled(true)
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.allowFileAccess = true
        // webView.settings.userAgentString = "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)"

        // WebView settings
        webView.fitsSystemWindows = true

        // Set web view client
        webView.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.d(TAG, "onPageStarted")

                progressBar.isVisible = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d(TAG, "onPageFinished")
                Log.d(TAG, "onPageFinished : " + queue.toLong())

                if ( url.toString().contains("order_result") || isBuy ) {
                    Log.d(TAG, "최종 완료");
                    insertLog()
                }

                progressBar.isVisible = false

                Executors.newSingleThreadScheduledExecutor().schedule({
                    Log.d(
                        TAG,
                        "newSingleThreadScheduledExecutor : " + url.toString()
                            .contains("order_result")
                    );
                    Log.d(TAG, "newSingleThreadScheduledExecutor isBuy : " + isBuy);
                    val obj = actions.getJSONObject(actionStep);
                    Log.d(TAG, "onPageFinished : 11")

                    if (obj.getString("action") != "detailClick" && obj.getString("action") != "productListSearchClick" && obj.getString(
                            "action"
                        ) != "listSearchClick"
                    ) {
                        webView.post(Runnable {
                            webView.loadUrl(
                                "javascript:(function(){" +
                                        "setTimeout(function() {" +
                                        "window.scrollTo(0, document.body.scrollHeight);" +
                                        "}, 1000);" +
                                        "})()"
                            )
                        });
                    };

                    pageFinishedAction(webView, url.toString())
                }, queue.toLong(), TimeUnit.SECONDS)
            }
        }

        // Set web view chrome client
        webView.webChromeClient = object: WebChromeClient() {
            /*
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                Log.d(TAG, "resultMsg ::::::::::::::: " + resultMsg?.data)
                val result = view!!.hitTestResult
                val data = result.extra
                Log.d(TAG,"Uri.parse(data) : "+data)
                /*
                view?.requestFocusNodeHref(resultMsg)
                val browserIntent = Intent(this@MainActivity, TabActivity::class.java)
                browserIntent.putExtra("URL", resultMsg?.data?.getString("url"))
                startActivity(browserIntent)
                */
                return false
            }
            */

            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                result.cancel()
                return true
            }

            override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                result.cancel()
                return true
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.i(TAG, "onProgressChanged : " + newProgress)
                progressBar.progress = newProgress
                if ( 100 <= newProgress ) {
                    Log.i(TAG, "onProgressChanged Complete")
                }
            }
        }

        btnRun.setOnClickListener(View.OnClickListener {
            if (isProgress) {
                var builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.activity_run_running_dialog_title)
                builder.setMessage(R.string.activity_run_running_dialog_description)

                var listener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                    }
                }

                builder.setPositiveButton(R.string.positive, listener)
                builder.show()
            } else {
                var builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.activity_run_run_dialog_title)
                builder.setMessage(R.string.activity_run_run_dialog_description)

                var listener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when (p1) {
                            DialogInterface.BUTTON_POSITIVE ->
                                play()

                        }
                    }
                }

                builder.setPositiveButton(R.string.positive, listener)
                builder.setNegativeButton(R.string.negative, listener)
                builder.show()
            }
        })

        val TT: TimerTask = object : TimerTask() {
            override fun run() {
                // 반복실행할 구문
                Log.d(TAG, "second : " + second)
                if ( 100 <= second ) {
                    stop()

                    second = 0;
                    runOnUiThread {
                        // 문제가 있어 재실행이 필요한 경우
                        play()
                    }
                } else {
                    second++
                }
            }
        }

        timer.schedule(TT, 0, 1000); //Timer 실행

        // firebase
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, TAG.toString())
            param(FirebaseAnalytics.Param.SCREEN_NAME, "실행")
        }

        play()
    }

    override fun onDestroy() {
        super.onDestroy()

        timer.cancel()
        stop();
    }

    fun pageFinishedAction(webView: WebView, url: String) {
        Log.d(TAG, "pageFinishedAction ::::::: ")
        val ss = actions.getJSONObject(actionStep);
        Log.i(TAG, "actionStep : " + actionStep)
        Log.i(TAG, "actionStep.length() : " + actions.length())
        Log.i(TAG, "actionStep.currentUrl : " + currentUrl)
        Log.i(TAG, "actionStep.url.toString() : " + url.toString())

        if ( actionStep < actions.length() && currentUrl != url.toString() )  {
            currentUrl = url.toString()
            val obj = actions.getJSONObject(actionStep);
            obj.put("step", actionStep)
            actionStep = actionStep + 1
            GlobalScope.launch(context = Dispatchers.Main) {
                if ( obj.has("delay") ) {
                    delay(50000L)
                } else {
                    delay(timeMillis)
                }
                Log.d(TAG, "" + obj.getInt("step") + "-" + obj.getString("name"))
                if ( obj.get("function") == "element" ) {
                    elementAction(obj, webView)
                } else if ( obj.get("function") == "webview" ) {
                    backAction(obj, webView)
                } else if ( obj.get("function") == "url" ) {
                    urlAction(obj, webView)
                }
            }
        } else {
            Log.d(TAG, "pageFinishedAction hello hello 0")
            if ( actionStep < actions.length() ) {
                Log.d(TAG, "pageFinishedAction hello hello 1")
            } else {
                Log.d(TAG, "pageFinishedAction hello hello 2")
                stop()
            }
        }
    }

    fun urlAction(obj: JSONObject, webView: WebView) {
        webView.post(Runnable {
            webView.loadUrl(obj.getString("url"))
        });

        second = 0;
    }
    fun backAction(obj: JSONObject, webView: WebView) {
        if ( obj.getString("action") == "back" ) {
            webView.goBack()
        }

        second = 0;
    }
    fun elementAction(obj: JSONObject, webView: WebView) {
        Log.d(TAG, "elementAction" + obj.getString("action"))
        if ( obj.getString("action") == "focus" ) {
            webView.post(Runnable {
                webView.loadUrl(
                    "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                        "index"
                    ) + "].focus();})()"
                )
            });
        } else if ( obj.getString("action") == "value" ) {
            Log.d(
                TAG,
                "elementAction" + obj.getString("action") + " // " + obj.getString("selector") + " // " + obj.getString(
                    "value"
                )
            )
            webView.post(Runnable {
                webView.loadUrl(
                    "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                        "index"
                    ) + "].value='" + obj.getString("value") + "'})()"
                )
            });
            /*
            if ( obj.has("delay") ) {
                webView.post(Runnable {
                    webView.loadUrl(
                        "javascript:(function(){" +
                                "setTimeout(function() {" +
                                "document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                            "index"
                        ) + "].value='" + obj.getString(
                            "value"
                        ) + "';" +
                                "console.log(document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                            "index"
                        ) + "].value);" +
                                "}, 500);" +
                                "})()"
                    )
                });
            } else {
                webView.post(Runnable {
                    webView.loadUrl(
                        "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                            "index"
                        ) + "].value='" + obj.getString("value") + "'})()"
                    )
                });
            }
            */
        } else if ( obj.getString("action") == "click" ) {
            if ( obj.getString("index") == "random" ) {
                webView.post(Runnable {
                    webView.loadUrl(
                        "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[Math.floor(Math.random() * document.querySelectorAll('" + obj.getString(
                            "selector"
                        ) + "').length)].click();})()"
                    )
                });
            } else {
                webView.post(Runnable {
                    webView.loadUrl(
                        "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                            "index"
                        ) + "].click();})()"
                    )
                });
            }
        } else if ( obj.getString("action") == "submit" ) {
            webView.post(Runnable {
                webView.loadUrl(
                    "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                        "index"
                    ) + "].submit();})()"
                )
            });
        } else if ( obj.getString("action") == "detailClick" ) {
            webView.post(Runnable {
                webView.loadUrl(
                    "javascript:(function(){" +
                            // "document.querySelectorAll('" + obj.getString("selector") + "')[0].setAttribute('target','_self')" +
                            "document.querySelectorAll('" + obj.getString("selector") + "')[0].click();" +
                            "})()"
                )
            });
        } else if ( obj.getString("action") == "listSearchClick" ) {
            /*
            TODO
            찾기시 스크롤을 한칸씩 내리도록 수정할 필요가 있음.
             */
            webView.post(Runnable {
                webView.loadUrl(
                    "javascript:(function(){" +
                            "var productSearch = function () {" +
                            "setTimeout(function() {" +
                            "var item = document.querySelectorAll('" + obj.getString("selector") + "');" +
                            "var selectProduct = false;" +
                            "for ( var i = 0; i < item.length; i++ ) {" +
                            "window.scrollTo(0, Number(item[i].offsetTop));" +
                            "if ( Number(item[i].getAttribute('data-i')) == " + obj.getString("data_i") + " ) {" +
                            "console.log('있다111');" +
                            // "alert('" + obj.getString("data_i") + "');" +
                            "if ( item[i].getAttribute('target') == '_blank') { item[i].setAttribute('target','_self') };" +
                            "window.scrollTo(0, document.body.scrollHeight);" +
                            "selectProduct = true;" +
                            "item[i].click();" +
                            "break;" +
                            // "alert(item[i].getAttribute('href'));"+
                            //"item[i].click();"+
                            "}" +
                            "}" +
                            "if ( !selectProduct ) {" +
                            "setTimeout(() => {" +
                            "console.log('없다없다1212');" +
                            "window.scrollTo(0, document.body.scrollHeight);" +
                            "productSearch()" +
                            "}, 1500);" +
                            "}" +
                            "}, 2000);" +
                            "};" +
                            "productSearch();" +
                            "})()"
                )
            });
        } else if ( obj.getString("action") == "productListSearchClick" ) {
            /*
            TODO
            찾기시 스크롤을 한칸씩 내리도록 수정할 필요가 있음.
             */
            webView.post(Runnable {
                webView.loadUrl(
                    "javascript:(function(){" +
                            "var productSearch = function () {" +
                            "setTimeout(function() {" +
                            "var item = document.querySelectorAll('" + obj.getString("selector") + "');" +
                            "var selectProduct = false;" +
                            "for ( var i = 0; i < item.length; i++ ) {" +
                            "window.scrollTo(0, Number(item[i].offsetTop));" +
                            "if ( Number(item[i].getAttribute('data-i')) ==  " + obj.getString("data_i") + " ) {" +
                            "console.log('있다1');" +
                            "if ( item[i].getAttribute('target') == '_blank') { item[i].setAttribute('target','_self') };" +
                            "selectProduct = true;" +
                            "item[i].click();" +
                            "break;" +
                            "}" +
                            "}" +
                            "if ( !selectProduct ) {" +
                            "document.querySelectorAll('.paginator_list_paging__2cmhX button.paginator_btn_next__36Dhk')[0].click();" +
                            "window.scrollTo(0, 0);" +
                            "setTimeout(() => {" +
                            "console.log('없다없다12');" +
                            "window.scrollTo(0, document.body.scrollHeight);" +
                            "productSearch()" +
                            "}, 1500);" +
                            "};" +
                            "}, 2500);" +
                            "};" +
                            "productSearch();" +
                            "})()"
                )
            });
        }

        if ( obj.has("buy") ) {
            if ( obj.getBoolean("buy") == true ) {
                isBuy = true;
            }
        }

        Log.d(TAG, "isBuy : " + isBuy);

        if ( obj.getBoolean("next") == true ) {
            if ( actionStep < actions.length() ) {
                val obj = actions.getJSONObject(actionStep);
                obj.put("step", actionStep)

                actionStep = actionStep + 1
                GlobalScope.launch(context = Dispatchers.Main) {
                    delay(timeMillis)
                    Log.d(TAG, "" + obj.getInt("step") + "-" + obj.getString("name"))
                    if ( obj.get("function") == "element" ) {
                        elementAction(obj, webView)
                    } else if ( obj.get("function") == "webview" ) {
                        backAction(obj, webView)
                    } else if ( obj.get("function") == "url" ) {
                        urlAction(obj, webView)
                    }
                }
            } else {
                stop()
            }
        }

        second = 0;
    }

    fun clearCookies(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(
                TAG,
                "Using clearCookies code for API >=" + Build.VERSION_CODES.LOLLIPOP_MR1.toString()
            )
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else {
            Log.d(
                TAG,
                "Using clearCookies code for API <" + Build.VERSION_CODES.LOLLIPOP_MR1.toString()
            )
            val cookieSyncMngr = CookieSyncManager.createInstance(context)
            cookieSyncMngr.startSync()
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            cookieManager.removeSessionCookie()
            cookieSyncMngr.stopSync()
            cookieSyncMngr.sync()
        }
    }

    fun play() {
        val ip = getIp()
        Log.d(TAG, "play isProgress : " + isProgress)
        Log.d(TAG, "ip toString : " + ip.toString())

        if ( globalIp == ip.toString() ) {
            // 마지막 IP 와 동일한 경우 airplanMode 실행
            this.airplaneMode()
            return
        }

        if ( !isProgress && 0 < ip.toString().length ) {
           this.globalIp = ip.toString();

            Log.d(TAG, "play : 1")
            web_view.clearCache(true)
            Log.d(TAG, "play : 2")
            web_view.clearHistory();
            Log.d(TAG, "play : 3")
            web_view.clearFormData();
            Log.d(TAG, "play : 4")
            val current = LocalDateTime.now()
            Log.d(TAG, "play : 5")
            val currentDate = current.format(DateTimeFormatter.ISO_LOCAL_DATE)
            Log.d(TAG, "play : 6")
            val temp = currentDate.toString().split("-")
            Log.d(TAG, "play : 8")
            clearCookies(this)
            timeBuy.clear()

            if ( 0 < App.prefs.time1.toString().length && 0 < App.prefs.purchase1.toString().length ) {
                timeBuy1.clear();
                timeBuy1.add(App.prefs.time1.toString())
                timeBuy1.add(App.prefs.purchase1.toString())
            }

            if ( 0 < App.prefs.time2.toString().length && 0 < App.prefs.purchase2.toString().length ) {
                timeBuy2.clear();
                timeBuy2.add(App.prefs.time2.toString())
                timeBuy2.add(App.prefs.purchase2.toString())
            }

            if ( 0 < App.prefs.time3.toString().length && 0 < App.prefs.purchase3.toString().length ) {
                timeBuy3.clear();
                timeBuy3.add(App.prefs.time3.toString())
                timeBuy3.add(App.prefs.purchase3.toString())
            }

            if ( 0 < App.prefs.time4.toString().length && 0 < App.prefs.purchase4.toString().length ) {
                timeBuy4.clear();
                timeBuy4.add(App.prefs.time4.toString())
                timeBuy4.add(App.prefs.purchase4.toString())
            }

            if ( timeBuy1.size == 2 ) {
                val timeTemp = timeBuy1.get(0).toString().split("/");
                val startTimeTemp = timeTemp.get(0).toString().split(":");
                val endTimeTemp = timeTemp.get(1).toString().split(":");
                val startTime = LocalDateTime.of(
                    temp.get(0).toInt(),
                    temp.get(1).toInt(),
                    temp.get(2).toInt(),
                    startTimeTemp.get(
                        0
                    ).toInt(),
                    startTimeTemp.get(1).toInt(),
                    0,
                    0
                )
                val endTime = LocalDateTime.of(
                    temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                        0
                    ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
                )

                var savedLogs = db!!.logsDao().getDateAll(
                    currentDate + " " + timeTemp.get(0).toString() + ":00",
                    currentDate + " " + timeTemp.get(
                        1
                    ).toString() + ":59"
                )
                if ( (current.isAfter(startTime)  &&  current.isBefore(endTime)) && savedLogs.size < timeBuy1.get(
                        1
                    ).toInt() ) {
                    Log.d(TAG, "timeBuy - 1 ok")
                    timeBuy.clear()
                    timeBuy.add(timeBuy1.get(0).toString())
                    timeBuy.add(timeBuy1.get(1).toString())
                } else {
                    Log.d(TAG, "timeBuy - 1 no")
                }
            } else {
                Log.d(TAG, "timeBuy - 1 / " + timeBuy1.size)
            }

            if ( timeBuy2.size == 2 ) {
                val timeTemp = timeBuy2.get(0).toString().split("/");
                val startTimeTemp = timeTemp.get(0).toString().split(":");
                val endTimeTemp = timeTemp.get(1).toString().split(":");
                val startTime = LocalDateTime.of(
                    temp.get(0).toInt(),
                    temp.get(1).toInt(),
                    temp.get(2).toInt(),
                    startTimeTemp.get(
                        0
                    ).toInt(),
                    startTimeTemp.get(1).toInt(),
                    0,
                    0
                )
                val endTime = LocalDateTime.of(
                    temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                        0
                    ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
                )
                var savedLogs = db!!.logsDao().getDateAll(
                    currentDate + " " + timeTemp.get(0).toString() + ":00",
                    currentDate + " " + timeTemp.get(
                        1
                    ).toString() + ":59"
                )
                if ( (current.isAfter(startTime)  &&  current.isBefore(endTime)) && savedLogs.size < timeBuy2.get(
                        1
                    ).toInt() ) {
                    Log.d(TAG, "timeBuy - 2 ok")
                    timeBuy.clear()
                    timeBuy.add(timeBuy2.get(0).toString())
                    timeBuy.add(timeBuy2.get(1).toString())
                } else {
                    Log.d(TAG, "timeBuy - 2 no")
                }
            } else {
                Log.d(TAG, "timeBuy - 2 / " + timeBuy2.size)
            }

            if ( timeBuy3.size == 2 ) {
                val timeTemp = timeBuy3.get(0).toString().split("/");
                val startTimeTemp = timeTemp.get(0).toString().split(":");
                val endTimeTemp = timeTemp.get(1).toString().split(":");
                val startTime = LocalDateTime.of(
                    temp.get(0).toInt(),
                    temp.get(1).toInt(),
                    temp.get(2).toInt(),
                    startTimeTemp.get(
                        0
                    ).toInt(),
                    startTimeTemp.get(1).toInt(),
                    0,
                    0
                )
                val endTime = LocalDateTime.of(
                    temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                        0
                    ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
                )
                var savedLogs = db!!.logsDao().getDateAll(
                    currentDate + " " + timeTemp.get(0).toString() + ":00",
                    currentDate + " " + timeTemp.get(
                        1
                    ).toString() + ":59"
                )
                if ( (current.isAfter(startTime)  &&  current.isBefore(endTime)) && savedLogs.size < timeBuy3.get(
                        1
                    ).toInt() ) {
                    Log.d(TAG, "timeBuy - 3 ok")
                    timeBuy.clear()
                    timeBuy.add(timeBuy3.get(0).toString())
                    timeBuy.add(timeBuy3.get(1).toString())
                } else {
                    Log.d(TAG, "timeBuy - 3 no")
                }
            } else {
                Log.d(TAG, "timeBuy - 3 / " + timeBuy3.size)
            }

            if ( timeBuy4.size == 2 ) {
                val timeTemp = timeBuy4.get(0).toString().split("/");
                val startTimeTemp = timeTemp.get(0).toString().split(":");
                val endTimeTemp = timeTemp.get(1).toString().split(":");
                val startTime = LocalDateTime.of(
                    temp.get(0).toInt(),
                    temp.get(1).toInt(),
                    temp.get(2).toInt(),
                    startTimeTemp.get(
                        0
                    ).toInt(),
                    startTimeTemp.get(1).toInt(),
                    0,
                    0
                )
                val endTime = LocalDateTime.of(
                    temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                        0
                    ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
                )
                var savedLogs = db!!.logsDao().getDateAll(
                    currentDate + " " + timeTemp.get(0).toString() + ":00",
                    currentDate + " " + timeTemp.get(
                        1
                    ).toString() + ":59"
                )
                if ( (current.isAfter(startTime)  &&  current.isBefore(endTime)) && savedLogs.size < timeBuy4.get(
                        1
                    ).toInt() ) {
                    Log.d(TAG, "timeBuy - 4 ok")
                    timeBuy.clear()
                    timeBuy.add(timeBuy4.get(0).toString())
                    timeBuy.add(timeBuy4.get(1).toString())
                } else {
                    Log.d(TAG, "timeBuy - 4 no")
                }
            } else {
                Log.d(TAG, "timeBuy - 4 / " + timeBuy4.size)
            }

            if ( timeBuy.size == 2 ) {
                if ( playDate == currentDate ) {
                    currentUrl = "";
                    isProgress = true;
                    isBuy = false;
                    actionStep = 0

                    web_view.post(Runnable {
                        web_view.loadUrl(rootUrl)
                    });

                    btn_run.setImageDrawable(getDrawable(R.drawable.ic_stop))
                } else {
                    Toast.makeText(
                        this@RunActivity,
                        "Failed: 'Date" + playDate + " / " + currentDate,
                        Toast.LENGTH_SHORT
                    ).show()
                    stop();
                    return
                }
            } else {
                Toast.makeText(this@RunActivity, "Failed: Time/Buy", Toast.LENGTH_SHORT).show()

                // 중지
                stop();
                return
            }

            var searchPosition = App.prefs.searchPosition.toString().toInt();
            var addressPosition = App.prefs.addressPosition.toString().toInt();

            if ( search.size <= searchPosition ) {
                searchPosition = 0;
                App.prefs.searchPosition = searchPosition.toString();
            }

            if ( address.size <= addressPosition ) {
                addressPosition = 0;
                App.prefs.addressPosition = addressPosition.toString();
            }

            Log.d(TAG, "searchPosition : " + searchPosition)
            Log.d(TAG, "addressPosition : " + addressPosition)

            var useAddress : ArrayList<String> = ArrayList();
            address.get(addressPosition).split(",").forEach{ row ->
                useAddress.add(row);
            }

            search.forEach{ row ->
                Log.d(TAG, "search row : " + row)
            }

            Log.d(TAG, "searchPosition123 : " + searchPosition + "/" + search.get(searchPosition))

            /*
            action : actions 에 추가할 json object 선언
             */
            actions = JSONArray()
            var action = JSONObject()
            /*
            name : 동작명
            action : 액션 ( focus, value. click, back, url, submit, listSearchClick )
                focus : selector 요소 포커스
                value : input 에 action.value 값 입력
                click : selector 요소 클릭
                back : webView.goBack()
                url : action.url 로 이동 ( 웹뷰 url 변경 )
                submit : selector 요소 submit
                listSearchClick : selector 요소 리스트에서 action.data_i 값 찾기

            selector : selector 요소 찾기 ( CSS selector 개념 )
            function : 함수
                element : 웹뷰내 웹 컨트롤
                webview : 웹뷰 컨트롤

            index :
                int : selector 요소가 여럭개가 나올경우 몆번째 요소인지 명시적 지정
                random : selector 요소가 여러개 나올경우 특정 번째 요소를 랜덤으로 선택함

            next : 페이지 이동 없이 한 화면에서 다음 동작 수행 ( true or false )
            */

            // 검색 포커스
            action = JSONObject()
            action.put("name", "검색 포커스")
            action.put("action", "focus")
            action.put("selector", "#MM_SEARCH_FAKE")
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", true)
            actions.put(action)

            // 검색어 입력
            action = JSONObject()
            action.put("name", "검색어 입력")
            action.put("action", "value")
            action.put("selector", "#query")
            action.put("value", search.get(searchPosition))
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", true)
            actions.put(action)

            // 검색어 폼 전송
            action = JSONObject()
            action.put("name", "검색어 폼 전송")
            action.put("action", "click")
            action.put("selector", ".sch_btn_search")
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", false)
            actions.put(action)

            // 검색어 결과 클릭
            action = JSONObject()
            action.put("name", "검색어 결과 클릭")
            action.put("action", "click")
            action.put("selector", ".sp_nreview .total_sub+.total_tit")
            action.put("function", "element")
            action.put("index", "random")
            action.put("next", false)
            actions.put(action)

            // 뒤로가기
            action = JSONObject()
            action.put("name", "뒤로가기")
            action.put("action", "back")
            action.put("function", "webview")
            action.put("next", false)
            actions.put(action)

            // 검색어 결과 클릭
            action = JSONObject()
            action.put("name", "검색어 결과 클릭")
            action.put("action", "click")
            action.put("selector", ".sp_nreview .total_sub+.total_tit")
            action.put("function", "element")
            action.put("index", "random")
            action.put("next", false)
            actions.put(action)

            // 뒤로가기
            action = JSONObject()
            action.put("name", "뒤로가기")
            action.put("action", "back")
            action.put("function", "webview")
            action.put("next", false)
            actions.put(action)

            // 검색 포커스
            action = JSONObject()
            action.put("name", "검색 포커스")
            action.put("action", "focus")
            action.put("selector", "#nx_query")
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", true)
            actions.put(action)

            // 검색어 입력
            action = JSONObject()
            action.put("name", "검색어 입력")
            action.put("action", "value")
            action.put("selector", "#nx_query")
            action.put("value", productName)
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", true)
            actions.put(action)

            // 검색어 폼 전송
            action = JSONObject()
            action.put("name", "검색어 폼 전송")
            action.put("action", "click")
            action.put("selector", ".btn_search")
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", false)
            actions.put(action)

            // 검색어 결과 쇼핑 클릭
            action = JSONObject()
            action.put("name", "검색어 결과 쇼핑 클릭")
            action.put("action", "click")
            action.put("selector", ".type_white .sch_tab .lst_sch .bx a")
            action.put("function", "element")
            action.put("index", 1)
            action.put("next", false)
            actions.put(action)

            // 쇼핑홈으로 이동
            action = JSONObject()
            action.put("name", "쇼핑홈으로 이동")
            action.put("action", "url")
            action.put("function", "url")
            action.put("url", rootShopUrl)
            actions.put(action)

            /**/
            // 검색 클릭
            action = JSONObject()
            action.put("name", "검색 클릭")
            action.put("action", "click")
            action.put("selector", "#sear")
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", true)
            actions.put(action)

            // 검색 포커스
            action = JSONObject()
            action.put("name", "검색 포커스")
            action.put("action", "focus")
            action.put("selector", "#sear")
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", true)
            actions.put(action)

            // 검색어 입력
            action = JSONObject()
            action.put("name", "검색어 입력")
            action.put("action", "value")
            action.put("selector", "#sear")
            action.put("value", productName)
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", true)
            actions.put(action)

            // 검색어 폼 전송
            action = JSONObject()
            action.put("name", "검색어 폼 전송")
            action.put("action", "submit")
            action.put("selector", "#searchForm")
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", false)
            actions.put(action)

            // 쇼핑 검색어 결과 상품 찾기후 클릭
            action = JSONObject()
            action.put("name", "쇼핑 검색어 결과 상품 찾기후 클릭")
            action.put("action", "productListSearchClick")
            action.put("selector", ".product_list_item__2tuKA a.product_info_main__1RU2S")
            action.put("function", "element")
            action.put("data_i", productId)
            action.put("next", false)
            actions.put(action)

            // 쇼핑 상세보기 에서 전체 판매처 보러가기
            action = JSONObject()
            action.put("name", "쇼핑 상세보기 에서 전체 판매처 보러가기")
            action.put("action", "detailClick")
            action.put("selector", ".main_link_more__1qw78.linkAnchor")
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", false)
            actions.put(action)

            // 쇼핑 상세보기 에서 전체 판매처로 이동
            action = JSONObject()
            action.put("name", "쇼핑 상세보기 에서 전체 판매처로 이동")
            action.put("action", "url")
            action.put("function", "url")
            action.put("url", productIdUrl)
            actions.put(action)

            // 쇼핑 상세보기 에서 구매하기 화면으로 이동
            action = JSONObject()
            action.put("name", "쇼핑 상세보기 에서 구매하기 화면으로 이동")
            action.put("action", "listSearchClick")
            action.put(
                "selector",
                ".productContent_item_inner___teBC .productContent_link_seller__uA-1b"
            )
            action.put("function", "element")
            action.put("data_i", purchaseId)
            action.put("next", false)
            actions.put(action)

            // 구매하기 화면 - 구매하기
            action = JSONObject()
            action.put("name", "구매하기 화면 - 구매하기")
            action.put("action", "click")
            action.put("selector", "#fixedActionButton .ec-base-button.gColumn  a.btnStrong")
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", false)
            actions.put(action)

            // 구매하기 화면 - 비회원구매
            action = JSONObject()
            action.put("name", "구매하기 화면 - 구매하기")
            action.put("action", "click")
            action.put("selector", ".btnEm")
            action.put("function", "element")
            action.put("index", 0)
            action.put("next", false)
            actions.put(action)
            // 06035 / 서울특별시 강남구 가로수길 9 (신사동) / 없음
            // 신나라,신사동 536-9,1,017-0000-0001,ergjeorgj@test.com
            // 신나라,6035,서울특별시 강남구 가로수길 9 (신사동),없음,017-0000-0001,ergjeorgj@test.com,@123@123

            // 구매하기 화면 - 비회원구매 - 이름 입력 1
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 이름 입력 1")
            action.put("action", "value")
            action.put("selector", "#rname")
            action.put("value", useAddress.get(0))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 30)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 우편번호
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 우편번호")
            action.put("action", "value")
            action.put("selector", "#rzipcode1")
            action.put("value", useAddress.get(1))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 기본주소
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 기본주소")
            action.put("action", "value")
            action.put("selector", "#raddr1")
            action.put("value", useAddress.get(2))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 상세주소
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 상세주소")
            action.put("action", "value")
            action.put("selector", "#raddr2")
            action.put("value", useAddress.get(3))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            val useAddressTel = useAddress.get(4).split("-")
            // 구매하기 화면 - 비회원구매 - 전화번호 _ 1
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 전화번호 _ 1")
            action.put("action", "value")
            action.put("selector", "#rphone2_1")
            action.put("value", useAddressTel.get(0))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 전화번호 _ 2
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 전화번호 _ 2")
            action.put("action", "value")
            action.put("selector", "#rphone2_2")
            action.put("value", useAddressTel.get(1))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 전화번호 _ 3
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 전화번호 _ 3")
            action.put("action", "value")
            action.put("selector", "#rphone2_3")
            action.put("value", useAddressTel.get(2))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            val useAddressEmail = useAddress.get(5).split("@")
            // 구매하기 화면 - 비회원구매 - 이메일 _ 1
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 이메일 _ 1")
            action.put("action", "value")
            action.put("selector", "#oemail1")
            action.put("value", useAddressEmail.get(0))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 이메일 _ 2
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 이메일 _ 2")
            action.put("action", "value")
            action.put("selector", "#oemail2")
            action.put("value", useAddressEmail.get(1))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 비밀번호
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 비밀번호")
            action.put("action", "value")
            action.put("selector", "#order_password")
            action.put("value", buyPassword)
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 비밀번호 확인
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 비밀번호 확인")
            action.put("action", "value")
            action.put("selector", "#order_password_confirm")
            action.put("value", buyPassword)
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 입금은행
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 입금은행")
            action.put("action", "value")
            action.put("selector", "#bankaccount")
            action.put("value", useAddress.get(6))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 입금자명
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 입금은행")
            action.put("action", "value")
            action.put("selector", "#pname")
            action.put("value", useAddress.get(0))
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 모든 약관 동의
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 모든 약관 동의")
            action.put("action", "click")
            action.put("selector", "#allAgree")
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("next", true)
            actions.put(action)

            // 구매하기 화면 - 비회원구매 - 결제하기
            action = JSONObject()
            action.put("name", "구매하기 화면 - 비회원구매 - 결제하기")
            action.put("action", "click")
            action.put("selector", "#btn_payment")
            action.put("function", "element")
            action.put("index", 0)
            action.put("delay", 5)
            action.put("buy", true)
            action.put("next", false)
            actions.put(action)

            val startCurrentDate = current.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val startCurrentTime = current.format(DateTimeFormatter.ISO_LOCAL_TIME)
            val startTemp = startCurrentTime.split(".");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE + "_start") {
                param(FirebaseAnalytics.Param.SCREEN_CLASS, TAG.toString())
                param("Name", productName.toString())
                param("Date", startCurrentDate + " " + startTemp[0])
                param("IpAddress", ip.toString())
                param("Address", address.get(addressPosition))
                param("Search", search.get(searchPosition))
                param("productId", productId.toString())
                param("purchaseId", purchaseId.toString())
                param("productIdUrl", productIdUrl)
            }
        }
    }

    fun stop() {
        if ( nextAuto == "Y" ) {
            val current = LocalDateTime.now()
            val currentDate = current.format(DateTimeFormatter.ISO_LOCAL_DATE)

            playDate = currentDate;
            App.prefs.playDate = currentDate;
        }

        isProgress = false
        btn_run.setImageDrawable(getDrawable(R.drawable.ic_play))
    }

    fun readSearchTextFromFile(path: String) {
        appExternalFile = File(getExternalFilesDir(filepath), path)

        var fileInputStream = FileInputStream(appExternalFile)
        var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
        var text: String? = null
        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        fileInputStream.close()
        if ( stringBuilder.toString().trim().length != 0 ) {
            stringBuilder.toString().split(",").forEach{ row ->
                Log.d(TAG, "search stringBuilder" + row)
                search.add(row)
            }
        }
    }

    fun readAddressTextFromFile(path: String) {
        appExternalFile = File(getExternalFilesDir(filepath), path)

        var fileInputStream = FileInputStream(appExternalFile)
        var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
        var text: String? = null
        while ({ text = bufferedReader.readLine(); text }() != null) {
            if ( text.toString().split(",").size == 7 ) {
                address.add(text.toString())
            }
        }
        fileInputStream.close()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun getIp(): String? {
        var ip = "";
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isWifiConn: Boolean = false
        var isMobileConn: Boolean = false

        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network).apply {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn = isWifiConn or isConnected
                }
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn = isMobileConn or isConnected
                }
            }
        }

        if ( isWifiConn ) {
            ip = getDeviceipWiFiData().toString();
        } else if ( isMobileConn ) {
            ip = getDeviceipMobileData().toString();
        } else {
            ip = "";
        }

        return ip;
    }

    private fun isAirplaneModeOn(context: Context): Boolean {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON,
            0
        ) !== 0
    }

    private fun airplaneMode() {
        App.prefs.airplaneMode = "1";

        val display = windowManager.defaultDisplay // in case of Activity
        val size = Point()
        display.getRealSize(size) // or getSize(size)
        val width = size.x
        val height = size.y
        Log.d(TAG, "heightheightheightheight :::" + height);

        var bottomBarHeight = 0
        val resourceIdBottom = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceIdBottom > 0) bottomBarHeight =
            resources.getDimensionPixelSize(resourceIdBottom)

        Log.d(TAG, "bottomBarHeightbottomBarHeight:::::::::::: " + bottomBarHeight)

        val sizeX = height - (bottomBarHeight/2);
        App.prefs.backButtonSizeX = sizeX.toString();

        val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
        startActivity(intent)
    }

    fun getDeviceipMobileData(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkinterface = en.nextElement()
                val enumIpAddr = networkinterface.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress.toString()
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("Current IP", ex.toString())
        }
        return null
    }

    fun getDeviceipWiFiData(): String? {
        val wm = getSystemService(WIFI_SERVICE) as WifiManager
        return Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
    }

    fun insertLog() {
        val current = LocalDateTime.now()
        val currentDate = current.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val currentTime = current.format(DateTimeFormatter.ISO_LOCAL_TIME)
        val temp = currentTime.split(".");
        var ip = getIp()

        Log.d(TAG, "searchPosition, addressPosition update")
        var searchPosition = App.prefs.searchPosition.toString().toInt();
        var addressPosition = App.prefs.addressPosition.toString().toInt();
        var strSearch = search.get(searchPosition);
        var strAddress = address.get(addressPosition);

        Log.d(TAG, "searchPosition, addressPosition update searchPosition 1 : " + searchPosition)
        Log.d(TAG, "searchPosition, addressPosition update addressPosition 1 : " + addressPosition)

        searchPosition++;
        addressPosition++;

        Log.d(TAG, "searchPosition, addressPosition update searchPosition 2 : " + searchPosition)
        Log.d(TAG, "searchPosition, addressPosition update addressPosition 2 : " + addressPosition)

        App.prefs.searchPosition = searchPosition.toString();
        App.prefs.addressPosition = addressPosition.toString();

        Log.d(
            TAG,
            "searchPosition, addressPosition update searchPosition 3 : " + App.prefs.searchPosition
        )
        Log.d(
            TAG,
            "searchPosition, addressPosition update addressPosition 3 : " + App.prefs.addressPosition
        )

        Log.d(
            TAG,
            "insertLog : " + currentDate + " " + temp[0] + " / " + productName.toString() + " / " + ip.toString()
        )

        val log = Logs(
            0,
            currentDate + " " + temp[0],
            productName.toString(),
            "1",
            ip.toString(),
            strAddress,
            strSearch
        )
        db?.logsDao()?.insertAll(log)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE + "end") {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, TAG.toString())
            param("Name", productName.toString())
            param("Date", currentDate + " " + temp[0])
            param("IpAddress", ip.toString())
            param("Address", strAddress)
            param("Search", strSearch)
            param("productId", productId.toString())
            param("purchaseId", purchaseId.toString())
            param("productIdUrl", productIdUrl)
        }

        isBuy = false;
        airplaneMode()
    }
}