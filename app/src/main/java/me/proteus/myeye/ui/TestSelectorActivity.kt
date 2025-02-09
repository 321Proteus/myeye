package me.proteus.myeye.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.ExpandableGrid
import me.proteus.myeye.ui.components.TopBar
import me.proteus.myeye.ui.theme.MyEyeTheme

class TestSelectorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyEyeTheme {
                Scaffold(
                    topBar = { TopBar() },
                    bottomBar = { BottomBar(this) }
                ) { innerPadding ->
                    BoxWithConstraints(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        ExpandableGrid(this.maxHeight, true)
                    }
                }
            }
        }
    }
}