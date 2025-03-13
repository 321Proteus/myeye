package me.proteus.myeye.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils
import me.proteus.myeye.io.ResultDataSaver

@Composable
fun VisionTestScreen(
    testID: String,
    isResult: Boolean,
    sessionID: Int?,
    distance: Float
) {

    val vtu = remember { VisionTestUtils() }
    val conn = ResultDataSaver.getConnection()

    val resultData = remember {
        if (!isResult) null
        else ResultDataSaver.select(conn, sessionID!!)[0]
    }

    val testObject = remember {
        if (!isResult) vtu.getTestByID(testID)
        else vtu.getTestByID(resultData!!.testID)
    }
    // TODO: multiplatform camera/mic permissions
//    val permissionList = mutableListOf<String>()
//    if (testObject.needsMicrophone) permissionList.add(Manifest.permission.RECORD_AUDIO)
//    if (testObject.distance != -1f) permissionList.add(Manifest.permission.CAMERA)
//
//    var isGranted by remember { mutableStateOf(false) }
//    var isDistance by remember { mutableStateOf(false) }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestMultiplePermissions()
//    ) { results -> isGranted = results.all { it.value }}

//    LaunchedEffect(Unit) {
//        if (permissionList.isNotEmpty() && !isResult) {
//            launcher.launch(permissionList.toTypedArray())
//        }
//        if (testObject.distance != -1f && !isResult) {
//            if (distance != 0f) {
//                println("Distance $distance")
//                testObject.distance = distance
//                isDistance = true
//            } else {
//                controller.navigate("distance/true/$testID") {
//                    popUpTo("menu")
//                }
//            }
//        } else {
//            isDistance = true
//            isGranted = true
//        }
//
//    }

    MyEyeTheme {
        Scaffold { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                testObject.BeginTest(
                    isResult = isResult,
                    result = resultData
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            conn.close()
            testObject.endTest(true)
        }
    }

}

//    override fun onDestroy() {
//        super.onDestroy()
//        testObject.endTest(this, true)
//    }