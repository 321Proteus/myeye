package me.proteus.myeye.visiontests

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.DBConnector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.SerializableStage
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.next
import me.proteus.myeye.resources.none
import me.proteus.myeye.resources.prev
import me.proteus.myeye.resources.test_plate
import me.proteus.myeye.ui.screens.res

class IshiharaTest : VisionTest {

    override val testID = "TEST_COLOR_PLATE"
    override var distance = -1f
    override val needsMicrophone = false
    override val resultCollector = ResultDataCollector()
    override val stageCount = 11
    override val testIcon = Icons.TwoTone.Star
    override var conn: DBConnector? = null

    private var currentID = 0

    data class IshiharaData(
        val id: String,
        var possibilities: List<Int>
    )

    private val data = listOf(
        IshiharaData("01", listOf(16, 12)),
        IshiharaData("03", listOf(39, 25, 22, 29)),
        IshiharaData("04", listOf(6, -1, 3, 5)),
        IshiharaData("05", listOf(18, 6, 9, 3)),
        IshiharaData("07", listOf(24, 71, 11, 74)),
        IshiharaData("08", listOf(-1, 5, 3, 6)),
        IshiharaData("09", listOf(11, 43, 15, 45)),
        IshiharaData("11", listOf(1, 3, -1, 6)),
        IshiharaData("10", listOf(4, -1, 3, 5)),
        IshiharaData("12", listOf(26, 13, -1, 16)),
        IshiharaData("13", listOf(13, 72, 12, 73)),
        IshiharaData("15", listOf(19, 11, 21, -1)),
    )

    @Composable
    override fun DisplayStage(
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        var showAnswer by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(contentAlignment = Alignment.Center) {
                Text(Res.string.test_plate.res())
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                val url = getUrl(data.find { it.possibilities.last().toString() == stage.first }!!)
                val painter = rememberAsyncImagePainter(url)

                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
                if (isResult && showAnswer) {
                    Text(
                        text = stage.first,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 180.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                if (isResult) {
                    Button(onClick = { onUpdate("PREV") }) {
                        Text(Res.string.next.res())
                    }
                    Button(onClick = { onUpdate("NEXT") }) {
                        Text(Res.string.prev.res())
                    }
                    Button(onClick = { showAnswer = !showAnswer }) {
                        Text(if (showAnswer) "Ukryj odpowiedź" else "Pokaż odpowiedź")
                    }
                } else {
                    for (el in data[currentID].possibilities.shuffled()) {
                        Button(onClick = {
                            onUpdate(el.toString())
                            currentID++
                        }) { Text(if (el == -1) Res.string.none.res() else el.toString()) }
                    }
                }


            }
        }
    }

    override fun generateQuestion(stage: Int?): String {
        println(stage)
        return data[stage ?: 0].possibilities.last().toString()
    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == "-1"
    }

    override fun getExampleAnswers(): Array<String> {
        val posList = data[currentID].possibilities.map { it.toString() }
        return posList.toTypedArray()
    }

    private fun getUrl(base: IshiharaData): String {
        return "https://www.blindnesstest.com/_next/image/?url=/images/ishihara/plate-${base.id}.png&w=640"
    }

}