package me.proteus.myeye

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.VisionTestLayoutActivity

interface VisionTest {

    val stageCount: Int
    val currentStage: Int

    val resultCollector: ResultDataCollector

    /**
     * Display the test stage as Composable in the target activity context
     * @param activity The canvas activity to display the layout
     */
    @Composable
    fun DisplayStage(activity: VisionTestLayoutActivity, modifier: Modifier)

    fun generateQuestion(): Any

    fun getExampleAnswers(): Array<String>

    fun checkAnswer(answer: String): Boolean

    fun storeResult(question: String, answer: String)

    fun endTest(activity: VisionTestLayoutActivity)

}