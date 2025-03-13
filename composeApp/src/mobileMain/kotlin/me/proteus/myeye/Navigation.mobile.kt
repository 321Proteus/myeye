package me.proteus.myeye

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import me.proteus.myeye.ui.components.TestSelector
import me.proteus.myeye.ui.screens.ArticleBrowserScreen
import me.proteus.myeye.ui.screens.ArticleScreen
import me.proteus.myeye.ui.screens.MainMenu
import me.proteus.myeye.ui.screens.MapScreen
import me.proteus.myeye.ui.screens.PlaceDetailsScreen
import me.proteus.myeye.ui.screens.ResultBrowserScreen
import me.proteus.myeye.ui.screens.SimpleDistanceScreen
import me.proteus.myeye.ui.screens.TestResultScreen
import me.proteus.myeye.ui.screens.VisionTestScreen

object NavControllerHolder {
    var navController: NavController? = null

    fun navigate(route: String) {
        navController?.navigate(route)
    }
}

actual fun getCurrentRoute(): String {
    return NavControllerHolder.navController!!.currentDestination!!.route!!
}

actual fun navigate(route: String) {
    NavControllerHolder.navController!!.navigate(route)
}

actual fun goBack(route: String, inclusive: Boolean) {
    NavControllerHolder.navController!!.popBackStack(route, inclusive)
}

@Composable
actual fun SetupNavigation() {

    NavControllerHolder.navController = rememberNavController()

    NavHost(
        navController = NavControllerHolder.navController as NavHostController,
        startDestination = "menu"
    ) {
        composable("menu") { MainMenu() }
        composable("browser") { ResultBrowserScreen() }
        composable(
            route = "result/{sessionId}/{isAfter}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.IntType },
                navArgument("isAfter") { type = NavType.BoolType }
            )
        ) {
            it.arguments?.read {
                val sessionId = getInt("sessionId")
                val isAfter = getBoolean("isAfter")
                TestResultScreen(sessionId, isAfter)
            }
        }
        composable(
            route = "visiontest/{testID}/{isResult}/{sessionId}/{distance}",
            arguments = listOf(
                navArgument("testID") { type = NavType.StringType },
                navArgument("isResult") { type = NavType.BoolType },
                navArgument("sessionId") { type = NavType.IntType },
                navArgument("distance") { type = NavType.FloatType }
            )
        ) {
            it.arguments?.read {

                val testID = getString("testID")
                val distance = getFloat("distance")
                val isResult = getBoolean("isResult")
                val sessionId = getInt("sessionId")

                println("isResult: $isResult")
                println("sessionId: $sessionId")

                VisionTestScreen(testID, isResult, sessionId, distance)
            }

        }
        composable(
            route = "distance/{countdown}/{testID}",
            arguments = listOf(
                navArgument("countdown") { type = NavType.BoolType }
            )
        ) {
            it.arguments?.read {
                val countdown = getBoolean("countdown")
                val testID = getString("testID")
                SimpleDistanceScreen(countdown, testID)
            }
        }

        composable("map") { MapScreen() }
        composable(
            "place/{placeID}",
            listOf(
                navArgument("placeID") { type = NavType.StringType }
            )
        ) {
            it.arguments?.read {
                val placeID = getString("placeID")
                PlaceDetailsScreen(placeID)
            }
        }
        composable("test_selector") { TestSelector() }
        composable("tools") {} //{ SettingsScreen(controller) }
        composable("article_browser") { ArticleBrowserScreen() }
        composable(
            "article/{id}",
            listOf(navArgument("id") { type = NavType.IntType })
        ) {
            it.arguments?.read {
                ArticleScreen(getInt("id"))
            }
        }

    }
}