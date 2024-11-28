package me.proteus.myeye.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.fragment.app.FragmentActivity
import me.proteus.myeye.TestResult
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils

class TestResultActivity : FragmentActivity() {

    private val viewModel: AuthorizationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyEyeTheme {
                TestResultScreen(
                    intent,
                    viewModel,
                    navigate = { data -> openTest(data) },
                    this
                )
            }
        }
    }

    fun openTest(data: TestResult?) {

        val detailsIntent: Intent = Intent(this, VisionTestLayoutActivity::class.java)
        detailsIntent.putExtra("IS_RESULT", true)
        detailsIntent.putExtra("RESULT_PARCEL", data)
        this.startActivity(detailsIntent)

    }

}

@Composable
fun TestResultScreen(
    inputIntent: Intent,
    viewModel: AuthorizationViewModel,
    navigate: (TestResult?) -> Unit,
    activity: TestResultActivity
    ) {

    val resultData = IntentCompat.getParcelableExtra(inputIntent, "RESULT_PARCEL", TestResult::class.java)
    if (resultData == null) return;

    val isAfterTest = inputIntent.getBooleanExtra("IS_AFTER", false)

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

                    Box(
                        modifier = Modifier
                            .padding(start = 100.dp, end = 100.dp)
                            .weight(0.65f)
                            .aspectRatio(1.0f)
                            .clip(shape = RoundedCornerShape(15.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            modifier = Modifier.fillMaxSize(0.4f),
                            imageVector = VisionTestUtils().getTestByID(resultData.testID).testIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )

                    }
                }

                Column(
                    modifier = Modifier.weight(0.8f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box() {
                        Text("Data wykonania: " + resultData.formattedTimestamp)
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

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            modifier = Modifier,
                            onClick = {

                                if (viewModel.isAuthorized) {
                                    navigate(resultData)
                                } else {
                                    authenticateUser(activity,
                                        onSuccess = {
                                            viewModel.authenticate()
                                            activity.openTest(resultData)
                                        },
                                        onFailure = { println("Autoryzacja nieudana") }
                                    )
                                }

                            }
                        ) {
                            Text(text = "Zobacz wyniki", fontSize = 20.sp)
                        }
                    }
                }


            }

        }
    )
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
