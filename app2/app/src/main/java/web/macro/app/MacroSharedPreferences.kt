package web.macro.app

import android.content.Context
import android.content.SharedPreferences

class MacroSharedPreferences(context: Context) {
    private val prefsFilename = "prefs"

    /**
     * # 설정
     *
     * 시간대/구매 ~ 4
     * 07:30 ~ 10:30 (String) / 1 (Int)
     * time1 / purchase1
     * time2 / purchase2
     * time3 / purchase3
     * time4 / purchase4
     *
     * 체류(MIN/MAX) ~ 1
     * 1/30 (String)
     * queue
     *
     * 상품명
     * 빵또아 (String)
     * productName
     *
     * 상품링크 ID
     * http://domain.com (String)
     * productId
     *
     * 구매 링크 ID
     * http://domain.com (String)
     * purchaseId
     *
     * 익일 자동실행
     * true (String)
     * nextAuto
     *
     * # 실행
     * 검색어 위치
     * 0 (Int)
     * searchPosition
     *
     * 주소 위치
     * 0 (Int)
     * addressPosition
     *
     * 실행되는 날짜
     * 2021-05-09 (String)
     * playDate
     *
     * 비행기모드
     * 0 (Int)
     * airplaneMode
     */
    private val prefs : SharedPreferences = context.getSharedPreferences(prefsFilename, 0)

    // 시간대/구매 ~ 4
    var time1: String?
        get() = prefs.getString("prefsKeyTime1","")
        set(value) = prefs.edit().putString("prefsKeyTime1", value).apply()
    var time2: String?
        get() = prefs.getString("prefsKeyTime2","")
        set(value) = prefs.edit().putString("prefsKeyTime2", value).apply()
    var time3: String?
        get() = prefs.getString("prefsKeyTime3","")
        set(value) = prefs.edit().putString("prefsKeyTime3", value).apply()
    var time4: String?
        get() = prefs.getString("prefsKeyTime4","")
        set(value) = prefs.edit().putString("prefsKeyTime4", value).apply()
    var purchase1: String?
        get() = prefs.getString("prefsKeyPurchase1","")
        set(value) = prefs.edit().putString("prefsKeyPurchase1", value).apply()
    var purchase2: String?
        get() = prefs.getString("prefsKeyPurchase2","")
        set(value) = prefs.edit().putString("prefsKeyPurchase2", value).apply()
    var purchase3: String?
        get() = prefs.getString("prefsKeyPurchase3","")
        set(value) = prefs.edit().putString("prefsKeyPurchase3", value).apply()
    var purchase4: String?
        get() = prefs.getString("prefsKeyPurchase4","")
        set(value) = prefs.edit().putString("prefsKeyPurchase4", value).apply()

    // 체류(MIN/MAX) ~ 1
    var queue: String?
        get() = prefs.getString("prefsKeyQueue","")
        set(value) = prefs.edit().putString("prefsKeyQueue", value).apply()

    // 상품명
    var productName: String?
        get() = prefs.getString("prefsKeyProductName","")
        set(value) = prefs.edit().putString("prefsKeyProductName", value).apply()

    // 상품링크 ID
    var productId: String?
        get() = prefs.getString("prefsKeyProductId","")
        set(value) = prefs.edit().putString("prefsKeyProductId", value).apply()

    // 구매 링크 ID
    var purchaseId: String?
        get() = prefs.getString("prefsKeyPurchaseId","")
        set(value) = prefs.edit().putString("prefsKeyPurchaseId", value).apply()

    // 익일 자동실행
    var nextAuto: String?
        get() = prefs.getString("prefsKeyNextAuto","N")
        set(value) = prefs.edit().putString("prefsKeyNextAuto", value).apply()

    // 검색어 위치
    var searchPosition: String?
        get() = prefs.getString("prefsKeySearchPosition","0")
        set(value) = prefs.edit().putString("prefsKeySearchPosition", value).apply()

    // 주소 위치
    var addressPosition: String?
        get() = prefs.getString("prefsKeyAddressPosition","0")
        set(value) = prefs.edit().putString("prefsKeyAddressPosition", value).apply()

    // 실행되는 날짜
   var playDate: String?
        get() = prefs.getString("prefsKeyPlayDate","")
        set(value) = prefs.edit().putString("prefsKeyPlayDate", value).apply()

    // 비행기 모드
   var airplaneMode: String?
       get() = prefs.getString("prefsKeyAirplaneMode","0")
       set(value) = prefs.edit().putString("prefsKeyAirplaneMode", value).apply()

    var backButtonSizeX: String?
        get() = prefs.getString("prefsKeyBackButtonSizeX","0")
        set(value) = prefs.edit().putString("prefsKeyBackButtonSizeX", value).apply()
}