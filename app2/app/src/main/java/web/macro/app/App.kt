package web.macro.app

import android.app.Application

class App : Application() {
    companion object {
        lateinit var prefs : MacroSharedPreferences
    }

    override fun onCreate() {
        prefs = MacroSharedPreferences(applicationContext)
        super.onCreate()
    }
}