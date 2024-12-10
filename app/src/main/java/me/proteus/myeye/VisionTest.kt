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
    fun DisplayStage(
        activity: VisionTestLayoutActivity,
        stage: SerializableStage,
        isResult: Boolean,
        difficulty: Int,
        onUpdate: (String) -> Unit
    )

    @Composable
    fun BeginTest(activity: VisionTestLayoutActivity, isResult: Boolean, result: TestResult?) {

        if (isResult) {
            var i by remember { mutableIntStateOf(0) }

            val resultData = ResultDataCollector.deserializeResult(result!!.result)
            var stageList = remember { resultData }

            var currentResultStage = stageList[i]
            var stageDifficulty = currentResultStage.difficulty

            DisplayStage(activity, currentResultStage, true, stageDifficulty) { answer ->
                if (answer == "PREV") {
                    if (i > 0) i--
                } else if (answer == "NEXT") {
                    if (i < stageList.size - 1) i++
                    // else powrot do menu
                }
            }

        } else {

            var currentDifficulty by remember { mutableIntStateOf(0) }

            var currentStage = SerializableStage(
                generateQuestion(currentDifficulty).toString(),
                getExampleAnswers().joinToString(" "),
                currentDifficulty
            )

            DisplayStage(activity, currentStage, false, currentDifficulty) { answer ->

                if (answer == "REGENERATE") {

                    currentStage = SerializableStage(
                        generateQuestion(currentDifficulty).toString(),
                        getExampleAnswers().joinToString(" "),
                        currentDifficulty
                    )
                    println("Regenerate")

                } else if (currentDifficulty == stageCount) {

                    storeResult(currentStage.first, answer, currentDifficulty)
                    endTest(activity)

                } else {
                    println("Answer: $answer")
                    storeResult(currentStage.first, answer, currentDifficulty)
                    currentDifficulty++
                }


            }

        }
//
//        var stageList = remember {
//            mutableListOf<SerializablePair>().apply {
//                if (isResult) {
//
//                } else {
//                    for (i in 0..<stageCount) {
//                        var pair = SerializablePair(
//                            generateQuestion(stageIterator).toString(),
//                            getExampleAnswers().joinToString(" ")
//                        )
//                        add(pair)
//                        stageIterator++
//                    }
//                }
//            }
//        }


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