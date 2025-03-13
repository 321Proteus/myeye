import UIKit
import GoogleMaps

// UNUSED

@objc public class DeviceInfo: NSObject {
    
    @objc private var mapView: GMSMapView!

    //example code
    @objc public static func getCurrentOSVersion() -> String {
        return UIDevice.current.systemVersion
    }
//    @objc public static func mapView() -> UIView {
//        GMSPlace
//        GMSMapView()
//    }
}
