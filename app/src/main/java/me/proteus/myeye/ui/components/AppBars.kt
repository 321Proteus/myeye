package me.proteus.myeye.ui.components

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import me.proteus.myeye.MenuActivity
import me.proteus.myeye.R
import me.proteus.myeye.ui.MapActivity
import me.proteus.myeye.ui.ResultBrowserActivity
import me.proteus.myeye.ui.SettingsActivity
import me.proteus.myeye.ui.SimpleDistanceActivity
import me.proteus.myeye.ui.SpeechDecoderActivity
import me.proteus.myeye.ui.TestSelectorActivity
import me.proteus.myeye.ui.VisionTestLayoutActivity
import me.proteus.myeye.visiontests.VisionTestUtils

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

class BottomBarPainter(val offset: Offset, val size: Size) : Painter() {

    override val intrinsicSize: Size
        get() = size

    override fun DrawScope.onDraw() {

        drawCircle(center = offset, radius = size.minDimension / 2.0f, color = Color.Red)
    }
}

@Composable
fun BottomBar(activity: ComponentActivity) {

    var selected by remember { mutableIntStateOf(2) }

    var rowSize by remember { mutableStateOf(Size(0f, 0f)) }
//    var mul by remember { mutableFloatStateOf(0f) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            while(mul < 100) { mul++; delay(10) }
//            while(mul > 0) { mul--; delay(10) }
//        }
//    }

    val offset by remember(rowSize, selected) {
        derivedStateOf {
            Offset(rowSize.width * selected / 4, rowSize.height / 2)
        }
    }

    BottomAppBar(
        content = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        rowSize = Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                    },
                 //   .paint(BottomBarPainter(offset, rowSize)),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomBarIcon(Modifier.weight(1f), Icons.Filled.LocationOn, stringResource(R.string.bottom_bar_map),
                    activity, MapActivity()
                ) { selected = 0 }
                BottomBarIcon(Modifier.weight(1f), Icons.Filled.Check, stringResource(R.string.bottom_bar_tests),
                    activity, TestSelectorActivity()
                ) { selected = 1 }
                BottomBarIcon(Modifier.weight(1f), Icons.Filled.Home, stringResource(R.string.bottom_bar_menu),
                    activity, MenuActivity()
                ) { selected = 2 }
                BottomBarIcon(Modifier.weight(1f), Icons.Filled.Info, stringResource(R.string.bottom_bar_results),
                    activity, ResultBrowserActivity()
                ) { selected = 3 }
                BottomBarIcon(Modifier.weight(1f), Icons.Filled.Build, stringResource(R.string.bottom_bar_tools),
                    activity, SettingsActivity()
                ) { selected = 4 }

            }
        },
    )
}


@Composable
fun BottomBarIcon(
    modifier: Modifier,
    icon: ImageVector,
    text: String,
    activity: ComponentActivity,
    target: ComponentActivity,
    onSelect: () -> Unit
) {

    val isNameMatching = activity::class.java.simpleName == target::class.java.simpleName

    Box(
        modifier
            .zIndex(1f)
            .clickable {
                println(isNameMatching)
            if (!isNameMatching)
                onSelect()
                activity.startActivity(Intent(activity, target::class.java))
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

@Composable
fun NavBar(activity: MenuActivity) {
    ModalDrawerSheet {

        VisionTestDrawerItem("TEST_SIGHT_CIRCLE", activity)
        VisionTestDrawerItem("TEST_TODO_BUILD", activity)
        VisionTestDrawerItem("TEST_SIGHT_INFO", activity)
        VisionTestDrawerItem("TEST_SIGHT_LOGMAR", activity)
        VisionTestDrawerItem("TEST_COLOR_ARRANGE", activity)
        VisionTestDrawerItem("TEST_MISC_REACTION", activity)

        NavigationDrawerItem(
            icon = { Icon(Icons.TwoTone.Check, contentDescription = null) },
            label = { Text("Przeglądaj wyniki") },
            selected = false,
            onClick = {
                activity.startActivity(Intent(activity, ResultBrowserActivity::class.java))
            },
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Call, contentDescription = null) },
            label = { Text("Vosk Test") },
            selected = false,
            onClick = {
                activity.startActivity(Intent(activity, SpeechDecoderActivity::class.java))
            },
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.TwoTone.LocationOn, contentDescription = null) },
            label = { Text("Pomiar dystansu") },
            selected = false,
            onClick = {
                activity.startActivity(Intent(activity, SimpleDistanceActivity::class.java))
            },
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.TwoTone.LocationOn, contentDescription = null) },
            label = { Text("Mapa") },
            selected = false,
            onClick = {
                activity.startActivity(Intent(activity, MapActivity::class.java))
            },
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
            label = { Text("Ustawienia") },
            selected = false,
            onClick = {
                activity.startActivity(Intent(activity, SettingsActivity::class.java))
            }
        )

    }
}
@Composable
fun VisionTestDrawerItem(testID: String, activity: MenuActivity) {

    val icon = VisionTestUtils().getTestByID(testID).testIcon

    val description = VisionTestUtils().getTestTypeByID(testID) + " " + VisionTestUtils().getTestNameByID(testID)

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