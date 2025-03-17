package me.proteus.myeye.visiontests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import me.proteus.myeye.GrammarType
import me.proteus.myeye.io.SerializableStage
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ASRViewModel
import me.proteus.myeye.io.DBConnector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.isLandscape
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.optician_sans
import org.jetbrains.compose.resources.Font
import kotlin.math.*
import kotlin.random.Random

class CircleTest : VisionTest {

    override val testID: String = "TEST_SIGHT_CIRCLE"
    override val testIcon: ImageVector = Icons.Outlined.AccountCircle
    override val needsMicrophone: Boolean = true
    override val stageCount: Int = 10
    override var conn: DBConnector? = null

    private var correctAnswer: String = ""

    override var distance: Float = 0f

    private var asr: ASRViewModel? = null

    override val resultCollector: ResultDataCollector = ResultDataCollector()

    private fun stageToCentimeters(stage: Int): Float {

        val marBase = ((PI/180) / 60) * 5  // 5 minut katowych
        val marCurrent = marBase * 10f.pow(-stage * 0.1f)

        val height = distance * tan(marCurrent / 2)

        return height.toFloat()

    }

    @Composable
    fun LetterContainer(currentStage: Int, directions: String, key: String?, modifier: Modifier = Modifier) {

        val opticianSansFamily = FontFamily(Font(Res.font.optician_sans))

        val screenDensity = LocalDensity.current.density / 2.54f
        val calculatedSize = stageToCentimeters(currentStage)
        println(calculatedSize)
        val pixelSize = with(LocalDensity.current) { (screenDensity * calculatedSize).toSp() }

        if (isLandscape()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,

            ) {
                for (i in directions.indices) {
                    Text(
                        text = "C",
                        modifier = modifier
                            .rotate(directions[i].digitToInt().toFloat() * 90f),
                        color =(if (key != null) (if (directions[i] == key[i]) Color.Green else Color.Red) else Color.Black),
                        fontSize = pixelSize * 15,
                        fontFamily = opticianSansFamily,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,//Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in directions.indices) {
                    Text(
                        text = "C",
                        modifier = modifier
                            .rotate(directions[i].digitToInt().toFloat() * 90f),
                        color =(if (key != null) (if (directions[i] == key[i]) Color.Green else Color.Red) else Color.Black),
                        fontSize = pixelSize * 15,
                        fontFamily = opticianSansFamily,

                    )
                }
            }
        }

    }

    @Composable
    fun ButtonRow(
        onRegenerate: () -> Unit,
        onSizeDecrease: () -> Unit,
        modifier: Modifier = Modifier) {

        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { onRegenerate() }) {
                Text(text = "Losuj ustawienie")
            }
            Button(onClick = { onSizeDecrease() }) {
                Text(text = "Dalej")
            }
        }

    }

    @Composable
    override fun DisplayStage(
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        if (!isResult) {

            val buf = asr!!.wordBuffer.collectAsState()

            println("Data: ${buf.value.joinToString(",")}")

            val mapped = buf.value.map { asr!!.grammarMapping!!.entries.first { key -> key.value == it } }
            val directions = mapped.map {
                when (it.key) {
                    "right" -> 0
                    "bottom" -> 1
                    "left" -> 2
                    "top" -> 3
                    else -> "error"
                }
            }.joinToString("")

            if (mapped.size == 5) {
                onUpdate(directions)
                asr!!.clearBuffer()
            }

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box (
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center,
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    LetterContainer(
                        directions = stage.first,
                        key = null,
                        currentStage = stage.difficulty,
                        modifier = Modifier
                    )

                    if (isResult) {
                        LetterContainer(
                            directions = stage.second,
                            key = stage.first,
                            currentStage = stage.difficulty,
                            modifier = Modifier
                        )
                    }
                }

            }

            if (!isResult) {
                ButtonRow(
                    onRegenerate = { onUpdate("REGENERATE") },

                    onSizeDecrease = {
                        onUpdate(generateDirections())
                    }
                )
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

    @Composable
    override fun BeginTest(
        isResult: Boolean,
        result: TestResult?
    ) {
        var trigger by remember { mutableStateOf(true) }

        if (!isResult) {
            asr = remember { ASRViewModel() }
            if (trigger) {
                asr?.start(GrammarType.LETTERS_LOGMAR)
                trigger = false
            }
        } else {
            distance = result!!.distance
        }

        super.BeginTest(isResult, result)
    }

    override fun generateQuestion(stage: Int?): String {

        val question: String = generateDirections()

        correctAnswer = question
        return question

    }

    private fun generateDirections(): String {

        val directions = "0123"
        var text = ""
        var i = 0

        while(i++ < 5)  {
            text += directions[abs(Random.nextInt() % 4)]
        }

        return text

    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }

    override fun getExampleAnswers(): Array<String> {

        val arr = Array(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = generateDirections()
        }
        arr[abs(Random.nextInt()) % 4] = correctAnswer

        return arr

    }

    override fun endTest(isExit: Boolean) {

        super.endTest(isExit)
        asr?.close()

    }

}