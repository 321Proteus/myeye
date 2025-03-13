package me.proteus.myeye.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HTTPDownloaderDialog(
    text: String,
    percent: Float,
    onDismiss: () -> Unit
) {

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text)
                    Row {
                        AnimatedEyes(percent)
                    }
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        progress = { percent }
                    )
                    Text("${percent}%")

                }
            }
        }
    }
}

@Composable
fun AnimatedEyes(percent: Float) {

    val eyelidTranslation = remember { Animatable(0f) }
    val shakeTranslation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {

            eyelidTranslation.animateTo(
                targetValue = 90f,
                animationSpec = tween(5000, easing = FastOutLinearInEasing),
            )

            eyelidTranslation.snapTo(0f)
            shake(shakeTranslation)
            blink(eyelidTranslation)

        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
    ) {

        val centerX = size.width / 2
        val centerY = size.height / 2
        val eyeWidth = size.width / 5
        val eyeHeight = size.width / 4.5f

        val lcx = centerX - eyeWidth / 2 - 25 + shakeTranslation.value
        val rcx = centerX + eyeWidth / 2 + 25 + shakeTranslation.value

        drawEye(lcx, centerY, eyeWidth, eyeHeight, percent, eyelidTranslation.value)
        drawEye(rcx, centerY, eyeWidth, eyeHeight, percent, eyelidTranslation.value)

    }

}

fun DrawScope.drawEye(
    cx: Float,
    cy: Float,
    width: Float,
    height: Float,
    direction: Float,
    sleepModifier: Float
) {

    val irisRadius = width / 5

    val eyePath = Path().apply {

        moveTo(cx - width / 2, cy)
        quadraticTo(cx, (cy - height / 2) + sleepModifier, cx + width / 2, cy)
        moveTo(cx + width / 2, cy)
        quadraticTo(cx, cy + height / 2, cx - width / 2, cy)
    }

    drawContext.canvas.withSave {
        clipPath(path = eyePath, clipOp = ClipOp.Intersect) {
            val irisCenterX = cx + (direction - 0.5f) * (width - 3 * irisRadius)
            val irisCenterY = cy + abs((direction - 0.5f)) + 10

            drawCircle(
                radius = irisRadius,
                center = Offset(irisCenterX, irisCenterY),
                style = Stroke(width = 4f),
                color = Color.Black
            )

            drawCircle(
                radius = irisRadius / 3,
                center = Offset(irisCenterX, irisCenterY),
                color = Color.Black
            )
        }

    }

    drawPath(
        path = eyePath,
        color = Color.Black,
        style = Stroke(width = 4f)
    )

}

suspend fun shake(animatable: Animatable<Float, AnimationVector1D>) {
    animatable.animateTo(50f,  tween(20, easing = LinearEasing))
    animatable.animateTo(-50f, tween(20, easing = LinearEasing))
    animatable.animateTo(50f,  tween(20, easing = LinearEasing))
    animatable.animateTo(-50f, tween(20, easing = LinearEasing))
    animatable.animateTo(0f,   tween(20, easing = LinearEasing))
}

suspend fun blink(animatable: Animatable<Float, AnimationVector1D>) {
    animatable.snapTo(90f)
    delay(35)
    animatable.snapTo(0f)
    delay(35)
    animatable.snapTo(90f)
    delay(35)
    animatable.snapTo(0f)
}
