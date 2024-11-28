package me.proteus.myeye.visiontests

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import me.proteus.myeye.SerializablePair
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.ui.VisionTestLayoutActivity

class ColorArrangementTest : VisionTest {
    override val testID: String = "COLOR_ARRANGE"
    override val testIcon: ImageVector = Icons.AutoMirrored.Outlined.List
    override val stageCount: Int = 4
    override val resultCollector: ResultDataCollector = ResultDataCollector()

    @Composable
    override fun DisplayStage(
        activity: VisionTestLayoutActivity,
        modifier: Modifier,
        stages: List<SerializablePair>,
        isResult: Boolean
    ) {



    }

    @Composable
    override fun BeginTest(
        activity: VisionTestLayoutActivity,
        modifier: Modifier,
        isResult: Boolean,
        result: TestResult?
    ) {

    }

    override fun generateQuestion(): Any {
        TODO("Not yet implemented")
    }

    override fun getExampleAnswers(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun checkAnswer(answer: String): Boolean {
        TODO("Not yet implemented")
    }


}