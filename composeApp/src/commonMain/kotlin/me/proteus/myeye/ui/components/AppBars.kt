package me.proteus.myeye.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.font.FontFamily
import me.proteus.myeye.getCurrentRoute
import me.proteus.myeye.navigate
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.*
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource

object AppBarsHeightHolder {
    var topBarHeight = 0
    var bottomBarHeight = 0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {

    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                AppBarsHeightHolder.topBarHeight = coords.size.height
            },
        title = {
            Text(
                fontSize = 36.sp,
                fontFamily = FontFamily(Font(Res.font.optician_sans)),
                text = stringResource(Res.string.app_name)
            )
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Menu, null)
//                Icon(Icons.Filled.Menu, null)
            }
        },
        actions = {

            IconButton(onClick = { }) {
                Icon(Icons.Outlined.Settings, null)
            }

        }
    )
}

@Composable
fun BottomBar() {

    var selected by remember { mutableIntStateOf(2) }

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                AppBarsHeightHolder.bottomBarHeight = coords.size.height
            },
        content = {

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val wt = Modifier.weight(1f)

                BottomBarIcon(wt, Icons.Filled.LocationOn, stringResource(Res.string.bottom_bar_map), "map")
                { selected = 0 }
                BottomBarIcon(wt, Icons.Filled.Check, stringResource(Res.string.bottom_bar_tests), "test_selector")
                { selected = 1 }
                BottomBarIcon(wt, Icons.Filled.Home, stringResource(Res.string.bottom_bar_menu), "menu")
                { selected = 2 }
                BottomBarIcon(wt, Icons.Filled.Info, stringResource(Res.string.bottom_bar_results), "browser")
                { selected = 3 }
                BottomBarIcon(wt, Icons.Filled.Favorite, stringResource(Res.string.bottom_bar_articles), "article_browser")
                { selected = 4 }
            }
        },
    )
}


@Composable
fun BottomBarIcon(
    modifier: Modifier,
    icon: ImageVector,
    text: String,
    route: String,
    onSelect: () -> Unit
) {

    val isNameMatching = getCurrentRoute().startsWith(route)

    Box(
        modifier
            .zIndex(1f)
            .clickable {
                println(isNameMatching)
            if (!isNameMatching)
                onSelect()
                navigate(route)
            },
//            .then(if (isNameMatching) Modifier.border(2.dp, Color.Red) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, "")
            Text(text, fontSize = 10.sp)
        }
    }
}