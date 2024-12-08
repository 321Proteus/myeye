package me.proteus.myeye.visiontests

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import me.proteus.myeye.SerializablePair
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random

class BuildTest : VisionTest {

    override val testID: String = "TEST_BUILD"
    override val testIcon: ImageVector = Icons.Outlined.Build

    override val stageCount: Int
        get() = TODO("Not yet implemented")

    override val resultCollector: ResultDataCollector = ResultDataCollector()

    @Composable
    override fun DisplayStage(
        activity: VisionTestLayoutActivity,
        stage: SerializablePair,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun generateQuestion(): String {

        return "Testowy test wzroku #2 (TEST_BUILD)"

    }

    override fun checkAnswer(answer: String): Boolean {
        return true
    }

    override fun storeResult(question: String, answer: String) {

        resultCollector.addResult(question, answer)

    }

    override fun getExampleAnswers(): Array<String> {

        var random = Random()
        return Array<String>(4) { random.nextInt().toString() }

    }

}