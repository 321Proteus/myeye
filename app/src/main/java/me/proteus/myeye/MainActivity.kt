package me.proteus.myeye

import android.content.Context
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
import me.proteus.myeye.ui.screens.DistanceMeasurement
import me.proteus.myeye.ui.screens.MapScreen
import me.proteus.myeye.ui.screens.PlaceDetailsScreen
import me.proteus.myeye.ui.screens.ResultBrowserScreen
import me.proteus.myeye.ui.screens.SettingsScreen
import me.proteus.myeye.ui.screens.TestResultScreen
import me.proteus.myeye.ui.screens.VisionTestScreen
import me.proteus.myeye.ui.components.TestSelector
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.util.LanguageUtils

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
            Scaffold { innerPadding ->

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
            startDestination = "menu"
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
                val isAfter = backStackEntry.arguments?.getBoolean("isAfter") == true
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
                    ?.savedStateHandle?.get<Boolean>("isResult") == true
                val sessionId = controller.previousBackStackEntry
                    ?.savedStateHandle?.get<Int>("sessionId")

                println("isResult: $isResult")

                VisionTestScreen(controller, testID, isResult, sessionId, distance)
            }
            composable(
                route = "distance/{countdown}/{testID}",
                arguments = listOf(
                    navArgument("countdown") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val countdown = backStackEntry.arguments?.getBoolean("countdown") != false
                val testID = backStackEntry.arguments?.getString("testID") ?: "error"
                DistanceMeasurement(controller, countdown, testID)
            }
            composable("map") { MapScreen(controller) }
            composable(
                "place/{placeID}",
                listOf(
                    navArgument("placeID") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val placeID = backStackEntry.arguments?.getString("placeID")
                PlaceDetailsScreen(controller, placeID!!)
            }
            composable("test_selector") {
                TestSelector(controller)
            }
            composable("tools") { SettingsScreen(controller) }

        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val currentLanguage = LanguageUtils.getCurrentLanguage(newBase)
        val newContext = LanguageUtils.setLocale(newBase, currentLanguage)
        super.attachBaseContext(newContext)
    }

}