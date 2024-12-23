package me.proteus.myeye;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import java.util.Locale;

public class LanguageUtils {

    public static String getCurrentLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return prefs.getString("lang", "en");
    }

     public static void saveLanguage(Context context, String language) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("lang", language).apply();
        Log.e("MyEyeApplication", "getCurrentLanguage: " + getCurrentLanguage(context));
    }

    public static Context setLocale(Context context, String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration resConfig = resources.getConfiguration();

        Configuration config = new Configuration(resConfig);
        config.setLocale(locale);

        return context.createConfigurationContext(config);

    }

}