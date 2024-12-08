package me.proteus.myeye

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
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
    fun DisplayStage(activity: VisionTestLayoutActivity, stage: SerializablePair, isResult: Boolean, onUpdate: (String) -> Unit)

    @Composable
    fun BeginTest(activity: VisionTestLayoutActivity, isResult: Boolean, result: TestResult?) {

        var stageList = remember {
            mutableListOf<SerializablePair>().apply {
                if (isResult) {
                    val resultData = ResultDataCollector.deserializeResult(result!!.result)
                    for (i in 0..<resultData.size) {
                        add(resultData[i])
                    }
                } else {
                    for (i in 0..<stageCount) {
                        var pair = SerializablePair(
                            generateQuestion().toString(),
                            getExampleAnswers().joinToString(" ")
                        )
                        add(pair)
                    }
                }
            }
        }

        var stageIterator by remember { mutableIntStateOf(0) }
        var currentStage: SerializablePair = stageList[stageIterator]

        DisplayStage(activity, currentStage, isResult) { answer ->

            println("Answer: $answer")
            storeResult(currentStage.first, answer)
            stageIterator++
            println(stageIterator)

        }

    }

    fun generateQuestion(): String

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