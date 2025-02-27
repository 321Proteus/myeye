package me.proteus.myeye

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.ui.components.ExpandableGrid
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.TopBar
import androidx.navigation.NavController
import me.proteus.myeye.ui.theme.MyEyeTheme

@Composable
fun MainMenu(controller: NavController) {

    MyEyeTheme {
        MenuScreen(controller)
    }

}

@Composable
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

                ExpandableGrid((LocalConfiguration.current.screenHeightDp / 2).dp, false, controller)

            }
        },
        bottomBar = { BottomBar(controller) }
    )
}

