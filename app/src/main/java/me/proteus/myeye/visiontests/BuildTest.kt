package me.proteus.myeye.visiontests

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random

class BuildTest : VisionTest {

    override val stageCount: Int
        get() = TODO("Not yet implemented")
    override var currentStage: Int
        get() = TODO("Not yet implemented")
        set(value) {}

    @Composable
    override fun DisplayStage(activity: VisionTestLayoutActivity, modifier: Modifier) {
        TODO("Not yet implemented")
    }

    override fun generateQuestion(): Any {

        return "Testowy test wzroku #2 (TEST_BUILD)"

    }

    override fun checkAnswer(answer: String): Boolean {
        return true
    }

    override fun getExampleAnswers(): Array<String> {

        var random = Random()
        return Array<String>(4) { random.nextInt().toString() }

    }

}