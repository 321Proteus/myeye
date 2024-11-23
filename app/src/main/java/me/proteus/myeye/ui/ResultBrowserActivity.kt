package me.proteus.myeye

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.theme.MyEyeTheme

class ResultBrowserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyEyeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column (
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(Color.LightGray)
                    ) {
                        val dbConnector = ResultDataSaver(this@ResultBrowserActivity)
                        dbConnector.select("*")

                        val ids: List<Int> = dbConnector.idList;

                        for (i in ids) print("$i ")
                        println()

                        for (i in ids) {
                            Text(text = dbConnector.testNames[i-1])
                        }
                    }

                }
            }
        }
    }
}