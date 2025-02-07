package me.proteus.myeye.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.theme.MyEyeTheme

@Composable
fun ResultBrowserScreen(controller: NavController) {
    MyEyeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            val dbConnector = ResultDataSaver(controller.context)
            dbConnector.select("*")

            var data by remember { mutableStateOf(dbConnector.resultData) }

            LazyColumn (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
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
                                controller.navigate("result/${result.resultID}/false")
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                VisionTestIcon(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp, horizontal = 16.dp),
                                    testID = result.testID
                                )

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
    }
}