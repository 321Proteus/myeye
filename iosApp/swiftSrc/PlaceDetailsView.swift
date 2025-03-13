//
//  PlaceDetailsView.swift
//  iosApp
//
//  Created by Bartłomiej Gajda on 04/03/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import UIKit
import GoogleMaps
import GooglePlaces
import CoreLocation

@objc public class PlaceDetailsView: UIView, GMSMapViewDelegate {

//    private var mapView: GMSMapView?
    private var place: GMSPlace?
    
    init(frame: CGRect, place: GMSPlace) {
        super.init(frame: frame)
        print("Started from \(place.name ?? "nil")")
        self.place = place
        
        let name = UILabel()
        name.text = place.name
        name.numberOfLines = 0
        name.lineBreakMode = .byWordWrapping
        name.frame = CGRect(x: 10, y: 100, width: frame.width - 20, height: 20)
        name.sizeToFit()
        addSubview(name)
        
        print(name.bounds.minX, name.bounds.minY)
        
        let phone = UILabel()
        phone.text = place.phoneNumber
        phone.frame = CGRect(x: 10, y: 200, width: frame.width - 20, height: 20)
        addSubview(phone)
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        print("Started from coder")
    }

}
