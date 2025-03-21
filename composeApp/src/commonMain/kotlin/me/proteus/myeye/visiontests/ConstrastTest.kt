package me.proteus.myeye.visiontests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.DBConnector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.SerializableStage
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.next
import me.proteus.myeye.resources.prev
import me.proteus.myeye.resources.test_contrast
import me.proteus.myeye.ui.screens.res
import me.proteus.myeye.visiontests.ColorArrangementTest.Companion.blendARGB
import me.proteus.myeye.visiontests.ColorArrangementTest.Companion.parseColor
import kotlin.random.Random

class ConstrastTest : VisionTest {

    override val testID: String = "TEST_COLOR_CONTRAST"
    override val testIcon: ImageVector = Icons.Outlined.AccountCircle
    override val needsMicrophone: Boolean = false
    override val stageCount: Int = 5
    override val resultCollector: ResultDataCollector = ResultDataCollector()
    override var distance: Float = -1f
    override var conn: DBConnector? = null

    private var correctAnswer: String = ""

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun DisplayStage(
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        var sliderPos by remember { mutableFloatStateOf(0f) }

        val stageColor = Color(parseColor(stage.first))
        val finalColor = blendARGB(Color.Black.toArgb(), stageColor.toArgb(), 1-sliderPos / 5)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box (contentAlignment = Alignment.Center) {
                Text(Res.string.test_contrast.res())
            }
            Box (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(stageColor)
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center,
            ) {

                Text(
                    text = stage.first,
                    color = Color(finalColor),
                    fontSize = 48.sp
                )

            }

            if (!isResult) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Slider(
                        value = sliderPos,
                        onValueChange = { sliderPos = it },
                        onValueChangeFinished = {
                            onUpdate(sliderPos.toString())
                            sliderPos = 0f
                        },
                        thumb = {
                            Spacer(
                                modifier = Modifier.size(20.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                )
                        },
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { onUpdate("PREV") }) {
                        Text(text = Res.string.prev.res())
                    }
                    Button(onClick = { onUpdate("NEXT") }) {
                        Text(text = Res.string.next.res())
                    }
                }
            }

        }

    }

    override fun generateQuestion(stage: Int?): String {
        var text = ""

        for (i in 0..2) text += (if (Random.nextBoolean()) "FF" else "00")

        return if (text == "000000" || text == "FFFFFF") generateQuestion(stage)
        else "#FF$text"
    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }

    override fun getExampleAnswers(): Array<String> {
        val arr = Array(4) { "" }
        return arr
    }

}