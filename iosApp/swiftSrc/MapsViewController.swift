//
//  MapsViewController.swift
//  iosApp
//
//  Created by Bartłomiej Gajda on 05/03/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import UIKit
import GooglePlaces

protocol ViewUpdateDelegate: AnyObject {
    func updateView(place: GMSPlace)
}

@objc public class MapsViewController: UIViewController, ViewUpdateDelegate {
    private let googleMapView = GoogleMapsView()
    private var placeDetailsView: PlaceDetailsView?
    private var isDetail = false
    
    public override func viewDidLoad() {
        super.viewDidLoad()

        googleMapView.frame = view.bounds
        googleMapView.delegate = self

        view.addSubview(googleMapView)

    }

    @objc func updateView(place: GMSPlace) {
        print("updating from \(isDetail) with name \(place.name ?? "nil")")
        isDetail.toggle()
        if isDetail {
            googleMapView.removeFromSuperview()
            self.dismiss(animated: false)
//            placeDetailsView = PlaceDetailsView(frame: view.bounds, place: place)
//            view.addSubview(placeDetailsView!)
        } else {
            placeDetailsView?.removeFromSuperview()
            view.addSubview(googleMapView)
        }
    }
}
