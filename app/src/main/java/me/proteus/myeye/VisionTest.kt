package me.proteus.myeye

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.TestResultActivity
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.time.LocalDateTime
import java.time.ZoneOffset

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
    fun BeginTest(activity: VisionTestLayoutActivity, modifier: Modifier, isResult: Boolean, result: TestResult?) {

        var stageList: MutableList<SerializablePair> = ArrayList<SerializablePair>()

        if (isResult) {
            val resultData = ResultDataCollector.deserializeResult(result!!.result)
            for (i in 0..<resultData.size) {
                stageList.add(resultData[i])
            }
        } else {
            for (i in 0..<stageCount) {
                var pair = SerializablePair(this.generateQuestion().toString(), this.getExampleAnswers().joinToString(" "))
                println(pair.first + " " + pair.second)
                stageList.add(pair)
            }
        }

        DisplayStage(activity, modifier, stageList, false)

    }

    fun generateQuestion(): Any

    fun getExampleAnswers(): Array<String>

    fun checkAnswer(answer: String): Boolean

    fun storeResult(question: String, answer: String) {
        resultCollector.addResult(question, answer)
    }


     fun endTest(activity: VisionTestLayoutActivity) {

         var localSaver = ResultDataSaver(activity.applicationContext)

         var timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
         localSaver.insert(this.testID, this.resultCollector.stages, timestamp)

         val testLeavingIntent = Intent(activity, TestResultActivity::class.java)
         testLeavingIntent.putExtra("IS_AFTER", true)
         testLeavingIntent.putExtra("RESULT_PARCEL", localSaver.lastResult)

         activity.startActivity(testLeavingIntent)

    }

}