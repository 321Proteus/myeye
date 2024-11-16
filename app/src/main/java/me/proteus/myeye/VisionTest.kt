package me.proteus.myeye

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import me.proteus.myeye.ui.VisionTestLayoutActivity

interface VisionTest {

    val stageCount: Int
    val currentStage: Int

    /**
     * Display the test stage as Composable in the target activity context
     * @param activity The canvas activity to display the layout
     */
    @Composable
    fun DisplayStage(activity: VisionTestLayoutActivity, modifier: Modifier)

    fun generateQuestion(): Any

    fun getExampleAnswers(): Array<String>

    fun checkAnswer(answer: String): Boolean

}