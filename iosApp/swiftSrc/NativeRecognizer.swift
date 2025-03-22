//
//  NativeRecognizer.swift
//  iosApp
//
//  Created by Bartłomiej Gajda on 11/03/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import Speech
import MyLibrary

@objc public class NativeRecognizer: NSObject, SFSpeechRecognizerDelegate {
    
    var audioEngine : AVAudioEngine!
    var processingQueue: DispatchQueue!
    var model: VoskModel?
    var recognizer: VoskRecognizer?
    
    @objc public static func requestAuthorization(callback: @escaping (Bool) -> Void) {
        SFSpeechRecognizer.requestAuthorization { status in
            DispatchQueue.main.async {
                callback(status == .authorized)
            }
        }
    }

    @objc public func startRecognition(grammar: String, path: String, callback: @escaping (String?) -> Void) {
        self.processingQueue = DispatchQueue(label: "processingQueue")
        let session = AVAudioSession.sharedInstance()
        do {
            try session.setCategory(.playAndRecord)
            try session.setPreferredSampleRate(48000)
            try session.setActive(true)
        } catch {
            print("Failed to set the audio session configuration")
        }
        
        session.requestRecordPermission { [self] granted in
            do {
                
                audioEngine = AVAudioEngine()
                
                let inputNode = audioEngine.inputNode
                let formatInput = inputNode.inputFormat(forBus: 0)
                
                let rate = formatInput.sampleRate
                print(rate)
                
                let format = AVAudioFormat.init(commonFormat: AVAudioCommonFormat.pcmFormatInt16, sampleRate: rate, channels: 1, interleaved: false)
                
                model = VoskModel(path: path, spkPath: nil)
                recognizer = VoskRecognizer(model: model!, sampleRate: Float(rate), grammar: grammar)
                
                print("Volume: \(session.outputVolume)")
                print("Route: \(session.currentRoute)")
                
                inputNode.installTap(
                    onBus: 0,
                    bufferSize: UInt32(formatInput.sampleRate / 10),
                    format: format
                ) { buffer, time in self.processingQueue.async {
                    let res = self.recognizer!.recognizeData(buffer: buffer)
                    DispatchQueue.main.async {
                        if (!res.contains("\"\"") && res != "") {
                            AudioServicesPlaySystemSound(1025)
                            callback(res)
                        }
                    }
                }
                }
                
                audioEngine.prepare()
                try audioEngine.start()
            } catch {
                print("Unable to start AVAudioEngine: \(error.localizedDescription)")
            }
        }
    }
    
    @objc public func stopRecognition() {
        model = nil
        recognizer = nil
        if audioEngine.isRunning {
            audioEngine.stop()
        }
        audioEngine.inputNode.removeTap(onBus: 0)
        
        processingQueue = nil
    }

}

