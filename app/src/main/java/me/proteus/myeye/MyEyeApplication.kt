package me.proteus.myeye

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import me.proteus.myeye.ui.SpeechDecoderActivity
import java.util.Locale

class MyEyeApplication : Application() {

    val languages = listOf("pl", "en")
    var currentLang = 0

    override fun attachBaseContext(base: Context) {
        val prefs = base.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val language = prefs.getString("lang", "en") ?: "en"
        val newContext = setLocale(base, language)
        super.attachBaseContext(newContext)
    }

    private fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putString("lang", language).apply()
    }

    private fun setLocale(context: Context, lang: String): Context {

        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)

    }

    fun setAppLanguage(activity: Activity, language: String) {

        val intent = Intent(activity, SpeechDecoderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()

    }

}