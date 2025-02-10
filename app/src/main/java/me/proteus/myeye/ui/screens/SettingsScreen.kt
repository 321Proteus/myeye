package me.proteus.myeye.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import me.proteus.myeye.MyEyeApplication
import me.proteus.myeye.R
import me.proteus.myeye.io.FileSaver
import me.proteus.myeye.io.HTTPDownloaderDialog
import me.proteus.myeye.io.HTTPRequestViewModel
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.TopBar
import me.proteus.myeye.ui.theme.MyEyeTheme
import java.io.File

@Composable
fun SettingsScreen(controller: NavController) {

    val model: HTTPRequestViewModel = viewModel()

    MyEyeTheme {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = { BottomBar(controller) },
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val context = LocalContext.current

                            Column(Modifier.weight(0.4f)) {
                                Text(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    text = "Język"
                                )
                                Text(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    text = stringResource(R.string.setting_desc_language)
                                )
                            }

                            val showDialog = model.showDialog.collectAsState()
                            val progress = model.progressFlow.collectAsState()

                            val scope = rememberCoroutineScope()

                            if (showDialog.value) {
                                HTTPDownloaderDialog(progress.value) { model.setShowDialog(false) }
                            }

                            Button(
                                modifier = Modifier
                                    .padding(8.dp),
                                onClick = {

                                    model.setShowDialog(true)
                                    val app = context.applicationContext as MyEyeApplication
                                    val activity = context as Activity
                                    app.setAppLanguage(activity, "pl")

                                    scope.launch {
                                        val modelName = context.getString(R.string.modelName)
                                        val url = "https://alphacephei.com/vosk/models/$modelName.zip"
                                        val downloaderPromise = model.download(url,
                                            File(app.filesDir.path + "/models/$modelName.zip"),
                                            File(app.filesDir.path + "/models/$modelName")
                                        )

                                        downloaderPromise.thenRun {
                                            FileSaver.unzip(File(app.filesDir.path + "/models/$modelName"))
                                            model.setShowDialog(false)
                                        }.exceptionally { ex ->
                                            Log.e("SettingsActivity", ex.message!!)
                                            null
                                        }
                                    }
                                }
                            ) { Text("Polski") }

                            Button(
                                modifier = Modifier
                                    .padding(8.dp),
                                onClick = {
                                    model.setShowDialog(true)
                                    val app = context.applicationContext as MyEyeApplication
                                    val activity = context as Activity
                                    app.setAppLanguage(activity, "en")

                                    scope.launch {
                                        val modelName = context.getString(R.string.modelName)
                                        val url = "https://alphacephei.com/vosk/models/$modelName.zip"
                                        val downloaderPromise = model.download(url,
                                            File(app.filesDir.path + "/models/$modelName.zip"),
                                            File(app.filesDir.path + "/models/$modelName")
                                        )

                                        downloaderPromise.thenRun {
                                            FileSaver.unzip(File(app.filesDir.path + "/models/$modelName.zip"))
                                            model.setShowDialog(false)
                                        }.exceptionally { ex ->
                                            Log.e("SettingsActivity", ex.message!!)
                                            null
                                        }
                                    }
                                }
                            ) { Text("English") }
                        }
                    }
                }
            }
        )
    }
}