package me.proteus.myeye.visiontests

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.AddCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.R
import me.proteus.myeye.SerializableStage
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.ui.VisionTestLayoutActivity
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.random.Random

class OpacityTest : VisionTest {

    override val testID = "TEST_SIGHT_GAP"
    override var distance = -1f
    override val needsMicrophone = false
    override val resultCollector = ResultDataCollector()
    override val stageCount = 20
    override val testIcon = Icons.TwoTone.AddCircle

    @Composable
    override fun DisplayStage(
        activity: VisionTestLayoutActivity,
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(contentAlignment = Alignment.Center) {
                Text("Jaki numer widać na ekranie?")
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center,
            ) {

                val alpha = (stageCount-stage.difficulty + 2) / 250f

                Text(
                    text = "C",
                    color = Color.Black.copy(alpha = alpha),
                    fontFamily = FontFamily(Font(R.font.opticiansans)),
                    fontSize = 256.sp,
                    modifier = Modifier
                        .rotate(stage.first.toInt() * 45f)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center,
            ) {

                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val segment = getSegment(
                                offset,
                                size.width / 4f,
                                Offset(size.width / 2f, size.height / 2f)
                            )
                            onUpdate(segment.toString())
                        }
                    }) {
                    val radius = size.minDimension / 2
                    val center = size.center//Offset(size.width / 2, size.height / 2)

                    drawArc(
                        color = Color.Black,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = true,
                        topLeft = Offset(center.x - radius/2, center.y - radius/2),
                        size = size.div(2f)
                    )

                    for (i in 0 until 8) {
                        rotate(i * 45f + 22.5f, pivot = center) {
                            drawArc(
                                color = Color.Black,
                                startAngle = 5f,
                                sweepAngle = 40f,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2)
                            )
                        }
                    }
                }
            }

        }
    }

    private fun getSegment(clickPosition: Offset, radius: Float, center: Offset): Int {
        val x = clickPosition.x - center.x
        val y = clickPosition.y - center.y
        val distance = sqrt((x * x + y * y).toDouble())

        if (distance > radius * 2) return -1

        val angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble())) + 22.5f
        val adjustedAngle = if (angle < 0) angle + 360 else angle

        val segmentAngle = 360 / 8
        return (adjustedAngle / segmentAngle).toInt()
    }

    override fun generateQuestion(stage: Int?): String {
        return Random.nextInt().mod(8).toString()
    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == "-1"
    }

    override fun getExampleAnswers(): Array<String> {
        return arrayOf("")
    }

}