package me.proteus.myeye

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.proteus.myeye.ui.ResultBrowserActivity
import me.proteus.myeye.ui.SettingsActivity
import me.proteus.myeye.ui.SimpleDistanceActivity
import me.proteus.myeye.ui.SpeechDecoderActivity
import me.proteus.myeye.ui.VisionTestLayoutActivity
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyEyeTheme {
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {

                            VisionTestDrawerItem("TEST_CIRCLE", this@MenuActivity)
                            VisionTestDrawerItem("TEST_BUILD", this@MenuActivity)
                            VisionTestDrawerItem("TEST_INFO", this@MenuActivity)
                            VisionTestDrawerItem("SNELLEN_CHART", this@MenuActivity)
                            VisionTestDrawerItem("COLOR_ARRANGE", this@MenuActivity)

                            NavigationDrawerItem(
                                icon = { Icon(Icons.TwoTone.Check, contentDescription = null) },
                                label = { Text("Przeglądaj wyniki") },
                                selected = false,
                                onClick = {
                                    val intent = Intent(this@MenuActivity, ResultBrowserActivity::class.java)
                                    startActivity(intent)
                                },
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Outlined.Call, contentDescription = null) },
                                label = { Text("Vosk Test") },
                                selected = false,
                                onClick = {
                                    val intent = Intent(this@MenuActivity, SpeechDecoderActivity::class.java)
                                    startActivity(intent)
                                },
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.TwoTone.LocationOn, contentDescription = null) },
                                label = { Text("Pomiar dystansu") },
                                selected = false,
                                onClick = {
                                    val intent = Intent(this@MenuActivity, SimpleDistanceActivity::class.java)
                                    startActivity(intent)
                                },
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                                label = { Text("Ustawienia") },
                                selected = false,
                                onClick = {
                                    val intent = Intent(this@MenuActivity, SettingsActivity::class.java)
                                    startActivity(intent)
                                }
                            )

                        }
                    }
                ) {
                    MenuScreen(scope = scope, state = drawerState)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MenuScreen(scope: CoroutineScope, state: DrawerState) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("MyEye", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { state.open() }}) {
                        Icon(Icons.Filled.Menu, null)
                    }
                },
                actions = {

                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Favorite, null)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Create, null)
                    }

                }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Menu MyEye")
            }
        }
    )
}

@Composable
fun VisionTestDrawerItem(testID: String, activity: MenuActivity) {

    var icon = VisionTestUtils().getTestByID(testID).testIcon

    println()

    var description = VisionTestUtils().getTestTypeByID(testID) + " " + VisionTestUtils().getTestNameByID(testID)

    NavigationDrawerItem(
        icon = { Icon(icon, null) },
        label = { Text(description) },
        selected = false,
        onClick = {
            val intent = Intent(activity, VisionTestLayoutActivity::class.java)
            intent.putExtra("TEST_ID", testID)
            activity.startActivity(intent)
        },
    )

}

