package me.proteus.myeye.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastRoundToInt
import me.proteus.myeye.TestResult
import me.proteus.myeye.goBack
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.navigate
import me.proteus.myeye.ui.components.VisionTestIcon
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.*
import me.proteus.myeye.util.biometryViewModelProvider
import me.proteus.myeye.visiontests.VisionTestUtils
import org.jetbrains.compose.resources.stringResource

@Composable
fun TestResultScreen(
    sessionID: Int,
    isAfterTest: Boolean
) {

    println("SessionID $sessionID")

    val title = stringResource(Res.string.biometry_title)
    val desc = stringResource(Res.string.biometry_reason)
    val fail = stringResource(Res.string.biometry_fail)

    val authModel = biometryViewModelProvider()

    MyEyeTheme {

        val conn = ResultDataSaver.getConnection()

        DisposableEffect(Unit) {
            onDispose { conn.close() }
        }

        val resultData = ResultDataSaver.select(conn, sessionID)[0]

        println("Result : ${resultData.resultID}")

        Scaffold(
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
                        val vtu = VisionTestUtils()
                        Text(text = vtu.getFullTestName(resultData.testID), fontSize = 24.sp)

                        VisionTestIcon(
                            modifier = Modifier
                                .padding(start = 100.dp, end = 100.dp)
                                .weight(0.65f),
                            testID = resultData.testID,
                            size = 0.4f
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.8f),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box {
                            Text(stringResource(Res.string.result_date) + ": " + resultData.formattedTimestamp)
                        }

                        if (resultData.distance != -1f) {
                            Box {
                                Text(stringResource(Res.string.result_distance) + ": " + resultData.distance.fastRoundToInt() + " cm")
                            }
                        }

                        if (isAfterTest) {
                            Text(
                                modifier = Modifier
                                    .padding(top = 32.dp, bottom = 16.dp),
                                text = stringResource(Res.string.result_thankyou),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {

                                val isAuthorized = authModel.isAuthorized.collectAsState()

                                Button(
                                    modifier = Modifier,
                                    onClick = {
                                        val debugAuth = true

                                        if (!isAfterTest && debugAuth && !isAuthorized.value) {
                                            authModel.tryToAuth(title, desc, fail) {
                                                openTest(resultData)
                                            }
                                        } else {
                                            openTest(resultData)
                                        }

                                    }
                                ) {
                                    Text(text = stringResource(Res.string.result_navigate), fontSize = 20.sp)
                                }
                            }
                            Button(onClick = {
                                goBack("menu", false)
                            }) {
                                Text(stringResource(Res.string.exit), fontSize = 20.sp)
                            }
                        }

                    }

                }

            }
        )
    }

}

fun openTest(test: TestResult) {

    navigate("visiontest/${test.testID}/2/${test.resultID}/${test.distance}")

}