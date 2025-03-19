package me.proteus.myeye.visiontests

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import me.proteus.myeye.VisionTest
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.*
import org.jetbrains.compose.resources.stringArrayResource

class VisionTestUtils {

    val testList = listOf(
        SnellenChart(),
        CircleTest(),
        ExampleTest(),
        ColorArrangementTest(),
        ReactionTest(),
        ConstrastTest(),
        IshiharaTest(),
        OpacityTest()
    )

    /**
     * Tutaj dodajemy wejscia do wszystkich klas testow
     * z interfejsu VisionTest i odpowiadajace im ID
     * @param testID id testu
     */
    fun getTestByID(testID: String): VisionTest {
        val found = testList.find {
            it.testID == testID
        }
        println("$testID $found")
        if (found == null) throw IllegalArgumentException("Nie znaleziono testu o podanym ID")
        else return found
    }

    @Composable
    fun getTestNameByID(testID: String): String {
        val idMap = stringArrayResource(Res.array.test_ids)
        val resource = stringArrayResource(Res.array.name)
        return resource[idMap.indexOf(testID)]
    }

    @Composable
    fun getTestTypeByID(testID: String): String {
        val categoryMap = stringArrayResource(Res.array.categories)

        return when (testID.split('_')[1]) {
            "SIGHT" -> categoryMap[0]
            "COLOR" -> categoryMap[1]
            else -> categoryMap[2]
        }
    }

    @Composable
    fun getTestDescriptionByID(testId: String): String {
        val idMap = stringArrayResource(Res.array.test_ids)
        val resource = stringArrayResource(Res.array.descriptions)
        return resource[idMap.indexOf(testId)]
    }

    @Composable
    fun getFullTestName(testId: String): String {
        val vtu = VisionTestUtils()

        val testType: String = vtu.getTestTypeByID(testId)
        val testName: String = vtu.getTestNameByID(testId)

        if (Locale.current.language == "pl") {
            return "$testType $testName"
        } else {
            return "$testName $testType"
        }
    }

}