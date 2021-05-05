package web.macro.app

import android.content.Context
import android.content.SharedPreferences

class MacroSharedPreferences(context: Context) {
    private val prefsFilename = "prefs"

    /**
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
}