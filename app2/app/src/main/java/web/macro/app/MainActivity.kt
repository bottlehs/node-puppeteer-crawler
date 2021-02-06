package web.macro.app

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    // Actions
    var actionStep = 0
    val actions = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRun: View = findViewById(R.id.btn_run);
        val webView: WebView = findViewById(R.id.web_view)

        val rootUrl = "https://m.naver.com" // 첫 홈페이지
        val rootShopUrl = "https://m.shopping.naver.com/home/m/index.nhn" // 쇼핑 홈페이지
        val sleep = 1000 // 실행 속도 ( 마이크로초 )

        // Actions
        var action = JSONObject()

        // 검색 포커스
        action = JSONObject()
        action.put("action", "focus")
        action.put("position", "m.naver.com")
        action.put("selector", "#MM_SEARCH_FAKE")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", true)
        actions.put(action)

        // 검색어 입력
        action = JSONObject()
        action.put("action", "value")
        action.put("position", "m.naver.com")
        action.put("selector", "#query")
        action.put("value", "지리")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", true)
        actions.put(action)

        // 검색어 폼 전송
        action = JSONObject()
        action.put("action", "click")
        action.put("position", "m.naver.com")
        action.put("selector", ".sch_submit")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", false)
        actions.put(action)

        // 검색어 결과 클릭
        action = JSONObject()
        action.put("action", "click")
        action.put("position", "m.naver.com")
        action.put("selector", ".sp_nnews .news_wrap .news_tit")
        action.put("function", "element")
        action.put("index", "random")
        action.put("next", false)
        actions.put(action)

        // 뒤로가기
        action = JSONObject()
        action.put("action", "back")
        action.put("position", "m.naver.com")
        action.put("function", "webview")
        action.put("next", false)
        actions.put(action)

        /*
        // 인기 주제 판을 확인해 보세요! 1
        action.put("action", "click")
        action.put("position", "m.naver.com")
        action.put("selector", ".uio_thumbnail .ut_a")
        action.put("function", "elementAction")
        action.put("index", 0)
        actions.put(action)

        // 인기 주제 판을 확인해 보세요! 2
        action = JSONObject()
        action.put("action", "click")
        action.put("position", "m.naver.com")
        action.put("selector", ".grid1_wrap a")
        action.put("function", "elementAction")
        action.put("index", "random")
        actions.put(action)
        */

        // Enable Javascript in web view
        webView.settings.javaScriptEnabled = true

        // Enable ans setup web view cashe
        webView.settings.setAppCacheEnabled(false)
        // webView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        // Enable zooming in web view
        webView.settings.setSupportZoom(true)

        // More optional settings
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.loadWithOverviewMode = true
        webView.settings.allowContentAccess = true
        webView.settings.setGeolocationEnabled(true)
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.allowFileAccess = true
        webView.settings.userAgentString = "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)"

        // WebView settings
        webView.fitsSystemWindows = true

        // Set web view client
        webView.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.i("MainActivity","onPageStarted")
            }

            override fun onPageFinished(view: WebView?, url: String?) {

                Toast.makeText(applicationContext, "onPageFinished", Toast.LENGTH_LONG).show()
                Log.i("MainActivity","onPageFinished")

                if ( actionStep < actions.length() )  {
                    val obj = actions.getJSONObject(actionStep);
                    if ( obj.get("function") == "element" ) {
                        elementAction(obj, webView)
                    } else if ( obj.get("function") == "webview" ) {
                        backAction(obj, webView)
                    }
                }
            }
        }

        // Set web view chrome client
        webView.webChromeClient = object: WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.i("MainActivity","newProgress:"+newProgress)
            }
        }

        btnRun.setOnClickListener(View.OnClickListener {
            actionStep = 0
            webView.loadUrl(rootUrl)
            /*
            webView.loadUrl("javascript:(function(){document.querySelector('#MM_SEARCH_FAKE').focus();})()")
            webView.loadUrl("javascript:(function(){document.querySelector('#query').value='hello1';})()")
            webView.loadUrl("javascript:(function(){document.querySelector('.sp_nnews .news_wrap .news_tit').click();})()")
            Toast.makeText(applicationContext, "hello", Toast.LENGTH_LONG).show()
            */
        })


    }
    fun backAction(obj: JSONObject, webView: WebView) {
        if ( obj.getString("action") == "back" ) {
            webView.goBack()
        }
    }
    fun elementAction(obj: JSONObject, webView: WebView) {
        Log.i("MainActivity","aa elementAction:")
        Log.i("MainActivity","aa actionStep:"+actionStep)
        Log.i("MainActivity","aa action:"+obj.getString("action"))
        Log.i("MainActivity","aa selector:"+obj.getString("selector"))
        actionStep = actionStep + 1
        if ( obj.getString("action") == "focus" ) {
            webView.loadUrl("javascript:(function(){document.querySelectorAll('"+obj.getString("selector")+"')["+obj.getInt("index")+"].focus();})()")
            Log.i("MainActivity","aa focusfocusfocusfocusfocusfocusfocusfocus")
        } else if ( obj.getString("action") == "value" ) {
            webView.loadUrl("javascript:(function(){document.querySelectorAll('"+obj.getString("selector")+"')["+obj.getInt("index")+"].value='"+obj.getString("value")+"'})()")
        } else if ( obj.getString("action") == "click" ) {
            if ( obj.getString("index") == "random" ) {
                webView.loadUrl("javascript:(function(){document.querySelectorAll('"+obj.getString("selector")+"')[Math.floor(Math.random() * document.querySelectorAll('"+obj.getString("selector")+"').length)].click();})()")
            } else {
                webView.loadUrl("javascript:(function(){document.querySelectorAll('"+obj.getString("selector")+"')["+obj.getInt("index")+"].click();})()")
            }
        }

        if ( obj.getBoolean("next") == true ) {
            if ( actionStep < actions.length() ) {
                val obj = actions.getJSONObject(actionStep);
                elementAction(obj, webView)
            }
        }
    }
}