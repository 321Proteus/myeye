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
import androidx.lifecycle.ViewModelProvider
import me.proteus.myeye.GrammarType
import me.proteus.myeye.R
import me.proteus.myeye.ScreenScalingUtils.getScreenInfo
import me.proteus.myeye.SerializableStage
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ASRViewModel
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random
import kotlin.math.*

class SnellenChart : VisionTest {

    override val testID: String = "SNELLEN_CHART"
    override val testIcon: ImageVector = Icons.TwoTone.Face
    override val stageCount: Int = 10
    override val resultCollector: ResultDataCollector = ResultDataCollector()
    override var distance: Float = 0f

    private var correctAnswer: String = ""

    private lateinit var asr: ASRViewModel

    fun stageToCentimeters(stage: Int, distance: Float): Float {

        var marBase = ((PI/180) / 60) * 5  // 5 minut katowych
        var marCurrent = marBase * 10f.pow(-stage * 0.1f)

        var height = 2 * distance * tan(marCurrent / 2)

        return height.toFloat()

    }

    @Composable
    fun LetterContainer(stage: Int, text: String, key: String?, modifier: Modifier = Modifier) {

        val config = LocalConfiguration.current
        val opticianSansFamily = FontFamily(Font(R.font.opticiansans))

        var screenDensity = getScreenInfo(LocalContext.current).densityDpi / 2.54f
        var calculatedSize = stageToCentimeters(stage, 100f)
        println(calculatedSize)
        var pixelSize = with(LocalDensity.current) { (screenDensity * calculatedSize).toSp() }

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in 0..<text.length) {
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
                for (i in 0..<text.length) {
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
        activity: VisionTestLayoutActivity,
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        if (!isResult) {
            val context = LocalContext.current
            val mapping = asr.grammarMapping

            asr.wordBuffer.observe(context as LifecycleOwner) { data ->

                if (data.isEmpty()) return@observe

                println("Data: ${data.map { it -> it.word }.joinToString(",")}")

                val mapped = data.map { it -> mapping.entries.first { key -> key.value == it.word} }

                if (mapped.size == 5) {
                    onUpdate(mapped.map { it -> it.key }.joinToString("").uppercase())
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
                        onUpdate(randomText(5))
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
                asr.initialize(GrammarType.LETTERS_LOGMAR)
            } else {
                rpl?.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        super.BeginTest(activity, isResult, result, null)

    }

    override fun generateQuestion(stage: Int?): String {

        var question: String = randomText(5)

        correctAnswer = question
        return question

    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }

    override fun getExampleAnswers(): Array<String> {

        var random = Random()

        var arr = Array<String>(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = randomText(5)
        }
        arr[abs(random.nextInt()) % 4] = correctAnswer.toString()

        return arr

    }

    fun randomText(n: Int): String {

        var all = GrammarType.LETTERS_LOGMAR.items.shuffled()

        var random = Random()

        var text: String = ""
        var i: Int = 0
        var j: Int = random.nextInt(all.size)

        while(i++ < n)  {
            text += all[j]
            j++; j %= all.size
        }

        return text
    }

    override fun endTest(activity: VisionTestLayoutActivity, isExit: Boolean) {

        super.endTest(activity, isExit)
        if (::asr.isInitialized) asr.close()

    }

}