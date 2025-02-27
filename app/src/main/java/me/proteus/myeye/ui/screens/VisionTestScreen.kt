package me.proteus.myeye.ui.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastRoundToInt
import androidx.navigation.NavController
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.components.TopBar
import me.proteus.myeye.ui.components.VisionTestIcon

@Composable
fun VisionTestDescription(controller: NavController, testID: String) {

    val vtu = VisionTestUtils()
    val test = remember { vtu.getTestByID(testID) }
    var expanded = remember { mutableStateListOf<Boolean>(false, false, false) }

    MyEyeTheme {
        Scaffold(
            topBar = { TopBar() },
            content = { innerPadding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(text = getLocalizedFullTestName(testID), fontSize = 24.sp)

                        VisionTestIcon(
                            modifier = Modifier
                                .padding(start = 100.dp, end = 100.dp)
                                .weight(0.65f),
                            testID = testID,
                            size = 0.4f
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.8f),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = {
                                controller.navigate("visiontest/${testID}")
                            }) {
                                Text("Start", fontSize = 20.sp)
                            }
                        }

                    }

                }

            }
        )
    }

}

@Composable
fun getLocalizedFullTestName(testID: String): String {

    val vtu = VisionTestUtils()
    val lang = LocalConfiguration.current.locales[0].displayName
    if (lang == "pl") {
        return vtu.getTestTypeByID(testID) + " " + vtu.getTestNameByID(testID)
    } else {
        return vtu.getTestNameByID(testID) + " " + vtu.getTestTypeByID(testID)
    }
}

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
        if (testObject.distance != -1f && !isResult) {
            if (distance != 0f) {
                println("Distance $distance")
                testObject.distance = distance
                isDistance = true
            } else {
                controller.navigate("distance/true/$testID") {
                    popUpTo("menu")
                }
            }
        } else {
            isDistance = true
            isGranted = true
        }

    }

    MyEyeTheme {
        Scaffold { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Log.e("VTLA", "$isDistance $isGranted")
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
        Log.e("VTLA","Disposable started")
        onDispose {
            Log.e("VTLA", "Disposed view")
            testObject.endTest(controller, true)
        }
    }

}

//    override fun onDestroy() {
//        super.onDestroy()
//        testObject.endTest(this, true)
//    }