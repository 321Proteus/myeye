package me.proteus.myeye.visiontests

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.proteus.myeye.MenuActivity
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random

class BuildTest : VisionTest {

    override val stageCount: Int
        get() = TODO("Not yet implemented")
    override var currentStage: Int
        get() = TODO("Not yet implemented")
        set(value) { currentStage = value }

    override val resultCollector: ResultDataCollector = ResultDataCollector()

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

    override fun storeResult(question: String, answer: String) {

        resultCollector.addResult(question, answer)

    }

    override fun getExampleAnswers(): Array<String> {

        var random = Random()
        return Array<String>(4) { random.nextInt().toString() }

    }

    override fun endTest(activity: VisionTestLayoutActivity) {

        var localSaver = ResultDataSaver(activity.applicationContext)
        localSaver.insert("TEST_BUILD", resultCollector.stages)

        val testLeavingIntent = Intent(activity, MenuActivity::class.java)
        activity.startActivity(testLeavingIntent)

    }

}