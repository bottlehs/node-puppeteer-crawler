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

    // 실행 코드
    var executionCdoe: String?
        get() = prefs.getString("prefsKeyExecutionCdoe","")
        set(value) = prefs.edit().putString("prefsKeyExecutionCdoe", value).apply()

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

    // product 1
    // 상품명
    var productName1: String?
        get() = prefs.getString("prefsKeyProductName1","")
        set(value) = prefs.edit().putString("prefsKeyProductName1", value).apply()

    // 상품링크 ID
    var productId1: String?
        get() = prefs.getString("prefsKeyProductId1","")
        set(value) = prefs.edit().putString("prefsKeyProductId1", value).apply()

    // 구매 링크 ID
    var purchaseId1: String?
        get() = prefs.getString("prefsKeyPurchaseId1","")
        set(value) = prefs.edit().putString("prefsKeyPurchaseId1", value).apply()

    // product 2
    // 상품명
    var productName2: String?
        get() = prefs.getString("prefsKeyProductName2","")
        set(value) = prefs.edit().putString("prefsKeyProductName2", value).apply()

    // 상품링크 ID
    var productId2: String?
        get() = prefs.getString("prefsKeyProductId2","")
        set(value) = prefs.edit().putString("prefsKeyProductId2", value).apply()

    // 구매 링크 ID
    var purchaseId2: String?
        get() = prefs.getString("prefsKeyPurchaseId2","")
        set(value) = prefs.edit().putString("prefsKeyPurchaseId2", value).apply()

    // product 3
    // 상품명
    var productName3: String?
        get() = prefs.getString("prefsKeyProductName3","")
        set(value) = prefs.edit().putString("prefsKeyProductName3", value).apply()

    // 상품링크 ID
    var productId3: String?
        get() = prefs.getString("prefsKeyProductId3","")
        set(value) = prefs.edit().putString("prefsKeyProductId3", value).apply()

    // 구매 링크 ID
    var purchaseId3: String?
        get() = prefs.getString("prefsKeyPurchaseId3","")
        set(value) = prefs.edit().putString("prefsKeyPurchaseId3", value).apply()

    // product 4
    // 상품명
    var productName4: String?
        get() = prefs.getString("prefsKeyProductName4","")
        set(value) = prefs.edit().putString("prefsKeyProductName4", value).apply()

    // 상품링크 ID
    var productId4: String?
        get() = prefs.getString("prefsKeyProductId4","")
        set(value) = prefs.edit().putString("prefsKeyProductId4", value).apply()

    // 구매 링크 ID
    var purchaseId4: String?
        get() = prefs.getString("prefsKeyPurchaseId4","")
        set(value) = prefs.edit().putString("prefsKeyPurchaseId4", value).apply()

    // product 5
    // 상품명
    var productName5: String?
        get() = prefs.getString("prefsKeyProductName5","")
        set(value) = prefs.edit().putString("prefsKeyProductName5", value).apply()

    // 상품링크 ID
    var productId5: String?
        get() = prefs.getString("prefsKeyProductId5","")
        set(value) = prefs.edit().putString("prefsKeyProductId5", value).apply()

    // 구매 링크 ID
    var purchaseId5: String?
        get() = prefs.getString("prefsKeyPurchaseId5","")
        set(value) = prefs.edit().putString("prefsKeyPurchaseId5", value).apply()

    // product 6
    // 상품명
    var productName6: String?
        get() = prefs.getString("prefsKeyProductName6","")
        set(value) = prefs.edit().putString("prefsKeyProductName6", value).apply()

    // 상품링크 ID
    var productId6: String?
        get() = prefs.getString("prefsKeyProductId6","")
        set(value) = prefs.edit().putString("prefsKeyProductId6", value).apply()

    // 구매 링크 ID
    var purchaseId6: String?
        get() = prefs.getString("prefsKeyPurchaseId6","")
        set(value) = prefs.edit().putString("prefsKeyPurchaseId6", value).apply()

    // 익일 자동실행
    var nextAuto: String?
        get() = prefs.getString("prefsKeyNextAuto","N")
        set(value) = prefs.edit().putString("prefsKeyNextAuto", value).apply()

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