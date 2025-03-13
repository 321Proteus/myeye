import SwiftUI
import GoogleMaps
import GooglePlaces

@main
struct iOSApp: App {

    init() {
        print("sigma")
        let key = "AIzaSyD33Ph-HlL8gtc08VZ4d07LH6dPlp71v3M"

        GMSServices.provideAPIKey(key)
        let a = GMSPlacesClient.provideAPIKey(key)
        print(a)
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
