package me.proteus.myeye.visiontests

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import me.proteus.myeye.resources.Res
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.DBConnector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.SerializableStage
import me.proteus.myeye.resources.farnsworth_colors
import me.proteus.myeye.resources.next
import me.proteus.myeye.resources.prev
import me.proteus.myeye.resources.test_correct
import me.proteus.myeye.resources.test_user
import me.proteus.myeye.ui.screens.res
import org.jetbrains.compose.resources.stringArrayResource

class ColorArrangementTest : VisionTest {

    override val testID: String = "TEST_COLOR_ARRANGE"
    override val testIcon: ImageVector = Icons.AutoMirrored.Outlined.List
    override val needsMicrophone: Boolean = false
    override val stageCount: Int = 6
    override val resultCollector: ResultDataCollector = ResultDataCollector()
    override var distance: Float = -1f
    override var conn: DBConnector? = null

    private var colorOffset = -10

    private var colors: List<String> = listOf()

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
//                Text(
//                    modifier = Modifier
//                        .scale(if (index == cd) 0.66f else 1f, 1f),
//                    text = "${getHue(item)}"
//                )
            }
        }

    }

    @Composable
    override fun DisplayStage(
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {
        println(stage.first + " $isResult (${stage.second})")
        if (stage.first.isNotEmpty())  println("First ${stage.first.split(' ').map { getHue(it) }}")
        if (stage.second.isNotEmpty()) { println("Second ${stage.second.split(' ').map { getHue(it) }}") }

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
                .fillMaxSize()
                .padding(18.dp),
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

                    if (isResult) Text(Res.string.test_user.res())

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

                        if (correctnessMap.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, null)
                            }
                        } else {
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

                    }

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(Res.string.test_correct.res())

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = {
                            val ans = stageColors.joinToString(" ")
                            println(getScore(ans, "RELATIVE"))
                            onUpdate(ans)
                        }
                    ) { Text(Res.string.next.res()) }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { onUpdate("PREV") }) {
                        Text(text = (Res.string.prev.res()))
                    }
                    Button(onClick = { onUpdate("NEXT") }) {
                        Text(text = Res.string.next.res())
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
        colors = stringArrayResource(Res.array.farnsworth_colors)

        BeginTestImpl(isResult, result)

            // zwieksz colorOffset gdy wynik jest bliski 100

    }

    override fun generateQuestion(stage: Int?): String {
        colorOffset += 10
        return prepareArray(
            old = colors.toList(),
            freq = 1,
            count = 10,
            offset = colorOffset
        ).joinToString(" ").trim()
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
        val a = parseColor(color)
        return colorToHSV(Color(a).toArgb())[0]
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

    companion object {
        fun parseColor(colorString: String): Long {
            val color = colorString.trim().removeSurrounding("\"")
            val hex = "ff" + color.removePrefix("#").lowercase()
            return hex.toLong(16)
        }

        fun blendARGB(color1: Int, color2: Int, ratio: Float): Int {
            val alpha1 = (color1 shr 24) and 0xFF
            val red1 = (color1 shr 16) and 0xFF
            val green1 = (color1 shr 8) and 0xFF
            val blue1 = color1 and 0xFF

            val alpha2 = (color2 shr 24) and 0xFF
            val red2 = (color2 shr 16) and 0xFF
            val green2 = (color2 shr 8) and 0xFF
            val blue2 = color2 and 0xFF

            val alpha = (alpha1 + (alpha2 - alpha1) * ratio).toInt()
            val red = (red1 + (red2 - red1) * ratio).toInt()
            val green = (green1 + (green2 - green1) * ratio).toInt()
            val blue = (blue1 + (blue2 - blue1) * ratio).toInt()

            return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
        }
    }


    private fun colorToHSV(color: Int): FloatArray {
        val r = ((color shr 16) and 0xFF).toFloat() / 255f
        val g = ((color shr 8) and 0xFF).toFloat() / 255f
        val b = (color and 0xFF).toFloat() / 255f

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        val h = when {
            delta == 0f -> 0f
            max == r -> ((g - b) / delta) % 6f * 60f
            max == g -> ((b - r) / delta + 2f) * 60f
            else -> ((r - g) / delta + 4f) * 60f
        }.let { if (it < 0) it + 360f else it }

        val s = if (max == 0f) 0f else delta / max

        return floatArrayOf(h, s * 100, max * 100)
    }


}