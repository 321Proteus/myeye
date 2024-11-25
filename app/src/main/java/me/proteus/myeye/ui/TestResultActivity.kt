package me.proteus.myeye.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import me.proteus.myeye.TestResult
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils

class TestResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyEyeTheme {
                TestResultScreen(intent)
            }
        }
    }
}

@Composable
fun TestResultScreen(inputIntent: Intent) {

    val resultData = IntentCompat.getParcelableExtra(inputIntent, "RESULT_PARCEL", TestResult::class.java)
    if (resultData == null) return;

    Scaffold(
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .padding(horizontal = 48.dp, vertical = 32.dp)
                        .weight(0.65f)
                        .aspectRatio(1.0f)
                        .border(width = 2.dp, color = Color.Black),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(VisionTestUtils().getTestByID(resultData.testID).testIcon, null)

                }

                Box(
                    modifier = Modifier
                        .weight(0.35f)
                ) {
                    Text(resultData.testID.toString())
                }

            }

        }
    )
}

