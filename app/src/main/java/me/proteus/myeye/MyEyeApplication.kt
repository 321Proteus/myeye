package me.proteus.myeye

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log

class MyEyeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.e("MyEyeApplication", "onCreate: App created")
    }

    override fun attachBaseContext(base: Context) {
        val currentLang = LanguageUtils.getCurrentLanguage(base)
        val newContext = LanguageUtils.setLocale(base, currentLang)
        Log.e("MyEyeApplication", "attachBaseContext: Context attached")
        super.attachBaseContext(newContext)
    }

    fun setAppLanguage(activity: Activity, language: String) {

        LanguageUtils.saveLanguage(activity, language)
        LanguageUtils.getCurrentLanguage(activity)

        val intent = activity.intent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()

    }

}