package me.proteus.myeye.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

    private lateinit var launcher: ActivityResultLauncher<Array<String>>
    private lateinit var testObject: VisionTest

    private var isResult: Boolean? = null
    private var resultData: TestResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        isResult = intent.getBooleanExtra("IS_RESULT", false)
        resultData = IntentCompat.getParcelableExtra(intent, "RESULT_PARCEL", TestResult::class.java)

        println(if (resultData != null) "Znaleziono TestResult o ID ${resultData!!.resultID}" else "Nie przekazano TestResult")

        val testID: String? = if (isResult == true && resultData != null) {
            resultData!!.testID
        } else {
            intent.getStringExtra("TEST_ID")
        }


        val vtu = VisionTestUtils()
        testObject = vtu.getTestByID(testID)

        val permissionList = mutableListOf<String>()

        if (testObject.needsMicrophone) permissionList.add(Manifest.permission.RECORD_AUDIO)
        if (testObject.distance != -1f) permissionList.add(Manifest.permission.CAMERA)

        launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val isMicGranted = permissions[Manifest.permission.RECORD_AUDIO] == true
            val isCamGranted = permissions[Manifest.permission.CAMERA] == true
            println("$isCamGranted $isMicGranted")
            if (isCamGranted && isMicGranted) {
                getDistance()
            } else {
                Toast.makeText(this, "Brak wymaganych uprawnień", Toast.LENGTH_SHORT).show()
            }
        }

        if (permissionList.isNotEmpty() && isResult == false) launcher.launch(permissionList.toTypedArray())
        else showTestScreen()

    }

    private fun showTestScreen() {
        setContent {
            MyEyeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        testObject.BeginTest(
                            activity = this@VisionTestLayoutActivity,
                            isResult = isResult!!,
                            result = resultData
                        )
                    }
                }
            }
        }
    }

    private fun getDistance() {
        if (intent.hasExtra("DISTANCE")) {
            val distance: Float = intent.getFloatExtra("DISTANCE", 2f)
            println("Distance $distance")
            testObject.distance = distance

            showTestScreen()

        } else {
            val distanceIntent = Intent(this@VisionTestLayoutActivity, SimpleDistanceActivity::class.java)
            distanceIntent.putExtra("TEST_ID", testObject.testID)
            startActivity(distanceIntent)
            finish()
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        testObject.endTest(this, true)
    }

}