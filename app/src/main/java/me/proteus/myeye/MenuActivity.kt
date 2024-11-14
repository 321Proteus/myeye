package me.proteus.myeye

import android.content.Intent
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
import me.proteus.myeye.ui.theme.MyEyeTheme

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
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                                label = { Text("Osoba") },
                                selected = false,
                                onClick = { println("Wcisnieto guzik 1") },
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Outlined.Build, contentDescription = null) },
                                label = { Text("Budowanie") },
                                selected = false,
                                onClick = { println("Wcisnieto guzik 2") },
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                                label = { Text("Informacja") },
                                selected = false,
                                onClick = { println("Wcisnieto guzik 3") },
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.TwoTone.Face, contentDescription = null) },
                                label = { Text("Test Wzroku LogMAR") },
                                selected = false,
                                onClick = {
                                    val intent = Intent(this@MenuActivity, SnellenChartActivity::class.java)
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

