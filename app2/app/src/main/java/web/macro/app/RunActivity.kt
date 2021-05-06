package web.macro.app

import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_log.toolbar
import kotlinx.android.synthetic.main.activity_run.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.random.Random


class RunActivity : AppCompatActivity() {
    private val TAG = RunActivity::class.qualifiedName

    /*
    actionStep : 동작 카운드
    actions : 전체 동작 JSON
    timeMillis : 동작당 지연 시간 ( 마이크로초 )
    currentUrl : 현재 URL ( Webview page loading 시 업데이트 되도록 되어 있음 )
    */
    var isProgress = false;
    var actionStep = 0
    val actions = JSONArray()
    val timeMillis = 1000L
    var currentUrl = ""
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
    var queue = 0;
    var buyCnt = 0;

    var search : ArrayList<String> = ArrayList();
    var address : ArrayList<String> = ArrayList();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)

        // 셋팅
        val searchTxtFilePath = filesDir.path+"/search.txt"
        readSearchTextFromFile(searchTxtFilePath)

        val addressTxtFilePath = filesDir.path+"/address.txt"
        readAddressTextFromFile(addressTxtFilePath)

        if ( 0 < App.prefs.queue.toString().length ) {
            val temp = App.prefs.queue.toString().split("/")
            queue = Random.nextInt(temp.get(0).toInt(), temp.get(1).toInt());
        }

        if ( 0 < App.prefs.time1.toString().length && 0 < App.prefs.purchase1.toString().length ) {
            timeBuy1.add(App.prefs.time1.toString())
            timeBuy1.add(App.prefs.purchase1.toString())
        }

        if ( 0 < App.prefs.time2.toString().length && 0 < App.prefs.purchase2.toString().length ) {
            timeBuy2.add(App.prefs.time2.toString())
            timeBuy2.add(App.prefs.purchase2.toString())
        }

        if ( 0 < App.prefs.time3.toString().length && 0 < App.prefs.purchase3.toString().length ) {
            timeBuy3.add(App.prefs.time3.toString())
            timeBuy3.add(App.prefs.purchase3.toString())
        }

        if ( 0 < App.prefs.time4.toString().length && 0 < App.prefs.purchase4.toString().length ) {
            timeBuy4.add(App.prefs.time4.toString())
            timeBuy4.add(App.prefs.purchase4.toString())
        }

        val current = LocalDateTime.now()
        val currentDate = current.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val temp = currentDate.toString().split("-")

        if ( timeBuy1.size == 2 ) {
            val timeTemp = timeBuy1.get(0).toString().split("/");
            val startTimeTemp = timeTemp.get(0).toString().split(":");
            val endTimeTemp = timeTemp.get(1).toString().split(":");
            val startTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), startTimeTemp.get(
                    0
                ).toInt(), startTimeTemp.get(1).toInt(), 0, 0
            )
            val endTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                    0
                ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
            )
            if ( current.isAfter(startTime)  &&  current.isBefore(endTime) ) {
                // true
                timeBuy.clear()
                timeBuy.add(timeBuy1.get(0).toString())
                timeBuy.add(timeBuy1.get(1).toString())
            }
        }

        if ( timeBuy2.size == 2 ) {
            val timeTemp = timeBuy2.get(0).toString().split("/");
            val startTimeTemp = timeTemp.get(0).toString().split(":");
            val endTimeTemp = timeTemp.get(1).toString().split(":");
            val startTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), startTimeTemp.get(
                    0
                ).toInt(), startTimeTemp.get(1).toInt(), 0, 0
            )
            val endTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                    0
                ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
            )
            if ( current.isAfter(startTime)  &&  current.isBefore(endTime) ) {
                // true
                timeBuy.clear()
                timeBuy.add(timeBuy2.get(0).toString())
                timeBuy.add(timeBuy2.get(1).toString())
            }
        }

        if ( timeBuy3.size == 2 ) {
            val timeTemp = timeBuy3.get(0).toString().split("/");
            val startTimeTemp = timeTemp.get(0).toString().split(":");
            val endTimeTemp = timeTemp.get(1).toString().split(":");
            val startTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), startTimeTemp.get(
                    0
                ).toInt(), startTimeTemp.get(1).toInt(), 0, 0
            )
            val endTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                    0
                ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
            )
            if ( current.isAfter(startTime)  &&  current.isBefore(endTime) ) {
                // true
                timeBuy.clear()
                timeBuy.add(timeBuy3.get(0).toString())
                timeBuy.add(timeBuy3.get(1).toString())
            }
        }

        if ( timeBuy4.size == 2 ) {
            val timeTemp = timeBuy4.get(0).toString().split("/");
            val startTimeTemp = timeTemp.get(0).toString().split(":");
            val endTimeTemp = timeTemp.get(1).toString().split(":");
            val startTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), startTimeTemp.get(
                    0
                ).toInt(), startTimeTemp.get(1).toInt(), 0, 0
            )
            val endTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                    0
                ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
            )
            if ( current.isAfter(startTime)  &&  current.isBefore(endTime) ) {
                // true
                timeBuy.clear()
                timeBuy.add(timeBuy4.get(0).toString())
                timeBuy.add(timeBuy4.get(1).toString())
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
        action.put("value", search.get(Random.nextInt(0, search.size)))
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
        action.put("selector", ".sp_ntotal .total_tit .link_tit")
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
        action.put("value", "신나라")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 5)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 우편번호
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 우편번호")
        action.put("action", "value")
        action.put("selector", "#rzipcode1")
        action.put("value", "6035")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 기본주소
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 기본주소")
        action.put("action", "value")
        action.put("selector", "#raddr1")
        action.put("value", "서울특별시 강남구 가로수길 9 (신사동)")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 상세주소
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 상세주소")
        action.put("action", "value")
        action.put("selector", "#raddr2")
        action.put("value", "없음")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 전화번호 _ 1
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 전화번호 _ 1")
        action.put("action", "value")
        action.put("selector", "#rphone2_1")
        action.put("value", "017")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 전화번호 _ 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 전화번호 _ 2")
        action.put("action", "value")
        action.put("selector", "#rphone2_2")
        action.put("value", "0001")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 전화번호 _ 3
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 전화번호 _ 3")
        action.put("action", "value")
        action.put("selector", "#rphone2_3")
        action.put("value", "0000")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 이메일 _ 1
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 이메일 _ 1")
        action.put("action", "value")
        action.put("selector", "#oemail1")
        action.put("value", "ergjeorgj")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 이메일 _ 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 이메일 _ 2")
        action.put("action", "value")
        action.put("selector", "#oemail2")
        action.put("value", "test.com")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 비밀번호
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 비밀번호")
        action.put("action", "value")
        action.put("selector", "#order_password")
        action.put("value", "@123@123")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 비밀번호 확인
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 비밀번호 확인")
        action.put("action", "value")
        action.put("selector", "#order_password_confirm")
        action.put("value", "@123@123")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 입금은행
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 입금은행")
        action.put("action", "value")
        action.put("selector", "#bankaccount")
        action.put("value", "bank_81:010-714471-56107:오미라:하나은행:www.hanabank.com")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 입금자명
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 입금은행")
        action.put("action", "value")
        action.put("selector", "#pname")
        action.put("value", "신나라")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 모든 약관 동의
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 모든 약관 동의")
        action.put("action", "click")
        action.put("selector", "#allAgree")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", false)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 이름 입력 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 이름 입력 2")
        action.put("action", "value")
        action.put("selector", "#rname")
        action.put("value", "신나라")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 5)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 우편번호 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 우편번호 2")
        action.put("action", "value")
        action.put("selector", "#rzipcode1")
        action.put("value", "6035")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 기본주소 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 기본주소 2")
        action.put("action", "value")
        action.put("selector", "#raddr1")
        action.put("value", "서울특별시 강남구 가로수길 9 (신사동)")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 상세주소 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 상세주소 2")
        action.put("action", "value")
        action.put("selector", "#raddr2")
        action.put("value", "없음")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 전화번호 _ 1 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 전화번호 _ 1 2")
        action.put("action", "value")
        action.put("selector", "#rphone2_1")
        action.put("value", "017")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 전화번호 _ 2 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 전화번호 _ 2 2")
        action.put("action", "value")
        action.put("selector", "#rphone2_2")
        action.put("value", "0001")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 전화번호 _ 3 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 전화번호 _ 3 2")
        action.put("action", "value")
        action.put("selector", "#rphone2_3")
        action.put("value", "0000")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 이메일 _ 1 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 이메일 _ 1 2")
        action.put("action", "value")
        action.put("selector", "#oemail1")
        action.put("value", "ergjeorgj")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 이메일 _ 2 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 이메일 _ 2 2")
        action.put("action", "value")
        action.put("selector", "#oemail2")
        action.put("value", "test.com")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 비밀번호 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 비밀번호 2")
        action.put("action", "value")
        action.put("selector", "#order_password")
        action.put("value", "@123@123")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 비밀번호 확인 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 비밀번호 확인 2")
        action.put("action", "value")
        action.put("selector", "#order_password_confirm")
        action.put("value", "@123@123")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 입금은행 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 입금은행 2")
        action.put("action", "value")
        action.put("selector", "#bankaccount")
        action.put("value", "bank_81:010-714471-56107:오미라:하나은행:www.hanabank.com")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 입금자명 2
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 입금은행 2")
        action.put("action", "value")
        action.put("selector", "#pname")
        action.put("value", "신나라")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", true)
        actions.put(action)

        // 구매하기 화면 - 비회원구매 - 모든 약관 동의 2
        /*
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 모든 약관 동의 2")
        action.put("action", "click")
        action.put("selector", "#allAgree")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
        action.put("next", false)
        actions.put(action)
        */

        // 구매하기 화면 - 비회원구매 - 결제하기
        action = JSONObject()
        action.put("name", "구매하기 화면 - 비회원구매 - 결제하기")
        action.put("action", "click")
        action.put("selector", "#btn_payment")
        action.put("function", "element")
        action.put("index", 0)
        action.put("delay", 3)
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
                Log.d(TAG, "onPageStarted")

                progressBar.isVisible = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d(TAG, "onPageFinished")
                progressBar.isVisible = false

                Executors.newSingleThreadScheduledExecutor().schedule({
                    val obj = actions.getJSONObject(actionStep);
                    if (obj.getString("action") != "detailClick") {
                        webView.post(Runnable {
                            webView.loadUrl(
                                "javascript:(function(){" +
                                        "window.scrollTo(0, document.body.scrollHeight);" +
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


    }

    fun pageFinishedAction(webView: WebView, url: String) {
        if ( actionStep < actions.length() && currentUrl != url.toString() )  {
            currentUrl = url.toString()
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
            if ( obj.has("delay") ) {
                webView.loadUrl(
                    "javascript:(function(){" +
                    "setTimeout(function() {" +
                    "document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt("index") + "].value='" + obj.getString("value") + "';" +
                    "console.log(document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt("index") + "].value);"+
                    "}, 500);" +
                    "})()"
                )
            } else {
                webView.loadUrl(
                    "javascript:(function(){document.querySelectorAll('" + obj.getString("selector") + "')[" + obj.getInt(
                        "index"
                    ) + "].value='" + obj.getString("value") + "'})()"
                )
            }
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
        } else if ( obj.getString("action") == "detailClick" ) {
            webView.loadUrl(
                "javascript:(function(){" +
                        // "document.querySelectorAll('" + obj.getString("selector") + "')[0].setAttribute('target','_self')" +
                        "document.querySelectorAll('" + obj.getString("selector") + "')[0].click();" +
                        "})()"
            )

        } else if ( obj.getString("action") == "listSearchClick" ) {
            webView.loadUrl(
                "javascript:(function(){" +
                        "setTimeout(function() {" +
                        "var item = document.querySelectorAll('" + obj.getString("selector") + "');" +
                        "for ( var i = 0; i < item.length; i++ ) {" +
                        "window.scrollTo(0, document.body.scrollHeight);" +
                        "if ( Number(item[i].getAttribute('data-i')) == " + obj.getString("data_i") + " ) {" +
                        // "alert('" + obj.getString("data_i") + "');" +
                        "if ( item[i].getAttribute('target') == '_blank') { item[i].setAttribute('target','_self') };" +
                        "window.scrollTo(0, document.body.scrollHeight);" +
                        "item[i].click();" +
                        "break;" +
                        // "alert(item[i].getAttribute('href'));"+
                        //"item[i].click();"+
                        "}" +
                        "}" +
                        "}, 2000);" +
                        "})()"
            )
        } else if ( obj.getString("action") == "productListSearchClick" ) {
            webView.loadUrl(
                "javascript:(function(){" +
                        "window.scrollTo(0, document.body.scrollHeight);" +
                        "var productSearch = function () {" +
                        "setTimeout(function() {" +
                        "var item = document.querySelectorAll('" + obj.getString("selector") + "');" +
                        "var selectProduct = false;" +
                        "for ( var i = 0; i < item.length; i++ ) {" +
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
                        "}, 500);" +
                        "};" +
                        "}, 2000);" +
                        "};" +
                        "productSearch();" +
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
    }

    fun play() {
        Log.d(TAG, "timeBuy.size : " + timeBuy.size)

        val current = LocalDateTime.now()
        val currentDate = current.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val temp = currentDate.toString().split("-")

        if ( timeBuy1.size == 2 ) {
            val timeTemp = timeBuy1.get(0).toString().split("/");
            val startTimeTemp = timeTemp.get(0).toString().split(":");
            val endTimeTemp = timeTemp.get(1).toString().split(":");
            val startTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), startTimeTemp.get(
                    0
                ).toInt(), startTimeTemp.get(1).toInt(), 0, 0
            )
            val endTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                    0
                ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
            )
            if ( current.isAfter(startTime)  &&  current.isBefore(endTime) ) {
                // true
                timeBuy.clear()
                timeBuy.add(timeBuy1.get(0).toString())
                timeBuy.add(timeBuy1.get(1).toString())
            }
        }

        if ( timeBuy2.size == 2 ) {
            val timeTemp = timeBuy2.get(0).toString().split("/");
            val startTimeTemp = timeTemp.get(0).toString().split(":");
            val endTimeTemp = timeTemp.get(1).toString().split(":");
            val startTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), startTimeTemp.get(
                    0
                ).toInt(), startTimeTemp.get(1).toInt(), 0, 0
            )
            val endTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                    0
                ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
            )
            if ( current.isAfter(startTime)  &&  current.isBefore(endTime) ) {
                // true
                timeBuy.clear()
                timeBuy.add(timeBuy2.get(0).toString())
                timeBuy.add(timeBuy2.get(1).toString())
            }
        }

        if ( timeBuy3.size == 2 ) {
            val timeTemp = timeBuy3.get(0).toString().split("/");
            val startTimeTemp = timeTemp.get(0).toString().split(":");
            val endTimeTemp = timeTemp.get(1).toString().split(":");
            val startTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), startTimeTemp.get(
                    0
                ).toInt(), startTimeTemp.get(1).toInt(), 0, 0
            )
            val endTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                    0
                ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
            )
            if ( current.isAfter(startTime)  &&  current.isBefore(endTime) ) {
                // true
                timeBuy.clear()
                timeBuy.add(timeBuy3.get(0).toString())
                timeBuy.add(timeBuy3.get(1).toString())
            }
        }

        if ( timeBuy4.size == 2 ) {
            val timeTemp = timeBuy4.get(0).toString().split("/");
            val startTimeTemp = timeTemp.get(0).toString().split(":");
            val endTimeTemp = timeTemp.get(1).toString().split(":");
            val startTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), startTimeTemp.get(
                    0
                ).toInt(), startTimeTemp.get(1).toInt(), 0, 0
            )
            val endTime = LocalDateTime.of(
                temp.get(0).toInt(), temp.get(1).toInt(), temp.get(2).toInt(), endTimeTemp.get(
                    0
                ).toInt(), endTimeTemp.get(1).toInt(), 0, 0
            )
            if ( current.isAfter(startTime)  &&  current.isBefore(endTime) ) {
                // true
                timeBuy.clear()
                timeBuy.add(timeBuy4.get(0).toString())
                timeBuy.add(timeBuy4.get(1).toString())
            }
        }

        if ( timeBuy.size == 2 ) {
            if ( buyCnt < timeBuy.get(1).toInt() ) {
                isProgress = true
                actionStep = 0
                web_view.loadUrl(rootUrl)

                btn_run.setImageDrawable(getDrawable(R.drawable.ic_stop))
            } else {
                Toast.makeText(this@RunActivity, "Failed", Toast.LENGTH_SHORT).show()
                stop();
            }
        } else {
            Toast.makeText(this@RunActivity, "Failed", Toast.LENGTH_SHORT).show()
            stop();
        }
    }

    fun stop() {
        isProgress = false
        btn_run.setImageDrawable(getDrawable(R.drawable.ic_play))
    }

    fun readSearchTextFromFile(path: String) {
        val file = File(path)
        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)
        var txt = "";
        bufferedReader.readLines().forEach() {
            txt = txt+it;
        }

        txt.split(",").forEach{ row ->
            search.add(row)
        }

        Log.i(TAG, search.get(0));
        Log.i(TAG, search.get(1));
        Log.i(TAG, search.get(2));
        Log.i(TAG, search.get(Random.nextInt(0, search.size)));
    }

    fun readAddressTextFromFile(path: String) {
        val file = File(path)
        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)
        var txt = "";
        bufferedReader.readLines().forEach() {
            address.add(it)
        }

        Log.i(TAG, address.get(0));
        Log.i(TAG, address.get(1));
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}