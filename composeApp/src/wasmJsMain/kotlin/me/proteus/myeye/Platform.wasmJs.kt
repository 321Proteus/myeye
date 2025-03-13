package me.proteus.myeye

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

class WasmPlatform: Platform {
    override val type: String = "WEB"
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()
actual fun getDriver(): HttpClientEngine {
    return Js.create()
}