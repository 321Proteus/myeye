package me.proteus.myeye

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.proteus.myeye.ui.ResultBrowserActivity
import me.proteus.myeye.ui.SettingsActivity
import me.proteus.myeye.ui.SimpleDistanceActivity
import me.proteus.myeye.ui.SpeechDecoderActivity
import me.proteus.myeye.ui.VisionTestIcon
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
                            VisionTestDrawerItem("REACTION_TEST", this@MenuActivity)

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

    override fun attachBaseContext(newBase: Context?) {
        val currentLanguage = LanguageUtils.getCurrentLanguage(newBase)
        val newContext = LanguageUtils.setLocale(newBase, currentLanguage)
        super.attachBaseContext(newContext)
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MenuScreen(scope: CoroutineScope, state: DrawerState) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        fontSize = 36.sp,
                        fontFamily = FontFamily(Font(R.font.opticiansans)),
                        text = stringResource(R.string.app_name)
                    )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    fontSize = 12.sp,
                    text = String.format(stringResource(R.string.menu_description), stringResource(R.string.app_name)),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.padding(16.dp))

                ExpandableGrid()


            }
        }
    )
}

@Composable
fun ExpandableGrid() {
    var expanded by remember { mutableStateOf(false) }
    var list = mutableListOf("TEST_BUILD", "TEST_CIRCLE", "SNELLEN_CHART", "TEST_INFO", "COLOR_ARRANGE", "REACTION_TEST")

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Odkryj nasze testy wzroku",
                fontSize = 14.sp,
            )
            Text(
                modifier = Modifier
                    .clickable { expanded = !expanded },
                text = "Zobacz wszystkie",
                color = Color.Blue
            )
        }

        AnimatedContent(targetState = expanded, label = "Grid Transition") { isExpanded ->
            if (isExpanded) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .height((LocalConfiguration.current.screenHeightDp / 2).dp)
                        .padding(8.dp)
                        .background(Color.LightGray),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(list) { id ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.width(80.dp)) {
                                VisionTestIcon(
                                    modifier = Modifier,
                                    testID = id,
                                    size = 0.4f
                                )
                            }
                            Text(VisionTestUtils().getTestNameByID(id), fontSize = 12.sp)
                        }

                    }
                }
            } else {

                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(list) { id ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.width(80.dp)) {
                                VisionTestIcon(
                                    modifier = Modifier,
                                    testID = id,
                                    size = 0.4f
                                )
                            }
                            Text(VisionTestUtils().getTestNameByID(id), fontSize = 12.sp)
                        }

                    }
                }
            }
        }
    }
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

