package me.proteus.myeye;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import java.util.Locale;

class LanguageUtils {

    static String getCurrentLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return prefs.getString("lang", "en");
    }

     static void saveLanguage(Context context, String language) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("lang", language).apply();
        Log.e("MyEyeApplication", "getCurrentLanguage: " + getCurrentLanguage(context));
    }

    static Context setLocale(Context context, String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);

    }

}