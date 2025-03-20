package me.proteus.myeye.visiontests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.GrammarType
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ASRViewModel
import me.proteus.myeye.io.DBConnector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.SerializableStage
import me.proteus.myeye.isLandscape
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.next
import me.proteus.myeye.resources.optician_sans
import me.proteus.myeye.resources.prev
import me.proteus.myeye.ui.components.OrientableGrid
import me.proteus.myeye.ui.screens.res
import org.jetbrains.compose.resources.Font
import kotlin.math.*
import kotlin.random.Random

class SnellenChart : VisionTest {

    override val testID: String = "TEST_SIGHT_LOGMAR"
    override val testIcon: ImageVector = Icons.TwoTone.Face
    override val needsMicrophone: Boolean = true
    override val stageCount: Int = 10
    override val resultCollector: ResultDataCollector = ResultDataCollector()
    override var distance: Float = 0f
    override var conn: DBConnector? = null

    private var correctAnswer: String = ""

    private var asr: ASRViewModel? = null

    private fun stageToCentimeters(stage: Int): Float {

        val marBase = ((PI/180) / 60) * 5  // 5 minut katowych
        val marCurrent = marBase * 10f.pow(-stage * 0.1f)

        val height = distance * tan(marCurrent / 2) * 2
        return height.toFloat()

    }

    @Composable
    private fun cmToSp(cm: Float): Float {
        val density = LocalDensity.current
        return with(density) {
            val dpi = density.density * 160
            val px = (cm / 2.54f) * dpi
            (px / fontScale)
        }
    }

    @Composable
    fun LetterContainer(stage: Int, text: String, key: String?, modifier: Modifier = Modifier) {
        val calculatedSize = stageToCentimeters(stage)
        println("height $calculatedSize")
        val pixelSize = cmToSp(calculatedSize).sp
        println("pixelSize $pixelSize")

        OrientableGrid(
            qualifier = isLandscape(),
            columnModifier = Modifier.fillMaxHeight(),
            rowModifier = Modifier.fillMaxWidth(),
            arrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in text.indices) {
                Text(
                    text = text[i].toString(),
                    color = (if (key != null) (if (text[i] == key[i]) Color.Green else Color.Red) else Color.Black),
                    fontSize = pixelSize,
                    fontFamily = FontFamily(Font(Res.font.optician_sans)),
                    modifier = modifier.padding(8.dp)
                )
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
                Text(text = "Losuj litery")
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

        println("diff ${stage.difficulty}")

        if (!isResult) {

            val buf = asr!!.wordBuffer.collectAsState()

                println("Data: ${buf.value.joinToString(",")}")

                val mapped = buf.value.map { asr!!.grammarMapping!!.entries.first { key -> key.value == it } }

                if (mapped.size == 5) {
                    onUpdate(mapped.joinToString("") { it.key }.uppercase())
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
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LetterContainer(
                        stage = stage.difficulty,
                        text = stage.first,
                        key = null,
                        modifier = Modifier
                    )
                    if (isResult) {
                        LetterContainer(
                            stage = stage.difficulty,
                            text = stage.second,
                            key = stage.first,
                            modifier = Modifier
                        )
                    }

                }

            }

            if (!isResult) {
                ButtonRow(
                    onRegenerate = { onUpdate("REGENERATE") },
                    onSizeDecrease = {
                        onUpdate(randomText())
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
                        Text(text = Res.string.prev.res())
                    }
                    Button(onClick = { onUpdate("NEXT") }) {
                        Text(text = Res.string.next.res())
                    }
                }
            }

        }
    }

    @Composable
    override fun BeginTest(isResult: Boolean, result: TestResult?) {

        var trigger by remember { mutableStateOf(true) }

        if (!isResult) {
            asr = remember { ASRViewModel() }
            if (trigger) {
                asr?.start(GrammarType.LETTERS_LOGMAR)
                trigger = false
            }
        }

        super.BeginTest(isResult, result)

    }

    override fun generateQuestion(stage: Int?): String {

        val question: String = randomText()

        correctAnswer = question
        return question

    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }

    override fun getExampleAnswers(): Array<String> {


        val arr = Array(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = randomText()
        }
        arr[abs(Random.nextInt()) % 4] = correctAnswer

        return arr

    }

    private fun randomText(): String {

        val all = GrammarType.LETTERS_LOGMAR.items.shuffled()

        var text = ""
        var i = 0
        var j: Int = Random.nextInt(all.size)

        while(i++ < 5)  {
            text += all[j]
            j++; j %= all.size
        }

        println("text $text")

        return text
    }

    override fun endTest(isExit: Boolean) {
        println("end")
        asr?.close()
        super.endTest(isExit)
    }

}