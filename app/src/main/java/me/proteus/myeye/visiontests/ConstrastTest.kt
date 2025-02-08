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
import androidx.core.graphics.ColorUtils
import me.proteus.myeye.SerializableStage
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random

class ConstrastTest : VisionTest {

    override val testID: String = "TEST_COLOR_CONTRAST"
    override val testIcon: ImageVector = Icons.Outlined.AccountCircle
    override val needsMicrophone: Boolean = false
    override val stageCount: Int = 5
    override val resultCollector: ResultDataCollector = ResultDataCollector()
    override var distance: Float = -1f

    private var correctAnswer: String = ""

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun DisplayStage(
        activity: VisionTestLayoutActivity,
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        var sliderPos by remember { mutableFloatStateOf(0f) }

        val stageColor = Color(android.graphics.Color.parseColor(stage.first))
        val finalColor = ColorUtils.blendARGB(Color.Black.toArgb(), stageColor.toArgb(), 1-sliderPos / 30)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box (contentAlignment = Alignment.Center) {
                Text("Przesuwaj, aż symbole staną się widoczne")
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
                        Text(text = "Poprzedni etap")
                    }
                    Button(onClick = { onUpdate("NEXT") }) {
                        Text(text = "Następny etap")
                    }
                }
            }

        }

    }

    override fun generateQuestion(stage: Int?): String {

        val random = Random()
        var text = ""

        for (i in 0..2) text += (if (random.nextBoolean() == true) "FF" else "00")

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