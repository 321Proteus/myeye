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
import androidx.compose.runtime.mutableIntStateOf
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
import me.proteus.myeye.R
import me.proteus.myeye.SerializablePair
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.ui.VisionTestLayoutActivity

class ColorArrangementTest : VisionTest {

    override val testID: String = "COLOR_ARRANGE"
    override val testIcon: ImageVector = Icons.AutoMirrored.Outlined.List
    override val stageCount: Int = 6
    override val resultCollector: ResultDataCollector = ResultDataCollector()

    val difficultyScale = listOf(8, 7, 5, 4, 3, 2, 1)
    var colorOffset = 0

    var colors: Array<String> = arrayOf()

    @Composable
    override fun DisplayStage(
        activity: VisionTestLayoutActivity,
        modifier: Modifier,
        stages: List<SerializablePair>,
        isResult: Boolean
    ) {

        var difficulty: Int by remember { mutableIntStateOf(1) }
        var resultIterator: Int by remember { mutableIntStateOf(0) }

        var colorArray: ArrayList<String> = ArrayList()
        var answerArray: ArrayList<String> = ArrayList()


        for (i in stages) {
            colorArray.add(if (isResult) i.second else i.first)
            answerArray.add(if (isResult) i.first else i.second)
        }

        var stageColors by remember { mutableStateOf(
            (if (isResult) colorArray[resultIterator].split(' ') else
                prepareArray(
                colorArray,
                difficultyScale[difficulty],
                10,
                colorOffset
            ).toList()))
        }

        var sortedColors by remember { mutableStateOf(
            if (isResult) answerArray[resultIterator].split(' ') else null
        ) }

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

                            var isTopEdge = (index == 0)
                            var isBottomEdge = (index == 9)

                            var edgeShape: Shape = RoundedCornerShape(
                                topStart = (if (isTopEdge) 15.dp else 0.dp),
                                topEnd = (if (isTopEdge) 15.dp else 0.dp),
                                bottomEnd = (if (isBottomEdge) 15.dp else 0.dp),
                                bottomStart = (if (isBottomEdge) 15.dp else 0.dp)
                            )

                            Column(
                                modifier = Modifier
                                    .offset { IntOffset(0, if (currentlyDragged == index) currentOffset.toInt() else 0) }
                                    .fillMaxHeight(0.1f)
                                    .scale(if (index == currentlyDragged) 1.5f else 1f, 1f)
                                    .zIndex((if (index == currentlyDragged) 1f else 0f))
                                    .draggable(
                                        enabled = (!(isTopEdge || isBottomEdge)),
                                        orientation = Orientation.Vertical,
                                        state = rememberDraggableState { delta ->

                                            if (currentlyDragged == null) currentlyDragged = index
                                            currentOffset += delta

                                            val currentIndex =
                                                currentlyDragged ?: return@rememberDraggableState

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
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (isResult) 1f else 0.6f)
                                        .height(60.dp)
                                        .clip(edgeShape)
                                        .background(Color(parseColor(item)))
                                        .then(
                                            if ((isTopEdge) || isBottomEdge) {
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
                                            .scale(if (index == currentlyDragged) 0.66f else 1f, 1f),
                                        text = "${getHue(item)}"
                                    )
                                }
                            }


                        }
                    }
                }

                if (isResult) {
                    var startX: Float = 0f
                    var startY: Float = 0f

                    Column (
                        modifier = Modifier
                            .weight(1f)
                            .onGloballyPositioned { lc ->
                                startX = lc.positionInWindow().x
                                startY = lc.positionInWindow().y
                                println("$startX $startY")
                            }
                    ) {
                        var correctnessMap = getCorrectnessMapping(sortedColors!!, stageColors)

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
                            itemsIndexed(sortedColors!!) { index, item ->

                                var isTopEdge = (index == 0)
                                var isBottomEdge = (index == 9)

                                var edgeShape: Shape = RoundedCornerShape(
                                    topStart = (if (isTopEdge) 15.dp else 0.dp),
                                    topEnd = (if (isTopEdge) 15.dp else 0.dp),
                                    bottomEnd = (if (isBottomEdge) 15.dp else 0.dp),
                                    bottomStart = (if (isBottomEdge) 15.dp else 0.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight(0.1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp)
                                            .clip(edgeShape)
                                            .background(Color(parseColor(item)))
                                            .then(
                                                if ((isTopEdge) || isBottomEdge) {
                                                    Modifier.border(
                                                        width = (2.dp),
                                                        brush = SolidColor(Color.Black),
                                                        shape = edgeShape
                                                    )
                                                } else Modifier
                                            ),

                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${getHue(item)}")
                                    }
                                }
                            }
                        }
                    }

                }

            }

            Box(
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {

                        if (isResult) {

                            resultIterator++
                            stageColors = colorArray[resultIterator].split(' ')
                            sortedColors = answerArray[resultIterator].split(' ')
                            return@Button

                        }

                        println("ohioskibidi")

                        if (difficulty < stageCount) {

                            var answer = stageColors.joinToString(" ")

                            var a = getScore(answer, "RELATIVE")
                            var b = getScore(answer, "LEVENSHTEIN")

                            if ((a + b) / 2 >= 70 && (a + b) / 2 < 100 && difficulty >= 4) {
                                colorOffset += 20
                                println("${(a + b) / 2} Bez zmiany trudnosci")
                            } else {
                                colorOffset = 0
                                difficulty++
                            }

                            storeResult(stageColors.sortedBy { getHue(it) }.joinToString(" "), answer)
                            stageColors = prepareArray(
                                colorArray,
                                difficultyScale[difficulty],
                                10,
                                colorOffset
                            ).toList()

                        } else {
                            storeResult(
                                stageColors.sortedBy { getHue(it) }.joinToString(" "),
                                stageColors.joinToString(" ")
                            )
                            endTest(activity)
                        }
                    }
                ) {
                    Text("Dalej")
                }
            }

        }

    }

    @Composable
    override fun BeginTest(
        activity: VisionTestLayoutActivity,
        modifier: Modifier,
        isResult: Boolean,
        result: TestResult?
    ) {
        colors = activity.resources.getStringArray(R.array.farnsworth_colors)

        if (!isResult) {

            var list: MutableList<SerializablePair> = ArrayList<SerializablePair>(stageCount)

            for (i in colors) {
                list.add(SerializablePair(i, ""))
            }

            DisplayStage(activity, modifier, list, false)

        } else {

            var inputList = ResultDataCollector.deserializeResult(result!!.result)

            var list = ArrayList<SerializablePair>()

            for (el in inputList) {
                list.add(SerializablePair(el.first, el.second))
            }

            DisplayStage(activity, modifier, list, true)

        }

    }

    override fun generateQuestion(): String {
        TODO("Not yet implemented")
    }

    override fun getExampleAnswers(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun checkAnswer(answer: String): Boolean {

        return true

    }

    fun getScore(answer: String, measurementMode: String): Int {

        var answeredArray = answer.split(' ').map { getHue(it) }
        var orderedArray = answeredArray.sorted()
        val size = answeredArray.size

        var percent = 0

        if (measurementMode == "ABSOLUTE") {

            for (i in 0..<size) {
                if (i == orderedArray.indexOf(answeredArray[i])) percent += 10
            }

        } else if (measurementMode == "RELATIVE") {

            for (i in 1..<size) {
                if (answeredArray[i] > answeredArray[i - 1]) percent += 10
            }

            percent += 10

        } else if (measurementMode == "LEVENSHTEIN") {

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

        return percent

    }

    fun getCorrectnessMapping(a: List<String>, b: List<String>): Map<Int, Int> {

        var map: MutableMap<Int, Int> = HashMap()
        var connected = b.joinToString(" ")

        var i = 1
        while (i < a.size - 1) {

            var idx = b.indexOf(a[i])
            var j = 0

            while(idx + j < a.size && connected.indexOf(a.subList(idx, idx+j).joinToString(" ")) != -1) j++

            if (idx != i) map[i] = idx

            i += j
            if (j > 1) i -= 1

        }
//        for (el in map) println("${el.value} ${el.key}")

        return map

    }

    inline fun <reified T> isSubArray(a: List<T>, b: List<T>): Boolean {

        return a.windowed(b.size).any {
            it.toTypedArray().contentEquals(b.toTypedArray())
        }

    }

    fun getHue(color: String): Float {

        var hsv = FloatArray(3)
        var a = Color(parseColor(color)).toArgb()

        colorToHSV(a, hsv)

        return hsv[0]
    }

    fun prepareArray(old: List<String>, n: Int, limit: Int, offset: Int = 0): ArrayList<String> {

        var new = ArrayList<String>()
        var i: Int = offset

        while(new.size < limit) {
            new.add(old[i % old.size])
            i += n
        }

        new = ArrayList(new.subList(0, limit))
        var begin = new.first()
        var end = new.last()

        new = ArrayList(new.subList(1, limit - 1))

        new.shuffle()
        new.add(0, begin)
        new.add(end)

        return new

    }


}