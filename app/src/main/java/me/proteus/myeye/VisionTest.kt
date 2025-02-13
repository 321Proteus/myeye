package me.proteus.myeye

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.ResultDataSaver
import java.time.LocalDateTime
import java.time.ZoneOffset

interface VisionTest {

    val testID: String
    val testIcon: ImageVector

    val stageCount: Int

    val resultCollector: ResultDataCollector

    /**
     * Field in which Distance Tracker output should be placed.
     * Should typically range from 2f to 6f (meters).
     *
     * -1f in initializer means that no distance measurement is needed
     */
    var distance: Float

    val needsMicrophone: Boolean

    /**
     * Display the test stage as Composable in the target activity context
     * @param controller the Navigation Controller used to move to menu after the test ends
     */
    @Composable
    fun DisplayStage(
        controller: NavController,
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    )

    @Composable
    fun BeginTest(controller: NavController, isResult: Boolean, result: TestResult?) {
        BeginTestImpl(controller, isResult, result)
    }

    @Composable
    fun BeginTestImpl(controller: NavController, isResult: Boolean, result: TestResult?) {

        if (isResult) {
            var i by remember { mutableIntStateOf(0) }

            val resultData = ResultDataCollector.deserializeResult(result!!.result)
            val stageList = remember { resultData }

            val currentResultStage = stageList[i]

            DisplayStage(controller, currentResultStage, true) { answer ->

                if (answer == "PREV") {
                    if (i > 0) i--
                } else if (answer == "NEXT") {
                    if (i < stageList.size - 1) i++
                    else {
//                        val exitIntent = Intent(controller, Menucontroller::class.java)
                        // TODO: Dodac podsumowanie testu
                        controller.navigate("menu")
//                        controller.startcontroller(exitIntent)
                    }
                }
            }

        } else {

            var currentDifficulty by remember { mutableIntStateOf(0) }

            var currentStage by remember {
                mutableStateOf(generateStage(currentDifficulty))
            }

            DisplayStage(controller, currentStage, false) { answer ->

                if (answer == "REGENERATE") {

                    currentStage = generateStage(currentDifficulty)
                    println("Regenerate")

                } else if (currentDifficulty == stageCount) {

                    storeResult(currentStage.first, answer, currentDifficulty)
                    endTest(controller, false)

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
            generateQuestion(difficulty),
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

     fun endTest(controller: NavController, isExit: Boolean) {

         if (!isExit) {
             val localSaver = ResultDataSaver(controller.context)

             val timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
             localSaver.insert(this.testID, this.resultCollector.stages, timestamp, this.distance)

             controller.navigate("result/${localSaver.lastID}/true")

//             val testLeavingIntent = Intent(activity, TestResultActivity::class.java)
//             testLeavingIntent.putExtra("IS_AFTER", true)
//             testLeavingIntent.putExtra("RESULT_PARCEL", localSaver.lastResult)
//
//             activity.startActivity(testLeavingIntent)
         }
    }

}