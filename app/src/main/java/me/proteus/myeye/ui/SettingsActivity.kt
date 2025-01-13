package me.proteus.myeye.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.MyEyeApplication
import me.proteus.myeye.io.ASRViewModel
import me.proteus.myeye.ui.theme.MyEyeTheme

class SettingsActivity : ComponentActivity() {

    private val asrViewModel: ASRViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyEyeTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Dostosuj MyEye", maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row {
                                    Column {
                                        Text(
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            text = "Język"
                                        )
                                        Text(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            text = "Wybiera język używany w aplikacji"
                                        )
                                    }

                                    val context = LocalContext.current

                                    Button(
                                        modifier = Modifier
                                            .padding(8.dp),
                                        onClick = {
                                            val app = context.applicationContext as MyEyeApplication
                                            val activity = context as Activity
                                            app.setAppLanguage(activity, "pl")
                                        }
                                    ) { Text("Polski") }

                                    Button(
                                        modifier = Modifier
                                            .padding(8.dp),
                                        onClick = {
                                            val app = context.applicationContext as MyEyeApplication
                                            val activity = context as Activity
                                            app.setAppLanguage(activity, "en")
                                        }
                                    ) { Text("English") }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}