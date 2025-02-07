package me.proteus.myeye.ui

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils
import me.proteus.myeye.io.ResultDataSaver

@Composable
fun VisionTestScreen(
    controller: NavController,
    testID: String,
    isResult: Boolean,
    sessionID: Int?,
    distance: Float
) {

    val context = LocalContext.current
    val vtu = remember { VisionTestUtils() }

    val resultData = remember {
        if (!isResult) null
        else ResultDataSaver(context).getResult(sessionID!!)
    }

    val testObject = remember {
        if (!isResult) vtu.getTestByID(testID)
        else vtu.getTestByID(resultData!!.testID)
    }

    val permissionList = mutableListOf<String>()
    if (testObject.needsMicrophone) permissionList.add(Manifest.permission.RECORD_AUDIO)
    if (testObject.distance != -1f) permissionList.add(Manifest.permission.CAMERA)

    var isGranted by remember { mutableStateOf(false) }
    var isDistance by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results -> isGranted = results.all { it.value }}

    LaunchedEffect(Unit) {
        if (permissionList.isNotEmpty() && !isResult) {
            launcher.launch(permissionList.toTypedArray())
        }
        if (testObject.distance != -1f) {
            if (distance != 0f) {
                println("Distance $distance")
                testObject.distance = distance
                isDistance = true
            } else {
                controller.currentBackStackEntry?.savedStateHandle?.set("testID", testID)
                controller.navigate("distance/true")
            }
        } else {
            isDistance = true
        }

    }

    MyEyeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isGranted && isDistance) {
                    testObject.BeginTest(
                        controller = controller,
                        isResult = isResult,
                        result = resultData
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        Log.e("VTLA","Disposable started", )
        onDispose {
            Log.e("VTLA", "Disposed view", )
            testObject.endTest(controller, true)
        }
    }

}

//    override fun onDestroy() {
//        super.onDestroy()
//        testObject.endTest(this, true)
//    }