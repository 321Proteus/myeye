//
//  NativeRecognizer.swift
//  iosApp
//
//  Created by Bartłomiej Gajda on 11/03/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import Speech

@objc public class SpeechRecognizer: NSObject, SFSpeechRecognizerDelegate {
    private let recognizer = SFSpeechRecognizer(locale: Locale(identifier: "pl-PL"))!
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest?
    private var recognitionTask: SFSpeechRecognitionTask?
    private let audioEngine = AVAudioEngine()
    
    private var history: [String] = []
    private var chunk: [String] = []
    private var silenceTimer: Timer?
    
    @objc public static func requestAuthorization(callback: @escaping (Bool) -> Void) {
        SFSpeechRecognizer.requestAuthorization { status in
            DispatchQueue.main.async {
                callback(status == .authorized)
            }
        }
    }

    @objc public func startRecognition(grammar: [String], callback: @escaping (String?) -> Void) {
        if recognitionTask != nil {
            recognitionTask?.cancel()
            recognitionTask = nil
        }
        
        history.removeAll()

        let audioSession = AVAudioSession.sharedInstance()
        try? audioSession.setCategory(.record, mode: .measurement, options: .duckOthers)
        try? audioSession.setActive(true, options: .notifyOthersOnDeactivation)
        
        recognitionRequest = SFSpeechAudioBufferRecognitionRequest()
        recognitionRequest?.contextualStrings = grammar
        let inputNode = audioEngine.inputNode
        recognitionRequest?.shouldReportPartialResults = true
        
        recognitionTask = recognizer.recognitionTask(with: recognitionRequest!) { result, error in
            if let result = result {
                let words = result.bestTranscription.segments.map { $0.substring }
                let newWords = words.dropFirst(self.history.count)

                if !newWords.isEmpty {
                    self.chunk.append(contentsOf: newWords)
                    self.resetTimer(callback)
                }

                self.history = words
            } else if let error = error {
                print("Błąd: \(error.localizedDescription)")
                callback(nil)
            }
        }

        let recordingFormat = inputNode.outputFormat(forBus: 0)
        inputNode.installTap(onBus: 0, bufferSize: 1024, format: recordingFormat) { (buffer, _) in
            self.recognitionRequest?.append(buffer)
        }
        
        audioEngine.prepare()
        try? audioEngine.start()
    }

    @objc public func stopRecognition() {
        audioEngine.stop()
        recognitionRequest?.endAudio()
    }
    
    private func resetTimer(_ callback: @escaping (String) -> Void) {
        silenceTimer?.invalidate()
        
        silenceTimer = Timer.scheduledTimer(withTimeInterval: 1.2, repeats: false) { _ in
            if !self.chunk.isEmpty {
                let chunkText = self.chunk.joined(separator: " ")
                callback(chunkText)
                
                self.chunk.removeAll()
                self.playSound()
            }
        }
    }

    private func playSound() {
        let systemSoundID: SystemSoundID = 1057
        AudioServicesPlaySystemSound(systemSoundID)
    }
}

