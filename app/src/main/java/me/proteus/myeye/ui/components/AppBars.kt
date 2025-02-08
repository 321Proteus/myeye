package me.proteus.myeye.ui.components

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import me.proteus.myeye.BottomBarIcon
import me.proteus.myeye.MenuActivity
import me.proteus.myeye.R
import me.proteus.myeye.ui.MapActivity
import me.proteus.myeye.ui.ResultBrowserActivity
import me.proteus.myeye.ui.TestSelectorActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                fontSize = 36.sp,
                fontFamily = FontFamily(Font(R.font.opticiansans)),
                text = stringResource(R.string.app_name)
            )
        },
        navigationIcon = {
            IconButton(onClick = { }) {
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
}

@Composable
fun BottomBar(activity: ComponentActivity) {
    BottomAppBar(
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomBarIcon(Modifier.weight(1f), Icons.Filled.LocationOn, "Mapa") {
                    activity.startActivity(Intent(activity, MapActivity::class.java))
                }
                BottomBarIcon(Modifier.weight(1f), Icons.Filled.Check, "Testy") {
                    activity.startActivity(Intent(activity, TestSelectorActivity::class.java))
                }
                BottomBarIcon(Modifier.weight(1f), Icons.Filled.Home, "Menu") {
                    if (activity.localClassName != "MenuActivity")
                        activity.startActivity(Intent(activity, MenuActivity::class.java))
                }
                BottomBarIcon(Modifier.weight(1f), Icons.Filled.Info, "Wyniki") {
                    activity.startActivity(Intent(activity, ResultBrowserActivity::class.java))
                }
                BottomBarIcon(Modifier.weight(1f), Icons.Filled.Build, "Narzędzia") {
//                            activity.startActivity(Intent(activity, ToolSelectorActivity::class.java))
                }

            }
        },
    )
}