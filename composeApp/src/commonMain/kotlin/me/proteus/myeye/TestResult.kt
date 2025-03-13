package me.proteus.myeye

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import me.proteus.myeye.visiontests.VisionTestUtils

@Serializable
class TestResult (
    var resultID: Int = 0,
    var testID: String = "",
    var timestamp: Long = 0L,
    var distance: Float = 0f,
    var result: ByteArray
) {

    val formattedTimestamp: String get() {
        val dateTime = Instant.fromEpochSeconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault())
        val m = dateTime.minute
        val minute = if (m < 10) "0$m" else m.toString()
        return "${dateTime.dayOfMonth}-${dateTime.monthNumber}-${dateTime.year} ${dateTime.hour}:$minute"
    }

    val fullTestName: String get() {

        try {

            val vtu = VisionTestUtils()

            val testType: String = vtu.getTestTypeByID(this.testID)
            val testName: String = vtu.getTestNameByID(this.testID)

            return "$testType $testName"

        } catch (e: IllegalArgumentException) {

//            Log.w(this.getClass().getSimpleName(), "Nieprawidlowe ID testu: " + this.testID + " na pozycji " + this.resultID);
            return "Nieznany test"

        }

    }

}
