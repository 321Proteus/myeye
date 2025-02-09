package me.proteus.myeye

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.ui.components.ExpandableGrid
import me.proteus.myeye.ui.MapActivity
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.NavBar
import me.proteus.myeye.ui.components.TopBar
import androidx.navigation.NavController
import me.proteus.myeye.ui.components.VisionTestIcon
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils

@Composable
fun MainMenu(controller: NavController) {

    MyEyeTheme {
//        val drawerState = rememberDrawerState(DrawerValue.Closed)
//        val scope = rememberCoroutineScope()
//
//        ModalNavigationDrawer(
//            drawerState = drawerState,
//            drawerContent = {
//                ModalDrawerSheet {
//
//                    VisionTestDrawerItem("TEST_CIRCLE", this@MenuActivity)
//                    VisionTestDrawerItem("TEST_BUILD", this@MenuActivity)
//                    VisionTestDrawerItem("TEST_INFO", this@MenuActivity)
//                    VisionTestDrawerItem("SNELLEN_CHART", this@MenuActivity)
//                    VisionTestDrawerItem("COLOR_ARRANGE", this@MenuActivity)
//                    VisionTestDrawerItem("REACTION_TEST", this@MenuActivity)
//
//                    NavigationDrawerItem(
//                        icon = { Icon(Icons.TwoTone.Check, contentDescription = null) },
//                        label = { Text("Przeglądaj wyniki") },
//                        selected = false,
//                        onClick = {
//                            val intent = Intent(this@MenuActivity, ResultBrowserActivity::class.java)
//                            startActivity(intent)
//                        },
//                    )
//
//                    NavigationDrawerItem(
//                        icon = { Icon(Icons.Outlined.Call, contentDescription = null) },
//                        label = { Text("Vosk Test") },
//                        selected = false,
//                        onClick = {
//                            val intent = Intent(this@MenuActivity, SpeechDecoderActivity::class.java)
//                            startActivity(intent)
//                        },
//                    )
//
//                    NavigationDrawerItem(
//                        icon = { Icon(Icons.TwoTone.LocationOn, contentDescription = null) },
//                        label = { Text("Pomiar dystansu") },
//                        selected = false,
//                        onClick = {
//                            val intent = Intent(this@MenuActivity, SimpleDistanceActivity::class.java)
//                            startActivity(intent)
//                        },
//                    )
//
//                    NavigationDrawerItem(
//                        icon = { Icon(Icons.TwoTone.LocationOn, contentDescription = null) },
//                        label = { Text("Mapa") },
//                        selected = false,
//                        onClick = {
//                            val intent = Intent(this@MenuActivity, MapActivity::class.java)
//                            startActivity(intent)
//                        },
//                    )
//
//                    NavigationDrawerItem(
//                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
//                        label = { Text("Ustawienia") },
//                        selected = false,
//                        onClick = {
//                            val intent = Intent(this@MenuActivity, SettingsActivity::class.java)
//                            startActivity(intent)
//                        }
//                    )
//
//                }
//            }
//        ) {
//            MenuScreen(scope = scope, state = drawerState)
//        }
//    }

        MenuScreen(controller)

    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MenuScreen(controller: NavController) {
    Scaffold(
        topBar = { TopBar() },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    fontSize = 12.sp,
                    text = String.format(
                        stringResource(R.string.menu_description),
                        stringResource(R.string.app_name)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.padding(16.dp))

                ExpandableGrid(height = (LocalConfiguration.current.screenHeightDp / 2).dp, false)

            }
        },
        bottomBar = { BottomBar(activity) }
    )
}

