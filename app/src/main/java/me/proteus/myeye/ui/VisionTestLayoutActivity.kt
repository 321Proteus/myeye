package me.proteus.myeye.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.IntentCompat
import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils
import me.proteus.myeye.TestResult

class VisionTestLayoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            MyEyeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        var isResult: Boolean = intent.getBooleanExtra("IS_RESULT", false)

                        var resultData: TestResult? = IntentCompat.getParcelableExtra(intent, "RESULT_PARCEL", TestResult::class.java)

                        println(if (resultData != null) "Znaleziono TestResult o ID ${resultData.resultID}" else "Nie przekazano TestResult")

                        getTest(intent).BeginTest(activity = this@VisionTestLayoutActivity, modifier = Modifier, isResult = isResult, result = resultData)

                    }

                }
            }

        }
    }
}

fun getTest(intent: Intent): VisionTest {
    val testID = intent.getStringExtra("TEST_ID")

    val test: VisionTest = VisionTestUtils().getTestByID(testID)

    return test
}