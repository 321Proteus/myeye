package me.proteus.myeye.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import me.proteus.myeye.MyEyeApplication
import me.proteus.myeye.io.ASRViewModel

class SpeechDecoderActivity : ComponentActivity() {

    private val viewModel: ASRViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    viewModel.initialize()
                } else {
                    println("Brak uprawnien")
                }
            }


        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.initialize()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            AppContent()
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun AppContent() {

        val result = viewModel.wordBuffer
        val context = LocalContext.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Rozpoznawanie Mowy") },
                    actions = {
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
                )
            },
            content = { innerPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (result.isEmpty()) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Zacznij mówić",
                                fontSize = 48.sp,
                                color = Color.LightGray

                            )
                        }

                    } else {
                        LazyColumn {
                            itemsIndexed(result) { index, item ->

                                Box {
                                    Text(
                                        text = item.word,
                                        fontSize = 24.sp,
                                        lineHeight = 24.sp,
                                        color = getProbabilityColor(item.confidence)
                                    )

                                }

                            }
                        }
                    }
                }

            }

        )

    }

    fun getProbabilityColor(p: Float): Color {
        return Color(ColorUtils.blendARGB(Color.Red.toArgb(), Color.Green.toArgb(), p))
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.close()
    }
}