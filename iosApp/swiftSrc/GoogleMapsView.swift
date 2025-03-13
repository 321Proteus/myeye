//
//  GoogleMapsView.swift
//  iosApp
//
//  Created by Bartłomiej Gajda on 03/03/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import UIKit
import GoogleMaps
import GooglePlaces
import CoreLocation
//import composeApp

typealias iOSLatLng = CLLocationCoordinate2D

@objc public class GoogleMapsView: UIView, GMSMapViewDelegate, CLLocationManagerDelegate {

    private var mapView: GMSMapView?
    private var locationManager = CLLocationManager()
    private var locationMarker: GMSMarker?
    private var range: GMSPolygon?
    weak var delegate: ViewUpdateDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)
        initLocation()
        setupMap()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        initLocation()
        setupMap()
    }
    
    func initLocation() {
        locationManager.delegate = self
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
    }
    
    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        
        let newCamera = GMSCameraPosition.camera(
            withLatitude: location.coordinate.latitude,
            longitude: location.coordinate.longitude,
            zoom: 15.0
        )
        
        mapView?.animate(to: newCamera)
        
        locationManager.stopUpdatingLocation()
    }
    
    @objc public func setupMap() {
        let camera = GMSCameraPosition.camera(
            withLatitude: 37.7749,
            longitude: -122.4194,
            zoom: 13.0
        )
        let options = GMSMapViewOptions()
        options.camera = camera

        mapView = GMSMapView.init(options: options)
        mapView?.autoresizingMask = [.flexibleWidth, .flexibleHeight]
//        mapView?.isMyLocationEnabled = true
        mapView?.delegate = self

        if let mapView = mapView {
            addSubview(mapView)
        }
    }
    
    @objc public func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        
        print("Kliknięto na znacznik: \(marker.userData ?? "nil")")
        
        let placeRequest = GMSFetchPlaceRequest(
            placeID: marker.userData as! String,
            placeProperties: [
                GMSPlaceProperty.name.rawValue,
                GMSPlaceProperty.coordinate.rawValue,
                GMSPlaceProperty.phoneNumber.rawValue,
                GMSPlaceProperty.currentOpeningHours.rawValue,
                GMSPlaceProperty.formattedAddress.rawValue
            ],
            sessionToken: nil
        )
        
        GMSPlacesClient.shared().fetchPlace(with: placeRequest) { res, err in
            if err != nil {
                print(err.debugDescription)
                return
            }

            if res != nil {
                print("Found place: \(res!.name ?? "nil")")
                
                self.delegate?.updateView(place: res!)
            }
        }
        return true
    }

    @objc public func mapView(_ mapView: GMSMapView, didTapAt coordinate: CLLocationCoordinate2D) {
//        print("Kliknięto w: \(coordinate.latitude), \(coordinate.longitude) b")

        drawMarker(pos: coordinate)
        let newCamera = GMSCameraPosition.camera(
           withLatitude: coordinate.latitude,
           longitude: coordinate.longitude,
           zoom: 13.0
        )
        mapView.animate(to: newCamera)
        search(center: coordinate)
        
        drawRange(mapView: mapView, center: coordinate, distance: 1.0)
        
    }
    
    func drawMarker(pos: iOSLatLng) {

        locationMarker?.map = nil
        let newMarker = GMSMarker(position: pos)
        newMarker.title = "Nowe miejsce"
        newMarker.snippet = "\(pos.latitude), \(pos.longitude)"
        newMarker.map = mapView

        locationMarker = newMarker

    }
    
    func drawRange(mapView: GMSMapView, center: iOSLatLng, distance: Double) {
        
        range?.map = nil
        
        let bounds = getRectangularBounds(center: center, distance: distance)
        let sw = bounds.0
        let ne = bounds.1
        let nw = iOSLatLng(latitude: ne.latitude, longitude: sw.longitude)
        let se = iOSLatLng(latitude: sw.latitude, longitude: ne.longitude)

        let path = GMSMutablePath()
        path.add(sw); path.add(se); path.add(ne)
        path.add(nw); path.add(sw)

        let polygon = GMSPolygon(path: path)
        polygon.strokeColor = UIColor.blue
        polygon.strokeWidth = 2.0
        polygon.fillColor = UIColor.green.withAlphaComponent(0.4)
        polygon.map = mapView
        
        range = polygon
    }
    
    func search(center: iOSLatLng) {
        
        let bounds = getRectangularBounds(center: center, distance: 1.0)
        let req = GMSPlaceSearchByTextRequest(
            textQuery: "okulistyczny",
            placeProperties: [
                GMSPlaceProperty.name.rawValue,
                GMSPlaceProperty.placeID.rawValue,
                GMSPlaceProperty.coordinate.rawValue
            ]
        )
//        req.includedType = "doctor"
        req.locationRestriction = GMSPlaceRectangularLocationOption(bounds.1, bounds.0)
        req.maxResultCount = 10
        
        GMSPlacesClient.shared().searchByText(with: req) { res, err in
            if err != nil {
                print(err.debugDescription)
                return
            }

            if let foundPlaces = res {
                for place in foundPlaces {
                    let placeMarker = GMSMarker(position: place.coordinate)
                    placeMarker.icon = GoogleMapsView.getMarker()
                    placeMarker.map = self.mapView
                    placeMarker.userData = place.placeID
                }
            }
        }
    }
    
    @objc public static func getMarker() -> UIImage {
        let size = 50.0
        let renderer = UIGraphicsImageRenderer(size: CGSize(width: size, height: size))

        return renderer.image { context in
            let rect = CGRect(x: 0, y: 0, width: size, height: size)
            let center = CGPoint(x: size / 2, y: size / 2)

            let backgroundColor = UIColor.red
            backgroundColor.setFill()
            context.cgContext.fillEllipse(in: rect)

            if let markerImage = UIImage(named: "LogoVector")?.withRenderingMode(.alwaysTemplate) {
                let original = markerImage.size

                let scale = min(size / original.width, size / original.height)
                let markerSize = CGSize(width: original.width * scale, height: original.height * scale)

                let markerRect = CGRect(
                    x: center.x - markerSize.width / 2,
                    y: center.y - markerSize.height / 2,
                    width: markerSize.width,
                    height: markerSize.height
                )

                UIColor.white.setFill()
                markerImage.draw(in: markerRect, blendMode: .normal, alpha: 1.0)
            }
        }
    }
    
    func getRectangularBounds(center: iOSLatLng, distance: Double) -> (iOSLatLng, iOSLatLng) {
        let earthRadius = 6371.0
        let lat = center.latitude
        let lon = center.longitude

        let latOffset = (distance / earthRadius) * (180.0 / .pi)

        let lngOffset = (distance / earthRadius) * (180.0 / .pi) / cos(lat * .pi / 180.0)

        let minLat = max(-90.0, lat - latOffset)
        let maxLat = min(90.0, lat + latOffset)
        let minLon = max(-180.0, lon - lngOffset)
        let maxLon = min(180.0, lon + lngOffset)
        return (
            iOSLatLng(latitude: minLat, longitude: minLon),
            iOSLatLng(latitude: maxLat, longitude: maxLon)
        )
    }

}
