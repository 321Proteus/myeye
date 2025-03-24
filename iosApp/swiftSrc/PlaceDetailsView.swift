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
    
    private let mapView = GMSMapView()
    private let name = UILabel()
    private let address = UILabel()
    private let phone = UILabel()
    private let hours = UILabel()
    private let page = UILabel()
    private let buttonStackView = UIStackView()
    private let actionButton1 = UIButton(type: .system)
    private let actionButton2 = UIButton(type: .system)
    
    init(frame: CGRect, place: GMSPlace) {
        super.init(frame: frame)
        print("Started from \(place.name ?? "nil")")
        self.place = place
        
        mapView.translatesAutoresizingMaskIntoConstraints = false
        mapView.isMyLocationEnabled = false
        mapView.isUserInteractionEnabled = false
        addSubview(mapView)
        
        let marker = GMSMarker(position: place.coordinate)
        marker.map = mapView
        marker.icon = GoogleMapsView.getMarker()
        mapView.camera = GMSCameraPosition.camera(withTarget: place.coordinate, zoom: 15)

        [name, address, phone, hours, page].forEach { label in
            label.textColor = .black
            label.translatesAutoresizingMaskIntoConstraints = false
            label.numberOfLines = 0
            label.lineBreakMode = .byWordWrapping
            label.textAlignment = .center
        }
                
        name.text = place.name
        address.text = place.formattedAddress
        phone.text =
            ResourceManager.shared.allResources["map_phone"]! + ": " +
            (place.phoneNumber ?? ResourceManager.shared.allResources["nodata"]!)
        page.text =
            "WWW: " + (place.website?.absoluteString ?? ResourceManager.shared.allResources["nodata"]!)
        
        hours.text =
            ResourceManager.shared.allResources["map_hours"]! + ": \n" +
            (place.currentOpeningHours?.weekdayText?.joined(separator: "\n") ??
             ResourceManager.shared.allResources["nodata"]!)
        hours.font = UIFont.systemFont(ofSize: 10)
        
        let textStackView = UIStackView(arrangedSubviews: [name, address])
        if (place.currentOpeningHours != nil) { textStackView.addArrangedSubview(hours) }
        if (place.phoneNumber != nil) { textStackView.addArrangedSubview(phone) }
        if (place.website != nil) { textStackView.addArrangedSubview(page) }
        
        textStackView.axis = .vertical
        textStackView.spacing = 10
        textStackView.alignment = .center
        textStackView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(textStackView)

        actionButton1.setTitle(ResourceManager.shared.allResources["map_see_gmaps"], for: .normal)
        actionButton2.setTitle(ResourceManager.shared.allResources["map_website"], for: .normal)
        
        [actionButton1, actionButton2].forEach { button in
            button.translatesAutoresizingMaskIntoConstraints = false
            button.setTitleColor(.white, for: .normal)
            button.backgroundColor = .blue
            button.layer.cornerRadius = 8
            button.heightAnchor.constraint(equalToConstant: 50).isActive = true
        }
        
        buttonStackView.axis = .horizontal
        buttonStackView.spacing = 20
        buttonStackView.distribution = .fillEqually
        buttonStackView.addArrangedSubview(actionButton1)
        buttonStackView.addArrangedSubview(actionButton2)
        buttonStackView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(buttonStackView)

        NSLayoutConstraint.activate([
            mapView.topAnchor.constraint(equalTo: topAnchor, constant: 64.0),
            mapView.leadingAnchor.constraint(equalTo: leadingAnchor),
            mapView.trailingAnchor.constraint(equalTo: trailingAnchor),
            mapView.heightAnchor.constraint(equalTo: heightAnchor, multiplier: 0.2),

            textStackView.topAnchor.constraint(equalTo: mapView.bottomAnchor, constant: 20),
            textStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 20),
            textStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -20),

            buttonStackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -80),
            buttonStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 20),
            buttonStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -20),
            buttonStackView.heightAnchor.constraint(equalToConstant: 50),
        ])
    }
//    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        print("Started from coder")
    }

}
