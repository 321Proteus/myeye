package me.proteus.myeye.io

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class SerializableStage(val first: String, val second: String, val difficulty: Int)

class ResultDataCollector {
    val stages: MutableList<SerializableStage> = mutableListOf()

    fun addResult(q: String, a: String, d: Int) {
        val p = SerializableStage(q, a, d)
        stages.add(p)
    }

    fun addResult(p: SerializableStage) {
        stages.add(p)
    }

    fun serializeResult(input: List<SerializableStage>): ByteArray {
        val jsonString = Json.encodeToString(input)
        return jsonString.encodeToByteArray()
    }

    fun deserializeResult(data: ByteArray): List<SerializableStage> {
        val jsonString = data.decodeToString()
        return Json.decodeFromString(jsonString)
    }
}