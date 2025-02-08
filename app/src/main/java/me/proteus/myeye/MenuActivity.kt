package me.proteus.myeye

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import me.proteus.myeye.ui.ResultBrowserActivity
import me.proteus.myeye.ui.SettingsActivity
import me.proteus.myeye.ui.SimpleDistanceActivity
import me.proteus.myeye.ui.SpeechDecoderActivity
import me.proteus.myeye.ui.VisionTestLayoutActivity
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.NavBar
import me.proteus.myeye.ui.components.TopBar
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
                    drawerContent = { NavBar(this) }
                ) {
                    MenuScreen(this)//scope = scope, state = drawerState)
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
fun MenuScreen(/*scope: CoroutineScope, state: DrawerState*/activity: ComponentActivity) {
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

