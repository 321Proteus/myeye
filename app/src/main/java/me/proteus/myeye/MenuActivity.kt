package me.proteus.myeye

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.proteus.myeye.ui.TestResultActivity
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

                            VisionTestDrawerItem("TEST_CIRCLE", { Icon(Icons.Outlined.AccountCircle, null) }, this@MenuActivity)
                            VisionTestDrawerItem("TEST_BUILD", { Icon(Icons.Outlined.Build, null) }, this@MenuActivity)
                            VisionTestDrawerItem("TEST_INFO", { Icon(Icons.Outlined.Info, null) }, this@MenuActivity)
                            VisionTestDrawerItem("SNELLEN_CHART", { Icon(Icons.TwoTone.Face, null) }, this@MenuActivity)

                            NavigationDrawerItem(
                                icon = { Icon(Icons.TwoTone.Check, contentDescription = null) },
                                label = { Text("Przeglądaj wyniki") },
                                selected = false,
                                onClick = {
                                    val intent = Intent(this@MenuActivity, ResultBrowserActivity::class.java)
                                    startActivity(intent)
                                },
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
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var fullText = "Example MyEye description for code showcase"
                items(count = fullText.length) {
                    Text(
                        text = fullText.substring(0..it),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun VisionTestDrawerItem(testID: String, icon: @Composable () -> Unit, activity: MenuActivity) {

    var description = VisionTestUtils().getTestTypeByID(testID) + " " + VisionTestUtils().getTestNameByID(testID)

    NavigationDrawerItem(
        icon = icon,
        label = { Text(description) },
        selected = false,
        onClick = {
            val intent = Intent(activity, VisionTestLayoutActivity::class.java)
            intent.putExtra("TEST_ID", testID)
            activity.startActivity(intent)
        },
    )

}

