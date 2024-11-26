package me.proteus.myeye

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.TestResultActivity
import me.proteus.myeye.ui.VisionTestLayoutActivity

interface VisionTest {

    val testID: String
    val testIcon: ImageVector

    val stageCount: Int

    val resultCollector: ResultDataCollector

    /**
     * Display the test stage as Composable in the target activity context
     * @param activity The canvas activity to display the layout
     */
    @Composable
    fun DisplayStage(activity: VisionTestLayoutActivity, modifier: Modifier, stages: List<SerializablePair>, isResult: Boolean)

    @Composable
    fun BeginTest(activity: VisionTestLayoutActivity, modifier: Modifier, isResult: Boolean, result: TestResult?)

    fun generateQuestion(): Any

    fun getExampleAnswers(): Array<String>

    fun checkAnswer(answer: String): Boolean

    fun storeResult(question: String, answer: String)


     fun endTest(activity: VisionTestLayoutActivity) {

        var localSaver = ResultDataSaver(activity.applicationContext)
        localSaver.insert(this.testID, resultCollector.stages)

        val testLeavingIntent = Intent(activity, TestResultActivity::class.java)
        testLeavingIntent
        activity.startActivity(testLeavingIntent)

    }

}