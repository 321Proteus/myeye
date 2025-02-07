package me.proteus.myeye.visiontests

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.content.res.Configuration
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.proteus.myeye.GrammarType
import me.proteus.myeye.R
import me.proteus.myeye.util.ScreenScalingUtils.getScreenInfo
import me.proteus.myeye.SerializableStage
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ASRViewModel
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.util.ASRViewModelFactory
import java.util.Random
import kotlin.math.*

class SnellenChart : VisionTest {

    override val testID: String = "SNELLEN_CHART"
    override val testIcon: ImageVector = Icons.TwoTone.Face
    override val needsMicrophone: Boolean = true
    override val stageCount: Int = 10
    override val resultCollector: ResultDataCollector = ResultDataCollector()
    override var distance: Float = 0f

    private var correctAnswer: String = ""

    private lateinit var asr: ASRViewModel

    private fun stageToCentimeters(stage: Int): Float {

        val marBase = ((PI/180) / 60) * 5  // 5 minut katowych
        val marCurrent = marBase * 10f.pow(-stage * 0.1f)

        val height = distance * tan(marCurrent / 2)

        return height.toFloat()

    }

    @Composable
    fun LetterContainer(stage: Int, text: String, key: String?, modifier: Modifier = Modifier) {

        val config = LocalConfiguration.current
        val opticianSansFamily = FontFamily(Font(R.font.opticiansans))

        val screenDensity = getScreenInfo(LocalContext.current).densityDpi / 2.54f
        val calculatedSize = stageToCentimeters(stage)
        println(calculatedSize)
        val pixelSize = with(LocalDensity.current) { (screenDensity * calculatedSize).toSp() }

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in text.indices) {
                    Text(
                        text = text[i].toString(),
                        color = (if (key != null) (if (text[i] == key[i]) Color.Green else Color.Red) else Color.Black),
                        fontSize = pixelSize * 15,
                        fontFamily = opticianSansFamily,
                        modifier = modifier.padding(8.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in text.indices) {
                    Text(
                        text = text[i].toString(),
                        color = (if (key != null) (if (text[i] == key[i]) Color.Green else Color.Red) else Color.Black),
                        fontSize = pixelSize * 15,
                        fontFamily = opticianSansFamily,
                        modifier = modifier.padding(8.dp)
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
                Text(text = "Losuj litery")
            }
            Button(onClick = { onSizeDecrease() }) {
                Text(text = "Dalej")
            }
        }

    }

    @Composable
    override fun DisplayStage(
        controller: NavController,
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        if (!isResult) {
            val context = LocalContext.current
            val mapping = asr.grammarMapping

            asr.wordBuffer.observe(context as LifecycleOwner) { data ->

                if (data.isEmpty()) return@observe

                println("Data: ${data.joinToString(",") { it.word }}")

                val mapped = data.map { mapping.entries.first { key -> key.value == it.word} }

                if (mapped.size == 5) {
                    onUpdate(mapped.joinToString("") { it.key }.uppercase())
                    asr.clearBuffer()
                }
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
        controller: NavController,
        isResult: Boolean,
        result: TestResult?
    ) {

        val context = LocalContext.current
        val app = context.applicationContext as Application

        if (!isResult) {

            asr = viewModel(factory = ASRViewModelFactory(app))

            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                asr.initialize(GrammarType.SIDES)
            }

        } else {
            distance = result!!.distance
        }

        super.BeginTest(controller, isResult, result)

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

        val random = Random()

        val arr = Array(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = randomText()
        }
        arr[abs(random.nextInt()) % 4] = correctAnswer

        return arr

    }

    private fun randomText(): String {

        val all = GrammarType.LETTERS_LOGMAR.items.shuffled()

        val random = Random()

        var text = ""
        var i = 0
        var j: Int = random.nextInt(all.size)

        while(i++ < 5)  {
            text += all[j]
            j++; j %= all.size
        }

        return text
    }

    override fun endTest(controller: NavController, isExit: Boolean) {

        super.endTest(controller, isExit)
        if (::asr.isInitialized) asr.close()

    }

}