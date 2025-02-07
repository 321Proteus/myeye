package me.proteus.myeye.visiontests

import android.graphics.Color.parseColor
import android.graphics.Color.colorToHSV
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import me.proteus.myeye.R
import me.proteus.myeye.SerializableStage
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector

class ColorArrangementTest : VisionTest {

    override val testID: String = "COLOR_ARRANGE"
    override val testIcon: ImageVector = Icons.AutoMirrored.Outlined.List
    override val needsMicrophone: Boolean = false
    override val stageCount: Int = 6
    override val resultCollector: ResultDataCollector = ResultDataCollector()
    override var distance: Float = -1f

    private val difficultyScale = listOf(7, 6, 5, 4, 3, 2, 1)
    private var colorOffset = 0

    private var colors: Array<String> = arrayOf()

    @Composable
    fun FarnsworthItem(modifier: Modifier, item: String, index: Int, cd: Int?) {

        val isTopEdge = (index == 0)
        val isBottomEdge = (index == 9)

        val edgeShape: Shape = RoundedCornerShape(
            topStart = (if (isTopEdge) 15.dp else 0.dp),
            topEnd = (if (isTopEdge) 15.dp else 0.dp),
            bottomEnd = (if (isBottomEdge) 15.dp else 0.dp),
            bottomStart = (if (isBottomEdge) 15.dp else 0.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight(0.1f)
                .then(modifier)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(edgeShape)
                    .background(Color(parseColor(item)))
                    .then(
                        if (isTopEdge || isBottomEdge) {
                            Modifier.border(
                                width = (2.dp),
                                brush = SolidColor(Color.Black),
                                shape = edgeShape
                            )
                        } else Modifier
                    ),

                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .scale(if (index == cd) 0.66f else 1f, 1f),
                    text = "${getHue(item)}"
                )
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

        if (stage.first.isNotEmpty())  println("First ${stage.first.split(' ').map { getHue(it) }}")
        if (stage.second.isNotEmpty()) println("Second ${stage.second.split(' ').map { getHue(it) }}")

        val questionColors by remember(stage) {
            derivedStateOf {
                if (isResult) stage.second.split(' ')
                else stage.first.split(' ')
            }
        }

        val answerColors by remember(stage) {
            derivedStateOf {
                questionColors.sortedBy { getHue(it) }
            }
        }

        var stageColors by remember { mutableStateOf(questionColors) }

        var correctnessMap by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }

        LaunchedEffect(questionColors) {
            stageColors = questionColors
            if (isResult) {
                correctnessMap = getCorrectnessMapping(answerColors, stageColors)
            }

        }

        var currentlyDragged by remember { mutableStateOf<Int?>(null) }
        var currentOffset by remember { mutableFloatStateOf(0f) }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = if (isResult) Arrangement.SpaceBetween else Arrangement.SpaceEvenly
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (isResult) Text("Odpowiedź\nużytkownika")

                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        itemsIndexed(stageColors) { index, item ->

                            val isTopEdge = (index == 0)
                            val isBottomEdge = (index == 9)

                            val movableModifier: Modifier = Modifier
                                .offset { IntOffset(0, if (currentlyDragged == index) currentOffset.toInt() else 0) }
                                .scale(if (index == currentlyDragged) 1.5f else 1f, 1f)
                                .zIndex((if (index == currentlyDragged) 1f else 0f))
                                .draggable(
                                    enabled = (!(isTopEdge || isBottomEdge || isResult)),
                                    orientation = Orientation.Vertical,
                                    state = rememberDraggableState { delta ->

                                        if (currentlyDragged == null) currentlyDragged = index
                                        currentOffset += delta

                                        val currentIndex = currentlyDragged ?: return@rememberDraggableState

                                        val targetIndex = when {
                                            currentOffset > 120 -> currentIndex + 1
                                            currentOffset < -120 -> currentIndex - 1
                                            else -> null
                                        }

                                        targetIndex?.let { target ->
                                            if (targetIndex == 0 || targetIndex == 9) return@rememberDraggableState
                                            if (target in stageColors.indices) {

                                                stageColors = stageColors.toMutableList().apply {
                                                    val draggedItem = removeAt(currentIndex)
                                                    add(target, draggedItem)
                                                }
                                                currentlyDragged = target
                                                currentOffset = 0f
                                            }
                                        }
                                    },
                                    onDragStopped = {
                                        currentlyDragged = null
                                        currentOffset = 0f
                                    }
                                )

                            FarnsworthItem(movableModifier, item, index, currentlyDragged)

                        }
                    }
                }

                if (isResult) {
                    var startX = 0f
                    var startY = 0f

                    Column (
                        modifier = Modifier
                            .weight(1f)
                            .onGloballyPositioned { lc ->
                                startX = lc.positionInWindow().x
                                startY = lc.positionInWindow().y
                                println("$startX $startY")
                            }
                    ) {

                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            for (el in correctnessMap) {
                                drawLine(
                                    start = Offset(x = 0f, y = (startY + el.value * 60).dp.toPx()),
                                    end = Offset(x = size.width, y = (startY + el.key * 60).dp.toPx()),
                                    color = Color.Black,
                                    strokeWidth = 4f
                                )
                            }
                        }

                    }

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text("Poprawna\nodpowiedź")

                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            itemsIndexed(answerColors) { index, item ->

                                FarnsworthItem(Modifier, item, index, currentlyDragged)

                            }
                        }
                    }

                }

            }

            if (!isResult) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            val ans = stageColors.joinToString(" ")
                            println(getScore(ans, "RELATIVE"))
                            onUpdate(ans)
                        }
                    ) {
                        Text("Dalej")
                    }
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


    @Composable
    override fun BeginTest(
        controller: NavController,
        isResult: Boolean,
        result: TestResult?
    ) {
        colors = stringArrayResource(R.array.farnsworth_colors)

        BeginTestImpl(controller, isResult, result)

            // zwieksz colorOffset gdy wynik jest bliski 100

    }

    override fun generateQuestion(stage: Int?): String {
        return prepareArray(
            old = colors.toList(),
            freq = (if (stage != null) difficultyScale[stage] else 7),
            count = 10,
            offset = colorOffset
        ).joinToString(" ")
    }

    override fun getExampleAnswers(): Array<String> {
        return arrayOf()
    }

    override fun checkAnswer(answer: String): Boolean {

        return true

    }

    private fun getScore(answer: String, measurementMode: String): Int {

        val answeredArray = answer.split(' ').map { getHue(it) }
        val orderedArray = answeredArray.sorted()
        val size = answeredArray.size

        var percent = 0

        when (measurementMode) {
            "ABSOLUTE" -> {

                for (i in 0..<size) {
                    if (i == orderedArray.indexOf(answeredArray[i])) percent += 10
                }

            }
            "RELATIVE" -> {

                for (i in 1..<size) {
                    if (answeredArray[i] > answeredArray[i - 1]) percent += 10
                }

                percent += 10

            }
            "LEVENSHTEIN" -> {

                val lengths = MutableList(size) { 0 }
                var i = 0

                while (i < size - 1) {

                    var j = 1
                    while (i + j < size && isSubArray(
                            orderedArray.slice(i..i + j),
                            answeredArray.slice(i..i + j)
                        )
                    ) j++
                    lengths[i] = j
                    i += j
                }
                for (l in lengths) {
                    if (l > 1) percent += 10 * l
                }

            }
        }

        return percent

    }

    private fun getCorrectnessMapping(a: List<String>, b: List<String>): Map<Int, Int> {

        val map: MutableMap<Int, Int> = HashMap()
        val connected = b.joinToString(" ")

        var i = 1
        while (i < a.size - 1) {

            val idx = b.indexOf(a[i])
            var j = 0

            while(idx + j < a.size && connected.indexOf(a.subList(idx, idx+j).joinToString(" ")) != -1) j++

            if (idx != i) map[i] = idx

            i += j
            if (j > 1) i -= 1

        }

        return map

    }

    private inline fun <reified T> isSubArray(a: List<T>, b: List<T>): Boolean {

        return a.windowed(b.size).any {
            it.toTypedArray().contentEquals(b.toTypedArray())
        }

    }

    private fun getHue(color: String): Float {

        val hsv = FloatArray(3)
        val a = Color(parseColor(color)).toArgb()

        colorToHSV(a, hsv)

        return hsv[0]
    }

    private fun prepareArray(old: List<String>, freq: Int, count: Int, offset: Int = 0): ArrayList<String> {

        var new = ArrayList<String>()
        var i: Int = offset

        while(new.size < count) {
            new.add(old[i % old.size])
            i += freq
        }

        new = ArrayList(new.subList(0, count))
        val begin = new.first()
        val end = new.last()

        new = ArrayList(new.subList(1, count - 1))

        new.shuffle()
        new.add(0, begin)
        new.add(end)

        return new

    }


}