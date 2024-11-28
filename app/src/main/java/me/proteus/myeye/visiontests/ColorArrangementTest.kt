package me.proteus.myeye.visiontests

import android.graphics.Color.parseColor
import android.graphics.Color.colorToHSV
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import me.proteus.myeye.R
import me.proteus.myeye.SerializablePair
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.ui.VisionTestLayoutActivity
import kotlin.math.max

class ColorArrangementTest : VisionTest {
    override val testID: String = "COLOR_ARRANGE"
    override val testIcon: ImageVector = Icons.AutoMirrored.Outlined.List
    override val stageCount: Int = 8
    override val resultCollector: ResultDataCollector = ResultDataCollector()

    var colors: Array<String> = arrayOf()

    @Composable
    override fun DisplayStage(
        activity: VisionTestLayoutActivity,
        modifier: Modifier,
        stages: List<SerializablePair>,
        isResult: Boolean
    ) {

        var stageIterator: Int by remember { mutableIntStateOf(1) }

        var shuffledStageColors by remember { mutableStateOf(stages[stageIterator-1].second.split(" ")) }
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
                 itemsIndexed(shuffledStageColors) { index, item ->

                    Column(
                        modifier = Modifier
                            .offset { IntOffset(0, if (currentlyDragged == index) currentOffset.toInt() else 0) }//IntOffset(0, if (currentlyDragged == index) currentOffset else 0) }
                            .draggable(
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
                                        if (target in shuffledStageColors.indices) {
                                            shuffledStageColors = shuffledStageColors.toMutableList().apply {
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
                                .background(Color(parseColor(item))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${getHue(item)}")
                        }
                    }

                }
            }

            Box(
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        if (stageIterator < stageCount) stageIterator++
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

            for (i in 0..<stageCount) {

                var original = colors.copyOfRange(i*10, i*10 + 10).toList()
                var shuffled = original.shuffled()

                list.add(SerializablePair(original.joinToString(" "), shuffled.joinToString(" ")))

            }

            DisplayStage(activity, modifier, list, false)

        } else {

            var list = ResultDataCollector.deserializeResult(result!!.result)

        }

    }

    override fun generateQuestion(): Any {
        TODO("Not yet implemented")
    }

    override fun getExampleAnswers(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun checkAnswer(answer: String): Boolean {
        TODO("Not yet implemented")
    }

    fun getHue(color: String): Float {

        var hsv = FloatArray(3)
        var a = Color(parseColor(color)).toArgb()

        colorToHSV(a, hsv)

        return hsv[0]
    }


}