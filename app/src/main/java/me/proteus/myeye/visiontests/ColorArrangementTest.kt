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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
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

        var colorArray: ArrayList<String> = ArrayList()
        for (i in stages) colorArray.add(i.first)

        var stageColors by remember { mutableStateOf(prepareArray(
            colorArray,
            difficultyScale[difficulty],
            10,
            colorOffset
        ).toList()) }

        var currentlyDragged by remember { mutableStateOf<Int?>(null) }
        var currentOffset by remember { mutableFloatStateOf(0f) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.9f),
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
                            .scale(scaleX = (if (index == currentlyDragged) 1.5f else 1f), scaleY = 1f)
                            .zIndex((if (index == currentlyDragged) 1f else 0f))
                            .draggable(
                                enabled = (!(isTopEdge || isBottomEdge)),
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
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
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
                                    .scale(scaleX = (if (index == currentlyDragged) 0.66f else 1f), scaleY = 1f),
                                text = "${getHue(item)}"
                            )
                        }
                    }

                }
            }

            Box(
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        if (difficulty < stageCount) {

                            var answer = stageColors.joinToString(" ")

                            var a = getScore(answer, "RELATIVE")
                            var b = getScore(answer, "LEVENSHTEIN")

                            if ((a+b) / 2 >= 70 && (a+b)/ 2 < 100 && difficulty >= 4) {
                                colorOffset += 20
                                println("${(a+b) / 2}Bez zmiany trudnosci")
                            }
                            else {
                                colorOffset = 0
                                difficulty++
                            }

                            stageColors = prepareArray(colorArray, difficultyScale[difficulty], 10, colorOffset).toList()

                            println(difficulty)

                        }
                        else println("I po tescie")
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

           // var list = ResultDataCollector.deserializeResult(result!!.result)

        }

    }

    override fun generateQuestion(): Any {
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

        println(answeredArray)

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