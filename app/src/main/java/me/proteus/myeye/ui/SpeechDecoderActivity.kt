package me.proteus.myeye.ui

import android.Manifest
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
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

        val result = viewModel.result

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Rozpoznawanie Mowy") })
            },
            content = { innerPadding ->

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    itemsIndexed(result) { index, item ->

                        Box() {

                            val line = "${item.word}"
                            val probabilityColor = Color(ColorUtils.blendARGB(Color.Red.toArgb(), Color.Green.toArgb(), item.confidence))

                            Text(
                                text = line,
                                fontSize = 24.sp,
                                lineHeight = 24.sp,
                                color = probabilityColor
                            )

                        }

                    }
                }
            }

        )

    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.close()
    }
}