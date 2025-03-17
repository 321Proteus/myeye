package me.proteus.myeye.io

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.float
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class SpeechDecoderResult(
    val confidence: Float,
    val start: Float,
    val end: Float,
    val word: String
)

fun deserialize(json: String): List<SpeechDecoderResult> {
    val list = mutableListOf<SpeechDecoderResult>()
    val obj = Json.parseToJsonElement(json).jsonObject

    obj["alternatives"]?.jsonArray?.forEach { el ->
        list.addAll(getSingleResult(el.jsonObject))
    }

    obj["result"]?.jsonArray?.forEach { el ->
        val result = el.jsonObject
        list.add(SpeechDecoderResult(
            result["conf"]!!.jsonPrimitive.float,
            result["start"]!!.jsonPrimitive.float,
            result["end"]!!.jsonPrimitive.float,
            result["word"]!!.jsonPrimitive.content
        ))
    }

    return list
}

private fun getSingleResult(json: JsonObject): List<SpeechDecoderResult> {
    val list = mutableListOf<SpeechDecoderResult>()

    if (json.containsKey("confidence")) {
        val resultArray = json["result"]!!.jsonArray
        val avgConf = json["confidence"]!!.jsonPrimitive.float / resultArray.size
        resultArray.forEach { el ->
            val obj = el.jsonObject
            list.add(SpeechDecoderResult(
                avgConf,
                obj["start"]!!.jsonPrimitive.float,
                obj["end"]!!.jsonPrimitive.float,
                obj["word"]!!.jsonPrimitive.content
            ))
        }
    } else {
        list.add(SpeechDecoderResult(
            json["conf"]!!.jsonPrimitive.float,
            json["start"]!!.jsonPrimitive.float,
            json["end"]!!.jsonPrimitive.float,
            json["word"]!!.jsonPrimitive.content
        ))
    }

    return list
}
