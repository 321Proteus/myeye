package me.proteus.myeye.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.proteus.myeye.util.LanguageUtils
import me.proteus.myeye.MyEyeApplication
import me.proteus.myeye.R
import me.proteus.myeye.io.FileSaver
import me.proteus.myeye.io.HTTPDownloaderDialog
import me.proteus.myeye.io.HTTPRequestViewModel
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.theme.MyEyeTheme
import java.io.File

class SettingsActivity : ComponentActivity() {

    private val model: HTTPRequestViewModel by viewModels()

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
                    bottomBar = { BottomBar(this) },
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
    }

    override fun attachBaseContext(newBase: Context?) {
        val currentLanguage = LanguageUtils.getCurrentLanguage(newBase)
        val newContext = LanguageUtils.setLocale(newBase, currentLanguage)
        super.attachBaseContext(newContext)
    }

}