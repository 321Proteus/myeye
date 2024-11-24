package me.proteus.myeye

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
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
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.VisionTestUtils
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    val ids: List<Int> = dbConnector.idList;

    for (i in ids) print("$i ")
    println()

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        for (i in ids) {

            var testID = dbConnector.testNames[i-1]
            var boxTitle: String;

            val timestamp: Long = dbConnector.timestamps[i-1]
            var date = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC)

            val fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault())
            var formattedDate = date.format(fmt)

            try {

                boxTitle = VisionTestUtils().getTestTypeByID(testID) + " " + VisionTestUtils().getTestNameByID(testID)

            } catch (e: IllegalArgumentException) {

                boxTitle = "Nieznany test"
                Log.w(activity.localClassName, "Nieprawidlowe ID testu: '$testID' na pozycji $i")
            }


            Box(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .aspectRatio(1.0f)
                            .clip(shape = RoundedCornerShape(15.dp))
                            .background(Color.Blue)
                    )
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = boxTitle,
                            fontSize = 18.sp
                        )
                        Text(
                            text = formattedDate,
                            fontSize = 14.sp
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.padding(8.dp))
        }

    }



}