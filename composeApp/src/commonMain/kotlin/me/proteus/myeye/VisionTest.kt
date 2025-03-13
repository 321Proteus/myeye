package me.proteus.myeye

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.datetime.Clock
import me.proteus.myeye.io.DBConnector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.io.SerializableStage

interface VisionTest {

    val testID: String
    val testIcon: ImageVector

    val stageCount: Int

    val resultCollector: ResultDataCollector
    var conn: DBConnector?

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
     */
    @Composable
    fun DisplayStage(
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    )

    @Composable
    fun BeginTest(isResult: Boolean, result: TestResult?) {
        BeginTestImpl(isResult, result)
    }

    @Composable
    fun BeginTestImpl(isResult: Boolean, result: TestResult?) {

        conn = ResultDataSaver.getConnection()

        if (isResult) {
            var i by remember { mutableIntStateOf(0) }

            val resultData = resultCollector.deserializeResult(result!!.result)
            val stageList = remember { resultData }

            val currentResultStage = stageList[i]

            DisplayStage(currentResultStage, true) { answer ->

                if (answer == "PREV") {
                    if (i > 0) i--
                } else if (answer == "NEXT") {
                    if (i < stageList.size - 1) i++
                    else {
                        // TODO: Dodac podsumowanie testu
                        navigate("menu")
                    }
                }
            }

        } else {

            var currentDifficulty by remember { mutableIntStateOf(0) }

            var currentStage by remember {
                mutableStateOf(generateStage(currentDifficulty))
            }

            DisplayStage(currentStage, false) { answer ->

                if (answer == "REGENERATE") {

                    currentStage = generateStage(currentDifficulty)
                    println("Regenerate")

                } else if (currentDifficulty == stageCount) {

                    storeResult(currentStage.first, answer, currentDifficulty)
                    endTest(false)

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

     fun endTest(isExit: Boolean) {

         if (!isExit && conn != null) {

             val stages = this.resultCollector.stages
             val serialized = ResultDataCollector().serializeResult(stages)

             var lastID = ResultDataSaver.getLastID(conn!!)

             // W SQLite klucze podstawowe licza sie od 1 (bruh)
             if (lastID == -1) lastID = 1
             else lastID++

             val timestamp = Clock.System.now().epochSeconds
             val result = TestResult(
                 lastID, this.testID, timestamp, this.distance, serialized
             )

             ResultDataSaver.insert(conn!!, result)

             conn!!.close()

             navigate("result/${lastID}/true")

         }
    }

}