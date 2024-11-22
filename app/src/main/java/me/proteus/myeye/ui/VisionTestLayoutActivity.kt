package me.proteus.myeye.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.*
import java.lang.IllegalArgumentException

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
                        getTest(intent, applicationContext).DisplayStage(activity = this@VisionTestLayoutActivity, modifier = Modifier)
                    }

                }
            }

        }
    }
}

fun getTest(intent: Intent, context: Context): VisionTest {
    val testID = intent.getStringExtra("TEST_ID")

    var saver: ResultDataSaver = ResultDataSaver(testID, context)

    val test = when (testID) {
        "SNELLEN_CHART" -> SnellenChart()
        "TEST_CIRCLE" -> CircleTest()
        "TEST_BUILD" -> BuildTest()
        "TEST_INFO" -> ExampleTest()
        else -> throw IllegalArgumentException("Nie znaleziono testu o podanym ID")
    }

    return test
}