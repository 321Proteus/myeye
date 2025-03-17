package me.proteus.myeye.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.getPlatform
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.navigate
import me.proteus.myeye.ui.components.VisionTestIcon

@Composable
fun VisionTestConfigScreen(
    testID: String,
    measured: Float? = null
) {
    MyEyeTheme {

        Scaffold(
            content = { innerPadding ->

                val vtu = VisionTestUtils()
                val test = vtu.getTestByID(testID)
                var distance by remember {
                    mutableStateOf(measured?.toString() ?: "0")
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxHeight(0.4f)
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = vtu.getFullTestName(testID), fontSize = 24.sp)

                        VisionTestIcon(
                            modifier = Modifier
                                .padding(start = 120.dp, end = 120.dp)
                                .weight(0.65f),
                            testID = testID,
                            size = 0.4f
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.8f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box {
                            Text(vtu.getTestDescriptionByID(testID))
                        }

                        if (test.distance != -1f) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box {
                                    TextField(
                                        value = distance,
                                        onValueChange = { newValue ->
                                            if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                                                distance = newValue
                                            }
                                        },
                                        label = { Text("Podaj odległość") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true
                                    )
                                }
                                if (getPlatform().type != "WEB") {
                                    Button(onClick = {
                                        navigate("distance/true/$testID")
                                    }) {
                                        Text("Zmierz odległość")
                                    }
                                }
                            }

                        }
                        Box(
                            contentAlignment = Alignment.Center
                        ) {


                            Button(
                                modifier = Modifier,
                                onClick = {
                                    navigate("visiontest/$testID/1/0/$distance")
                                }
                            ) {
                                Text(text = "Rozpocznij test", fontSize = 20.sp)
                            }
                        }

                    }

                }

            }
        )
    }
}

@Composable
fun VisionTestScreen(
    testID: String,
    navMode: Int,
    sessionID: Int?,
    distance: Float
) {

    val vtu = remember { VisionTestUtils() }
    val conn = ResultDataSaver.getConnection()

    val resultData = remember {
        if (navMode == 1) null
        else ResultDataSaver.select(conn, sessionID!!)[0]
    }

    val testObject = remember {
        if (navMode == 1) vtu.getTestByID(testID)
        else vtu.getTestByID(resultData!!.testID)
    }

    println("dystans: $distance")

    // TODO: multiplatform camera/mic permissions

    if (testObject.distance != -1f) {
        testObject.distance = distance
    }
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
                    isResult = (navMode == 2),
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