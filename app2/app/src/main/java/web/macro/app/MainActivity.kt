package web.macro.app

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    // Actions
    var actionStep = 0
    val actions = JSONArray()
    val timeMillis = 1000L // 실행 속도 ( 마이크로초 )
    var currentUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRun: View = findViewById(R.id.btn_run);
        val webView: WebView = findViewById(R.id.web_view)

        val rootUrl = "https://m.naver.com" // 첫 홈페이지
        val rootShopUrl = "https://m.shopping.naver.com/home/m/index.nhn" // 쇼핑 홈페이지

        // Actions
        var action = JSONObject()

        // 검색 포커스
        action = JSONObject()
        action.put("name", "검색 포커스")
        action.put("action", "focus")
        action.put("position", "m.naver.com")
        action.put("selector", "#MM_SEARCH_FAKE")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", true)
        actions.put(action)

        // 검색어 입력
        action = JSONObject()
        action.put("name", "검색어 입력")
        action.put("action", "value")
        action.put("position", "m.naver.com")
        action.put("selector", "#query")
        action.put("value", "해운대")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", true)
        actions.put(action)

        // 검색어 폼 전송
        action = JSONObject()
        action.put("name", "검색어 폼 전송")
        action.put("action", "click")
        action.put("position", "m.naver.com")
        action.put("selector", ".sch_submit")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", false)
        actions.put(action)

        // 검색어 결과 클릭
        action = JSONObject()
        action.put("name", "검색어 결과 클릭")
        action.put("action", "click")
        action.put("position", "m.naver.com")
        action.put("selector", ".sp_nnews .news_wrap .news_tit")
        action.put("function", "element")
        action.put("index", "random")
        action.put("next", false)
        actions.put(action)

        // 뒤로가기
        action = JSONObject()
        action.put("name", "뒤로가기")
        action.put("action", "back")
        action.put("position", "m.naver.com")
        action.put("function", "webview")
        action.put("next", false)
        actions.put(action)

        // 검색어 결과 클릭
        action = JSONObject()
        action.put("name", "검색어 결과 클릭")
        action.put("action", "click")
        action.put("position", "m.naver.com")
        action.put("selector", ".sp_nnews .news_wrap .news_tit")
        action.put("function", "element")
        action.put("index", "random")
        action.put("next", false)
        actions.put(action)

        // 뒤로가기
        action = JSONObject()
        action.put("name", "뒤로가기")
        action.put("action", "back")
        action.put("position", "m.naver.com")
        action.put("function", "webview")
        action.put("next", false)
        actions.put(action)

        // 검색 포커스
        action = JSONObject()
        action.put("name", "검색 포커스")
        action.put("action", "focus")
        action.put("position", "m.naver.com")
        action.put("selector", "#nx_query")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", true)
        actions.put(action)

        // 검색어 입력
        action = JSONObject()
        action.put("name", "검색어 입력")
        action.put("action", "value")
        action.put("position", "m.naver.com")
        action.put("selector", "#nx_query")
        action.put("value", "비타민나무")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", true)
        actions.put(action)

        // 검색어 폼 전송
        action = JSONObject()
        action.put("name", "검색어 폼 전송")
        action.put("action", "click")
        action.put("position", "m.naver.com")
        action.put("selector", ".btn_search")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", false)
        actions.put(action)

        // 검색어 결과 쇼핑 클릭
        action = JSONObject()
        action.put("name", "검색어 결과 쇼핑 클릭")
        action.put("action", "click")
        action.put("position", "m.naver.com")
        action.put("selector", ".type_white .sch_tab .lst_sch .bx a")
        action.put("function", "element")
        action.put("index", 1)
        action.put("next", false)
        actions.put(action)

        // 쇼핑홈으로 이동
        action = JSONObject()
        action.put("name", "쇼핑홈으로 이동")
        action.put("action", "url")
        action.put("position", "m.naver.com")
        action.put("function", "url")
        action.put("url", rootShopUrl)
        actions.put(action)

        // 검색 클릭
        action = JSONObject()
        action.put("name", "검색 클릭")
        action.put("action", "click")
        action.put("position", "m.naver.com")
        action.put("selector", "#sear")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", true)
        actions.put(action)

        // 검색 포커스
        action = JSONObject()
        action.put("name", "검색 포커스")
        action.put("action", "focus")
        action.put("position", "m.naver.com")
        action.put("selector", "#sear")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", true)
        actions.put(action)

        // 검색어 입력
        action = JSONObject()
        action.put("name", "검색어 입력")
        action.put("action", "value")
        action.put("position", "m.naver.com")
        action.put("selector", "#sear")
        action.put("value", "비타민나무")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", true)
        actions.put(action)

        // 검색어 폼 전송
        action = JSONObject()
        action.put("name", "검색어 폼 전송")
        action.put("action", "submit")
        action.put("position", "m.naver.com")
        action.put("selector", "#searchForm")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", false)
        actions.put(action)

        // 쇼핑 검색어 결과 상품 찾기후 클릭
        action = JSONObject()
        action.put("name", "쇼핑 검색어 결과 상품 찾기후 클릭")
        action.put("action", "listSearchClick")
        action.put("position", "m.naver.com")
        action.put("selector", ".product_list_item__2tuKA a.product_info_main__1RU2S")
        action.put("function", "element")
        action.put("data_i", 25184334522)
        action.put("next", false)
        actions.put(action)

        // 쇼핑 상세보기 에서 구매하기 화면으로 이동
        action = JSONObject()
        action.put("name", "쇼핑 상세보기 에서 구매하기 화면으로 이동")
        action.put("action", "listSearchClick")
        action.put("position", "m.naver.com")
        action.put("selector", ".productPerMall_seller_item__jcayW .productPerMall_link_seller__3GSdU")
        action.put("function", "element")
        action.put("data_i", 21499083928)
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
                Log.i("MainActivity", "onPageStarted")
            }

            override fun onPageFinished(view: WebView?, url: String?) {

                Toast.makeText(applicationContext, "onPageFinished", Toast.LENGTH_LONG).show()
                Log.i("MainActivity", "aa onPageFinished url : " + url)

                Log.i("MainActivity", "aa actions : " + actions.length())
                // view.scrollY(0,view.contentHeight())

                if ( actionStep < actions.length() && currentUrl != url.toString() )  {
                    currentUrl = url.toString()
                    val obj = actions.getJSONObject(actionStep);
                    obj.put("step", actionStep)
                    actionStep = actionStep + 1
                    Log.i("MainActivity", "aa actionStep onPageFinished : " + obj.getInt("step"))
                    GlobalScope.launch(context = Dispatchers.Main) {
                        delay(timeMillis)
                        Log.i(
                            "MainActivity",
                            "step info === step:" + obj.getInt("step") + "/name:" + obj.getString(
                                "name"
                            ) + "/action:" + obj.getString("action")
                        )
                        if ( obj.get("function") == "element" ) {
                            elementAction(obj, webView)
                        } else if ( obj.get("function") == "webview" ) {
                            backAction(obj, webView)
                        } else if ( obj.get("function") == "url" ) {
                            urlAction(obj, webView)
                        }
                    }
                }
            }
        }

        // Set web view chrome client
        webView.webChromeClient = object: WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.i("MainActivity", "newProgress:" + newProgress)
                if ( 100 <= newProgress ) {
                    Log.i("MainActivity", "aa actionStep onPageFinishedaa :")
                }
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
    fun urlAction(obj: JSONObject, webView: WebView) {
        webView.loadUrl(obj.getString("url"))
    }
    fun backAction(obj: JSONObject, webView: WebView) {
        if ( obj.getString("action") == "back" ) {
            webView.goBack()
        }
    }
    fun elementAction(obj: JSONObject, webView: WebView) {
        if ( obj.getString("action") == "focus" ) {
            webView.loadUrl(
                "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                    "index"
                ) + "].focus();})()"
            )
            Log.i("MainActivity", "aa focusfocusfocusfocusfocusfocusfocusfocus")
        } else if ( obj.getString("action") == "value" ) {
            webView.loadUrl(
                "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                    "index"
                ) + "].value='" + obj.getString("value") + "'})()"
            )
        } else if ( obj.getString("action") == "click" ) {
            if ( obj.getString("index") == "random" ) {
                webView.loadUrl(
                    "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[Math.floor(Math.random() * document.querySelectorAll('" + obj.getString(
                        "selector"
                    ) + "').length)].click();})()"
                )
            } else {
                webView.loadUrl(
                    "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                        "index"
                    ) + "].click();})()"
                )
            }
        } else if ( obj.getString("action") == "submit" ) {
            webView.loadUrl(
                "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                    "index"
                ) + "].submit();})()"
            )
        } else if ( obj.getString("action") == "listSearchClick" ) {
            Log.i("MainActivity", "aa actionStep listSearchClick")

            // webView.loadUrl("javascript:(function(){document.querySelectorAll('"+obj.getString("selector")+"')[0].click();})()")

            webView.loadUrl(
                "javascript:(function(){" +
                        "setTimeout(function() {"+
                        "var item = document.querySelectorAll('" + obj.getString("selector") + "');" +
                        "for ( var i = 0; i < item.length; i++ ) {" +
                        "window.scrollTo(0, item[i].getBoundingClientRect().top);"+
                        "if ( Number(item[i].getAttribute('data-i')) == " + obj.getString("data_i") + " ) {" +
                        // "alert('" + obj.getString("data_i") + "');" +
                        "if ( item[i].getAttribute('target') == '_blank') { item[i].setAttribute('target','_self') };"+
                        "window.scrollTo(0, item[i].getBoundingClientRect().top);"+
                        "item[i].click();"+
                        "break;"+
                        // "alert(item[i].getAttribute('href'));"+
                        //"item[i].click();"+
                        "}" +
                        "}" +
                        "}, 2000);"+
                        "})()"
            )
        }

        if ( obj.getBoolean("next") == true ) {
            if ( actionStep < actions.length() ) {
                val obj = actions.getJSONObject(actionStep);
                obj.put("step", actionStep)

                actionStep = actionStep + 1
                Log.i("MainActivity", "aa actionStep next : " + obj.getInt("step"))
                GlobalScope.launch(context = Dispatchers.Main) {
                    delay(timeMillis)
                    Log.i(
                        "MainActivity",
                        "step info === step:" + obj.getInt("step") + "/name:" + obj.getString(
                            "name"
                        ) + "/action:" + obj.getString("action")
                    )
                    if ( obj.get("function") == "element" ) {
                        elementAction(obj, webView)
                    } else if ( obj.get("function") == "webview" ) {
                        backAction(obj, webView)
                    } else if ( obj.get("function") == "url" ) {
                        urlAction(obj, webView)
                    }
                }
            }
        }
    }
}