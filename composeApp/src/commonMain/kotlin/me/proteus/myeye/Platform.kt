package me.proteus.myeye

import io.ktor.client.engine.HttpClientEngine

interface Platform {
    val type: String
    val name: String
}

expect fun getPlatform(): Platform

expect fun getDriver(): HttpClientEngine