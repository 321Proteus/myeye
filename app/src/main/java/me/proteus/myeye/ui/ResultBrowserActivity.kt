package me.proteus.myeye.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.TestResult
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils

class ResultBrowserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyEyeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    ResultColumn(this@ResultBrowserActivity, innerPadding)

                }
            }
        }
    }
}

@Composable
fun ResultColumn(activity: ResultBrowserActivity, paddingValues: PaddingValues) {

    val dbConnector = ResultDataSaver(activity)
    dbConnector.select("*")

    val data: List<TestResult> = dbConnector.resultData

    for (i in data) print("$i ")
    println()

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        for (result in data) {

            Box(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .clickable {
                        val intent: Intent = Intent(activity, TestResultActivity::class.java)
                        intent.putExtra("RESULT_PARCEL", result)
                        activity.startActivity(intent)
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .aspectRatio(1.0f)
                            .clip(shape = RoundedCornerShape(15.dp))
                            .background(Color.Blue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(VisionTestUtils().getTestByID(result.testID).testIcon, null, tint = Color.White)
                    }
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = result.fullTestName,
                            fontSize = 18.sp
                        )
                        Text(
                            text = result.formattedTimestamp,
                            fontSize = 14.sp
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.padding(8.dp))
        }

    }



}