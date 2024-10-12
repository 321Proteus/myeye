package me.proteus.myeye;

import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenScalingUtils {

    public static ScreenInfo getScreenInfo(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float densityDpi = metrics.densityDpi;

        return new ScreenInfo(width, height, densityDpi);

    }

    public static class ScreenInfo {
        private final int width;
        private final int height;
        private final float densityDpi;

        public ScreenInfo(int width, int height, float density) {
            this.width = width;
            this.height = height;
            this.densityDpi = density;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public float getDensityDpi() {
            return densityDpi;
        }

    }
}
