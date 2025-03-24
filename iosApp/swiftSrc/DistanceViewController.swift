//
//  DistanceViewController.swift
//  iosApp
//
//  Created by Bartłomiej Gajda on 08/03/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import UIKit
import AVFoundation
import Vision
import ARKit

@objc public class DistanceViewController:
    UIViewController,
    AVCaptureVideoDataOutputSampleBufferDelegate,
    ARSCNViewDelegate

{
        
    var session: AVCaptureSession!
    var previewLayer: AVCaptureVideoPreviewLayer!
    var device: AVCaptureDevice!
    var ogniskowa: CGFloat?
    
    var counts: Int = 0
    var sum: Float = 0
    
    @objc public func getDistance() -> Float {
        if (counts >= 25) {
            let srednia = sum / Float(counts)
            return srednia
        } else {
            return 0
        }
    }
    
    let distanceLabel: UILabel = {
        
        let distString = ResourceManager.shared.allResources["result_distance"]!
        
        let label = UILabel()
        label.text = distString + ": -- cm"
        label.textColor = .white
        label.font = UIFont.boldSystemFont(ofSize: 24)
        label.textAlignment = .center
        label.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        label.layer.cornerRadius = 10
        label.layer.masksToBounds = true
        return label
    }()
    
    @objc public override func viewDidLoad() {
        super.viewDidLoad()
        setupCamera()
        setupUI()
    }
    
    func setupCamera() {
        session = AVCaptureSession()
        session.sessionPreset = .high
        
        guard let camera = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .front) else { return }
        self.device = camera
        
        do {
            let input = try AVCaptureDeviceInput(device: camera)
            session.addInput(input)
            
            let output = AVCaptureVideoDataOutput()
            output.setSampleBufferDelegate(self, queue: DispatchQueue(label: "videoQueue"))
            session.addOutput(output)
            
            previewLayer = AVCaptureVideoPreviewLayer(session: session)
            previewLayer.videoGravity = .resizeAspectFill
            previewLayer.frame = view.layer.bounds
            view.layer.addSublayer(previewLayer)
            
            session.startRunning()
        } catch {
            print("Camera config error: \(error)")
        }
    }
    
    func setupUI() {
        view.addSubview(distanceLabel)
        distanceLabel.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            distanceLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            distanceLabel.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20),
            distanceLabel.widthAnchor.constraint(equalToConstant: 200),
            distanceLabel.heightAnchor.constraint(equalToConstant: 50)
        ])
    }
    
    @objc public func captureOutput(_ output: AVCaptureOutput,
                       didOutput sampleBuffer: CMSampleBuffer,
                       from connection: AVCaptureConnection) {
        guard let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else { return }
        
        let request = VNDetectFaceRectanglesRequest { request, error in
            guard let results = request.results as? [VNFaceObservation], let face = results.first else { return }
            
            DispatchQueue.main.async {
//                print(face.boundingBox.width)
                let faceWidth = face.boundingBox.width * self.view.frame.width  // Szerokość twarzy w pikselach
                let distance = self.calculateDistance(faceWidth: faceWidth)
                
                let distString = ResourceManager.shared.allResources["result_distance"]!
                
                self.distanceLabel.text = String(format: distString + ": %.1f cm", distance)
            }
        }
        
        let handler = VNImageRequestHandler(cvPixelBuffer: pixelBuffer, orientation: .up, options: [:])
        try? handler.perform([request])
    }
    
    func calculateDistance(faceWidth: CGFloat) -> CGFloat {
        
        if (ogniskowa == nil) {
            let fov = device.activeFormat.videoFieldOfView
            print("FOV \(fov)")
            let width = UIScreen.main.nativeBounds.width
            ogniskowa = width / 2 / tan(CGFloat(fov)/2)
            print("Ogniskowa \(ogniskowa ?? -1.0)")
        }
        
        let sredniaSzerokoscTwarzy: CGFloat = 15.0
        
//        guard let sensorWidth = device?.activeFormat.formatDescription.dimensions.width else { return 0 }
        // TODO: magic value 2??
        let calculated = ogniskowa! * sredniaSzerokoscTwarzy / faceWidth / 2
        counts += 1
        sum += Float(calculated)
        return calculated
    }
}
