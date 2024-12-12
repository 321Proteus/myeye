package me.proteus.myeye

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.TestResultActivity
import me.proteus.myeye.ui.VisionTestLayoutActivity
import me.proteus.myeye.visiontests.ColorArrangementTest
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
    fun DisplayStage(
        activity: VisionTestLayoutActivity,
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    )

    @Composable
    fun BeginTest(activity: VisionTestLayoutActivity, isResult: Boolean, result: TestResult?) {
        BeginTestImpl(activity, isResult, result)
    }

    @Composable
    fun BeginTestImpl(activity: VisionTestLayoutActivity, isResult: Boolean, result: TestResult?) {

        if (isResult) {
            var i by remember { mutableIntStateOf(0) }

            val resultData = ResultDataCollector.deserializeResult(result!!.result)
            var stageList = remember { resultData }

            var currentResultStage = stageList[i]

            DisplayStage(activity, currentResultStage, true) { answer ->
                print("Update")
                if (answer == "PREV") {
                    if (i > 0) i--
                } else if (answer == "NEXT") {
                    if (i < stageList.size - 1) i++
                    else {
                        var exitIntent = Intent(activity, MenuActivity::class.java)
                        // TODO: Dodac podsumowanie testu
                        activity.startActivity(exitIntent)
                    }
                }
            }

        } else {

            var currentDifficulty by remember { mutableIntStateOf(0) }

            var currentStage by remember {
                mutableStateOf(generateStage(currentDifficulty))
            }

            DisplayStage(activity, currentStage, false) { answer ->

                if (answer == "REGENERATE") {

                    currentStage = generateStage(currentDifficulty)
                    println("Regenerate")

                } else if (currentDifficulty == stageCount) {

                    storeResult(currentStage.first, answer, currentDifficulty)
                    endTest(activity)

                } else {
                    storeResult(currentStage.first, answer, currentDifficulty)
                    currentDifficulty++
                    currentStage = generateStage(currentDifficulty)
                }

            }

        }

    }

    fun generateStage(difficulty: Int): SerializableStage {
        return SerializableStage(
            generateQuestion(difficulty).toString(),
            getExampleAnswers().joinToString(" "),
            difficulty
        )
    }

    fun generateQuestion(stage: Int?): String

    fun getExampleAnswers(): Array<String>

    fun checkAnswer(answer: String): Boolean

    fun storeResult(question: String, answer: String, difficulty: Int) {
        resultCollector.addResult(question, answer, difficulty)
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