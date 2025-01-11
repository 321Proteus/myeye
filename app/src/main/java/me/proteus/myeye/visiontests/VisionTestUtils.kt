package me.proteus.myeye.visiontests

import me.proteus.myeye.VisionTest

class VisionTestUtils {

    /**
     * Tutaj dodajemy wejscia do wszystkich klas testow
     * z interfejsu VisionTest i odpowiadajace im ID
     * @param testID id testu
     */
    fun getTestByID(testID: String?): VisionTest {

        return when (testID) {
            "SNELLEN_CHART" -> SnellenChart()
            "TEST_CIRCLE" -> CircleTest()
            "TEST_BUILD" -> BuildTest()
            "TEST_INFO" -> ExampleTest()
            "COLOR_ARRANGE" -> ColorArrangementTest()
            else -> throw IllegalArgumentException("Nie znaleziono testu o podanym ID")
        }

    }

    fun getTestNameByID(testID: String?): String {

        return when (testID) {
            "SNELLEN_CHART" -> "LogMAR"
            "TEST_CIRCLE" -> "Landolt C"
            "TEST_BUILD" -> "TODO"
            "TEST_INFO" -> "Przykładowy"
            "COLOR_ARRANGE" -> "Farnsworth"
            else -> throw IllegalArgumentException("Nie znaleziono testu o podanym ID")
        }

    }

    fun getTestTypeByID(testID: String): String {

        return when (testID.split('_')[0]) {
            "TEST" -> "Test ostrości"
            "COLOR" -> "Test kolorów"
            else -> "Test wzroku"

        }


    }

}