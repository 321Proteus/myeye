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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.io.ResultDataSaver
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

    var data by remember { mutableStateOf(dbConnector.resultData) }

    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        items(data) { result ->

            Box(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .clickable {
                        val intent: Intent = Intent(activity, TestResultActivity::class.java)
                        intent.putExtra("IS_AFTER", false)
                        intent.putExtra("RESULT_PARCEL", result)
                        activity.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .aspectRatio(1.0f)
                                .clip(shape = RoundedCornerShape(15.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = VisionTestUtils().getTestByID(result.testID).testIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
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

                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .aspectRatio(1.0f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier
                                .clickable {
                                    dbConnector.delete(result.resultID)
                                    data = data.filter { it.resultID != result.resultID }
                                },
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))
        }

    }



}