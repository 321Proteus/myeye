package me.proteus.myeye.visiontests

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import me.proteus.myeye.VisionTest
import me.proteus.myeye.R

class VisionTestUtils {

    val testList = listOf(
        SnellenChart(),
        CircleTest(),
        BuildTest(),
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
    fun getTestByID(testID: String?): VisionTest {

        val found = testList.find {
            it.testID == testID
        }

        if (found == null) throw IllegalArgumentException("Nie znaleziono testu o podanym ID")
        else return found

    }

    @Composable
    fun getTestNameByID(testID: String?): String {

        val idMap = stringArrayResource(R.array.test_ids)
        val resource = stringArrayResource(R.array.name)

        val found = resource[idMap.indexOf(testID)]
        return found

    }

    @Composable
    fun getTestTypeByID(testID: String): String {

        val resource = stringArrayResource(R.array.categories)

        return when (testID.split('_')[1]) {
            "SIGHT" -> resource[0]
            "COLOR" -> resource[1]
            else -> resource[2]
        }


    }

}