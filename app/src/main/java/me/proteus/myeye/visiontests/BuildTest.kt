package me.proteus.myeye.visiontests

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import me.proteus.myeye.SerializableStage
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import java.util.Random

class BuildTest : VisionTest {

    override val testID: String = "TEST_BUILD"
    override val testIcon: ImageVector = Icons.Outlined.Build
    override val needsMicrophone: Boolean = false
    override val stageCount: Int = 5
    override val resultCollector: ResultDataCollector = ResultDataCollector()
    override var distance: Float = -1f

    @Composable
    override fun DisplayStage(
        controller: NavController,
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun generateQuestion(stage: Int?): String {

        return "Testowy test wzroku #2 (TEST_BUILD)"

    }

    override fun checkAnswer(answer: String): Boolean {
        return true
    }

    override fun getExampleAnswers(): Array<String> {

        val random = Random()
        return Array(4) { random.nextInt().toString() }

    }

}