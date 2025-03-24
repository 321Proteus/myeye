//
//  ResourceManager.swift
//  iosApp
//
//  Created by Bartłomiej Gajda on 23/03/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation

@objc public class ResourceManager: NSObject {
    @objc public static let shared = ResourceManager()
    @objc public var allResources: [String: String] = [:]
    
    @objc public func printElements() {
        print("Running \(allResources.count) elements: ")
        for el in allResources {
            print(el.key + " " + el.value)
        }
    }
    
    @objc public func addResource(key: String, value: String) {
        allResources[key] = value
    }
    
}
