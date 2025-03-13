package me.proteus.myeye.util

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual fun formatString(format: String, vararg args: Any?): String {
    try {

        var returnString = ""
        val regEx = """%[\d|.]*\$[sdf]""".toRegex()

        println("size: ${regEx.findAll(format).toList().size}")
        val singleFormats = regEx.findAll(format).map {
            it.groupValues.first()
        }.toList()

        val newStrings = format.split(regEx)

        println("format: $format")

        for (i in args.indices) {
            val arg = args[i]

            println(regEx.findAll(format).toList()[0].groupValues)

//        singleFormats.forEachIndexed { index, match ->
//            logger.info { "Match[$index]: $match" }
//            logger.info { "Match value[$index]: ${match.value}" }
//        }

            returnString += when (arg) {
                is Double -> NSString.stringWithFormat(newStrings[i] + singleFormats[i], arg)
                is Int -> NSString.stringWithFormat(newStrings[i] + singleFormats[i], arg)
                is String -> NSString.stringWithFormat(newStrings[i], arg as NSString)
                else -> NSString.stringWithFormat(newStrings[i] + "%@", arg ?: "null")
            }
        }

        println(returnString)

        return returnString
    } catch (e: Exception) {
        println(e.message)
        return ""
    }
}