package me.proteus.myeye

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.proteus.myeye.ui.DistanceMeasurement
import me.proteus.myeye.ui.ResultBrowserScreen
import me.proteus.myeye.ui.TestResultScreen
import me.proteus.myeye.ui.VisionTestScreen
import me.proteus.myeye.ui.theme.MyEyeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            SetupNavigation()

        }
    }

    @Composable
    fun MainScreen(controller: NavController) {
        MyEyeTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        modifier = Modifier.padding(innerPadding),
                        onClick = { controller.navigate("menu") }
                    ) {
                        Text(text = "Uruchom test")
                    }
                }

            }
        }
    }

    @Composable
    fun SetupNavigation() {

        val controller = rememberNavController()

        NavHost(
            navController = controller,
            startDestination = "main"
        ) {
            composable("menu") { MainMenu(controller) }
            composable("main") { MainScreen(controller) }
            composable("browser") { ResultBrowserScreen(controller) }
            composable(
                route = "result/{sessionId}/{isAfter}",
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.IntType },
                    navArgument("isAfter") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: -1
                val isAfter = backStackEntry.arguments?.getBoolean("isResult") ?: false

                TestResultScreen(controller, sessionId, isAfter)
            }
            composable(
                route = "visiontest/{testID}",
                arguments = listOf(
                    navArgument("testID") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val testID = backStackEntry.arguments?.getString("testID") ?: "defaultTest"
                Log.w("Nav", "SetupNavigation: testID $testID")

                val distance = controller.previousBackStackEntry
                    ?.savedStateHandle?.get<Float>("distance") ?: 0f
                val isResult = controller.previousBackStackEntry
                    ?.savedStateHandle?.get<Boolean>("isResult") ?: false
                val sessionId = controller.previousBackStackEntry
                    ?.savedStateHandle?.get<Int>("sessionId")

                println(isResult)
                println(sessionId)

                VisionTestScreen(controller, testID, isResult, sessionId, distance)
            }
            composable(
                route = "distance/{countdown}",
                arguments = listOf(
                    navArgument("countdown") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val countdown = backStackEntry.arguments?.getBoolean("countdown") ?: true
                val testID = controller.previousBackStackEntry
                    ?.savedStateHandle?.get<String>("testID") ?: "null"
                Log.w("Nav", "SetupNavigation: testID $testID")
                DistanceMeasurement(controller, countdown, testID)
            }
        }
    }

}