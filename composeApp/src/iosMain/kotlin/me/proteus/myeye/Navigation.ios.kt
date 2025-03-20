package me.proteus.myeye

import androidx.compose.runtime.Composable
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation

typealias UID = UIDeviceOrientation

@Composable
actual fun isLandscape(): Boolean {
    val o = UIDevice.currentDevice.orientation
    return o == UID.UIDeviceOrientationLandscapeLeft ||
            o == UIDeviceOrientation.UIDeviceOrientationLandscapeRight

//    return UIDevice.currentDevice.orientation == UID.LandscapeLeft ||
//            UIDevice.currentDevice.orientation == UIDeviceOrientation.LandscapeRight
}