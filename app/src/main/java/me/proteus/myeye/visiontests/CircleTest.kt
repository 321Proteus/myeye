package me.proteus.myeye.visiontests

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import me.proteus.myeye.R
import me.proteus.myeye.ScreenScalingUtils.getScreenInfo
import me.proteus.myeye.SerializableStage
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ASRViewModel
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.SpeechDecoderResult
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random
import kotlin.math.*

class CircleTest : VisionTest {

    override val testID: String = "TEST_CIRCLE"
    override val testIcon: ImageVector = Icons.Outlined.AccountCircle

    override val stageCount: Int = 10

    private var correctAnswer: String = ""

    private lateinit var asr: ASRViewModel

    override val resultCollector: ResultDataCollector = ResultDataCollector()

    fun stageToCentimeters(stage: Int, distance: Float): Float {

        var marBase = ((PI/180) / 60) * 5  // 5 minut katowych
        var marCurrent = marBase * 10f.pow(-stage * 0.1f)

        var height = 2 * distance * tan(marCurrent / 2)

        return height.toFloat()

    }

    @Composable
    fun LetterContainer(currentStage: Int, directions: String, key: String?, modifier: Modifier = Modifier) {

        val config = LocalConfiguration.current
        val opticianSansFamily = FontFamily(Font(R.font.opticiansans))

        var screenDensity = getScreenInfo(LocalContext.current).densityDpi / 2.54f
        var calculatedSize = stageToCentimeters(currentStage, 100f)
        println(calculatedSize)
        var pixelSize = with(LocalDensity.current) { (screenDensity * calculatedSize).toSp() }

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,

            ) {
                for (i in 0..<directions.length) {
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0..<directions.length) {
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
        activity: VisionTestLayoutActivity,
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        if (!isResult) {
            val context = LocalContext.current
            val mapping = asr.grammarMapping

            asr.wordBuffer.observe(context as LifecycleOwner) { data ->

                println("Data: ${data.map { it -> it.word }.joinToString(",")}")

                val mapped = data.map { it -> mapping.entries.first { key -> key.value == it.word} }
                val directions = mapped.map { it ->
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
        activity: VisionTestLayoutActivity,
        isResult: Boolean,
        result: TestResult?,
        rpl: ActivityResultLauncher<String>?
    ) {

        if (!isResult) {
            asr = ViewModelProvider(
                activity,
                ViewModelProvider.AndroidViewModelFactory.getInstance(activity.application)
            )[ASRViewModel::class]

            if (activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                asr.initialize()
            } else {
                rpl?.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        super.BeginTest(activity, isResult, result, null)

    }

    override fun generateQuestion(stage: Int?): String {

        var question: String = generateDirections()

        correctAnswer = question
        return question

    }

    fun generateDirections(): String {

        var directions = "0123"
        var text: String = ""
        var i: Int = 0

        var random = Random()

        while(i++ < 5)  {
            text += directions[abs(random.nextInt() % 4)]
        }
        println(text)
        return text

    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }

    override fun getExampleAnswers(): Array<String> {

        var random = Random()

        var arr = Array<String>(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = generateDirections()
        }
        arr[abs(random.nextInt()) % 4] = correctAnswer

        return arr

    }

    override fun endTest(activity: VisionTestLayoutActivity, isExit: Boolean) {

        if (!isExit) {
            super.endTest(activity, true)
        } else {
            asr.close()
        }

    }

}