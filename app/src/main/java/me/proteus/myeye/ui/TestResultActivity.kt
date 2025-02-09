package me.proteus.myeye.ui

import android.annotation.SuppressLint
import androidx.biometric.BiometricPrompt
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastRoundToInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.proteus.myeye.TestResult
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.components.VisionTestIcon
import me.proteus.myeye.ui.theme.MyEyeTheme

@Composable
fun TestResultScreen(
    controller: NavController,
    sessionID: Int,
    isAfterTest: Boolean
) {

    val viewModel: AuthorizationViewModel = viewModel()

    MyEyeTheme {

        val resultData = ResultDataSaver(controller.context)
            .getResult(sessionID)

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

                        Text(text = resultData.fullTestName, fontSize = 24.sp)

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
                            Text("Data wykonania: " + resultData.formattedTimestamp)
                        }

                        if (resultData.distance != -1f) {
                            Box {
                                Text("Odległość: " + resultData.distance.fastRoundToInt() + " cm")
                            }
                        }

                        if (isAfterTest) {
                            Text(
                                modifier = Modifier
                                    .padding(top = 32.dp, bottom = 16.dp),
                                text = "Dziękujemy za wykonanie testu!",
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
                                Button(
                                    modifier = Modifier,
                                    onClick = {

                                        val debugAuth = false

                                        if (debugAuth) {
                                            if (viewModel.isAuthorized) {
                                                openTest(controller, resultData)
                                            } else {
//                                        authenticateUser(activity,
//                                            onSuccess = {
//                                                viewModel.authenticate()
//                                                openTest(controller, resultData)
//                                            },
//                                            onFailure = { println("Autoryzacja nieudana") }
//                                        )
                                            }
                                        } else {
                                            openTest(controller, resultData)
                                        }

                                    }
                                ) {
                                    Text(text = "Zobacz wyniki", fontSize = 20.sp)
                                }
                            }
                            Button(onClick = {
                                controller.popBackStack("menu", false)
                            }) {
                                Text("Wyjdź", fontSize = 20.sp)
                            }
                        }

                    }

                }

            }
        )
    }

}

@SuppressLint("RestrictedApi")
fun openTest(controller: NavController, test: TestResult) {

    for (el in controller.currentBackStack.value) println(el)

    controller.currentBackStackEntry?.savedStateHandle?.apply {
        set("isResult", true)
        set("sessionId", test.resultID)
    }
    controller.navigate("visiontest/${test.testID}")

}

fun authenticateUser(activity: FragmentActivity, onSuccess: () -> Unit, onFailure: () -> Unit) {

    val executor = ContextCompat.getMainExecutor(activity)

    val prompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                onFailure()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Potwierdź swoją tożsamość")
        .setDescription("Użyj odcisku palca lub hasła, aby wyświetlić wyniki testu")
        .setNegativeButtonText("Anuluj")
        .build()

    prompt.authenticate(promptInfo)

}
