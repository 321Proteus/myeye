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

@objc public class DistanceViewController: UIViewController, AVCaptureVideoDataOutputSampleBufferDelegate {
    var session: AVCaptureSession!
    var previewLayer: AVCaptureVideoPreviewLayer!
    var device: AVCaptureDevice!
    
    let distanceLabel: UILabel = {
        let label = UILabel()
        label.text = "Odległość: -- cm"
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
        
        guard let camera = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .front) else {
            print("Brak dostępnej kamery")
            return
        }
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
            print("Błąd konfiguracji kamery: \(error)")
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
                let faceWidth = face.boundingBox.width * self.view.frame.width  // Szerokość twarzy w pikselach
                let distance = self.calculateDistance(faceWidth: faceWidth)
                
                self.distanceLabel.text = String(format: "Odległość: %.1f cm", distance)
            }
        }
        
        let handler = VNImageRequestHandler(cvPixelBuffer: pixelBuffer, orientation: .up, options: [:])
        try? handler.perform([request])
    }
    
    func calculateDistance(faceWidth: CGFloat) -> CGFloat {
        let knownFaceWidth: CGFloat = 16.0 // cm (średnia szerokość twarzy)
        let focalLength: CGFloat = 4.25 // mm (przykładowa wartość dla iPhone)
        
        guard let sensorWidth = device?.activeFormat.formatDescription.dimensions.width else { return 0 }
        
        return (focalLength * knownFaceWidth * CGFloat(sensorWidth)) / faceWidth
    }
}
