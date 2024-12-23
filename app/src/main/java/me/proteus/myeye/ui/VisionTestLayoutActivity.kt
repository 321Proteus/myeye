package me.proteus.myeye.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    private lateinit var rpl: ActivityResultLauncher<String>
    private lateinit var testObject: VisionTest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        rpl =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    println("Uprawnienia przyznane")
                } else {
                    println("Brak uprawnień")
                }
            }


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

                        var testID: String? = if (isResult && resultData != null) {
                            resultData.testID
                        } else {
                            intent.getStringExtra("TEST_ID")
                        }

                        testObject = VisionTestUtils().getTestByID(testID)

                        testObject.BeginTest(
                            activity = this@VisionTestLayoutActivity,
                            isResult = isResult,
                            result = resultData,
                            rpl = rpl
                        )
                    }

                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        testObject.endTest(this, true)
    }

}