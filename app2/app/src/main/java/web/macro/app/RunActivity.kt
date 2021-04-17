package web.macro.app

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
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

class RunActivity : AppCompatActivity() {
    private val TAG = "RunActivity"

    /*
    actionStep : 동작 카운드
    actions : 전체 동작 JSON
    timeMillis : 동작당 지연 시간 ( 마이크로초 )
    currentUrl : 현재 URL ( Webview page loading 시 업데이트 되도록 되어 있음 )
    */
    var actionStep = 0
    val actions = JSONArray()
    val timeMillis = 1000L
    var currentUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)

        /*
        btnRun (View) : 실행 버튼 ( res/layout/activity_main.xml btn_run 을 참조함 )
        webView (WebView) : 웹뷰 ( res/layout/activity_main.xml web_view 을 참조함 )
        rootUrl : 첫 홈페이지
        rootShopUrl : 쇼핑 홈페이지                
        */
        val btnRun: View = findViewById(R.id.btn_run);
        val webView: WebView = findViewById(R.id.web_view)
        val rootUrl = "https://m.naver.com" // 첫 홈페이지
        val rootShopUrl = "https://m.shopping.naver.com/home/m/index.nhn" // 쇼핑 홈페이지


        /*
        action : actions 에 추가할 json object 선언
         */
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
        action.put("value", "해운대")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", true)
        actions.put(action)

        // 검색어 폼 전송
        action = JSONObject()
        action.put("name", "검색어 폼 전송")
        action.put("action", "click")
        action.put("selector", ".sch_submit")
        action.put("function", "element")
        action.put("index", 0)
        action.put("next", false)
        actions.put(action)

        // 검색어 결과 클릭
        action = JSONObject()
        action.put("name", "검색어 결과 클릭")
        action.put("action", "click")
        action.put("selector", ".sp_nnews .news_wrap .news_tit")
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
        action.put("selector", ".sp_nnews .news_wrap .news_tit")
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
        action.put("value", "비타민나무")
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
        action.put("value", "비타민나무")
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
        action.put("action", "listSearchClick")
        action.put("selector", ".product_list_item__2tuKA a.product_info_main__1RU2S")
        action.put("function", "element")
        action.put("data_i", 25184334522)
        action.put("next", false)
        actions.put(action)

        // 쇼핑 상세보기 에서 구매하기 화면으로 이동
        action = JSONObject()
        action.put("name", "쇼핑 상세보기 에서 구매하기 화면으로 이동")
        action.put("action", "listSearchClick")
        action.put(
            "selector",
            ".productPerMall_seller_item__jcayW .productPerMall_link_seller__3GSdU"
        )
        action.put("function", "element")
        action.put("data_i", 21499083928)
        action.put("next", false)
        actions.put(action)

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
                Toast.makeText(applicationContext, "onPageStarted", Toast.LENGTH_LONG).show()
                Log.i(TAG, "onPageStarted")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Toast.makeText(applicationContext, "onPageFinished", Toast.LENGTH_LONG).show()
                Log.i(TAG, "onPageFinished")

                if ( actionStep < actions.length() && currentUrl != url.toString() )  {
                    currentUrl = url.toString()
                    val obj = actions.getJSONObject(actionStep);
                    obj.put("step", actionStep)
                    actionStep = actionStep + 1
                    GlobalScope.launch(context = Dispatchers.Main) {
                        delay(timeMillis)
                        Toast.makeText(
                            applicationContext,
                            "" + obj.getInt("step") + "-" + obj.getString("name"),
                            Toast.LENGTH_LONG
                        ).show()
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
                Log.i(TAG, "onProgressChanged : " + newProgress)
                if ( 100 <= newProgress ) {
                    Toast.makeText(
                        applicationContext,
                        "onProgressChanged Complete",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i(TAG, "onProgressChanged Complete")
                }
            }
        }

        btnRun.setOnClickListener(View.OnClickListener {
            actionStep = 0
            webView.loadUrl(rootUrl)
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
            webView.loadUrl(
                "javascript:(function(){" +
                        "setTimeout(function() {" +
                        "var item = document.querySelectorAll('" + obj.getString("selector") + "');" +
                        "for ( var i = 0; i < item.length; i++ ) {" +
                        "window.scrollTo(0, item[i].getBoundingClientRect().top);" +
                        "if ( Number(item[i].getAttribute('data-i')) == " + obj.getString("data_i") + " ) {" +
                        // "alert('" + obj.getString("data_i") + "');" +
                        "if ( item[i].getAttribute('target') == '_blank') { item[i].setAttribute('target','_self') };" +
                        "window.scrollTo(0, item[i].getBoundingClientRect().top);" +
                        "item[i].click();" +
                        "break;" +
                        // "alert(item[i].getAttribute('href'));"+
                        //"item[i].click();"+
                        "}" +
                        "}" +
                        "}, 2000);" +
                        "})()"
            )
        }

        if ( obj.getBoolean("next") == true ) {
            if ( actionStep < actions.length() ) {
                val obj = actions.getJSONObject(actionStep);
                obj.put("step", actionStep)

                actionStep = actionStep + 1
                GlobalScope.launch(context = Dispatchers.Main) {
                    delay(timeMillis)
                    Toast.makeText(
                        applicationContext,
                        "" + obj.getInt("step") + "-" + obj.getString("name"),
                        Toast.LENGTH_LONG
                    ).show()
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