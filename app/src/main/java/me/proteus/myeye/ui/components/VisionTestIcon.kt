package me.proteus.myeye.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import me.proteus.myeye.ui.VisionTestLayoutActivity
import me.proteus.myeye.visiontests.VisionTestUtils

@Composable
fun VisionTestIcon(
    modifier: Modifier,
    testID: String,
    size: Float = 0f,
    clickable: Boolean = false,
    context: Context? = null
) {
    Box(
        modifier = modifier
            .aspectRatio(1.0f)
            .clip(shape = RoundedCornerShape(15.dp))
            .clickable(enabled = clickable) {
                val activity = context!! as Activity
                val intent = Intent(activity, VisionTestLayoutActivity::class.java)
                intent.putExtra("TEST_ID", testID)
                activity.startActivity(intent)
            }
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            modifier = if (size != 0f) Modifier.fillMaxSize(size) else Modifier,
            imageVector = VisionTestUtils().getTestByID(testID).testIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )

    }
}