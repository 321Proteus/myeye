package me.proteus.myeye.visiontests

import me.proteus.myeye.VisionTest

class VisionTestUtils {

    val testList = listOf(
        SnellenChart(),
        CircleTest(),
        BuildTest(),
        ExampleTest(),
        ColorArrangementTest(),
        ReactionTest(),
        ConstrastTest(),
        IshiharaTest()
    )

    /**
     * Tutaj dodajemy wejscia do wszystkich klas testow
     * z interfejsu VisionTest i odpowiadajace im ID
     * @param testID id testu
     */
    fun getTestByID(testID: String?): VisionTest {

        val found = testList.find {
            it.testID == testID
        }

        if (found == null) throw IllegalArgumentException("Nie znaleziono testu o podanym ID")
        else return found

    }

    fun getTestNameByID(testID: String?): String {

        return when (testID) {
            "TEST_SIGHT_LOGMAR" -> "LogMAR"
            "TEST_SIGHT_CIRCLE" -> "Landolt C"
            "TEST_TODO_BUILD" -> "TODO"
            "TEST_SIGHT_INFO" -> "Przykładowy"
            "TEST_COLOR_ARRANGE" -> "Farnsworth"
            "TEST_MISC_REACTION" -> "Czas reakcji"
            "TEST_COLOR_CONTRAST" -> "Kolory - konstrast"
            "TEST_COLOR_PLATE" -> "Ishihary"
            else -> throw IllegalArgumentException("Nie znaleziono testu o podanym ID")
        }

    }

    fun getTestTypeByID(testID: String): String {

        return when (testID.split('_')[1]) {
            "SIGHT" -> "Test ostrości wzroku"
            "COLOR" -> "Test widzenia kolorów"
            else -> "Test"

        }


    }

}