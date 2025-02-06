package me.proteus.myeye.io;

import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.core.resolutionselector.ResolutionStrategy;
import android.util.Size;

public class CameraUtils {

    public static ResolutionStrategy createStrategy(Size deviceSize) {

        return new ResolutionStrategy(deviceSize, ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER);

    }

    public static ResolutionSelector createSelector(Size deviceSize) {

        return new ResolutionSelector.Builder().setResolutionStrategy(createStrategy(deviceSize)).build();

    }

}
