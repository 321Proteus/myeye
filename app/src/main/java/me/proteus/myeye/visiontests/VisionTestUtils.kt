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
            else -> throw IllegalArgumentException("Nie znaleziono testu o podanym ID")
        }

    }

}