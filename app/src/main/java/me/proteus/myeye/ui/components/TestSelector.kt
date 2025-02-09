package me.proteus.myeye.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.visiontests.VisionTestUtils
import me.proteus.myeye.R

@Composable
fun ExpandableGrid(height: Dp, toggleExpand: Boolean) {
    var expanded by remember { mutableStateOf(toggleExpand) }
    val list = VisionTestUtils().testList

    Column(modifier = Modifier.fillMaxSize()) {

        if (!toggleExpand) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.menu_discover_tests),
                    fontSize = 14.sp,
                )
                Text(
                    modifier = Modifier
                        .clickable { expanded = !expanded },
                    text = stringResource(R.string.menu_tests_see_all),
                    color = Color.Blue
                )
            }
        }

        AnimatedContent(targetState = expanded, label = "Grid Transition") { isExpanded ->
            if (isExpanded) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .height(height)
                        .padding(8.dp)
                        .background(Color.LightGray),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(list) { id ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.width(80.dp)) {
                                VisionTestIcon(
                                    modifier = Modifier,
                                    testID = id.testID,
                                    size = 0.4f,
                                    clickable = true,
                                    context = LocalContext.current
                                )
                            }
                            Text(VisionTestUtils().getTestNameByID(id.testID), fontSize = 12.sp)
                        }

                    }
                }
            } else {

                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(list) { id ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.width(80.dp)) {
                                VisionTestIcon(
                                    modifier = Modifier,
                                    testID = id.testID,
                                    size = 0.4f,
                                    clickable = true,
                                    context = LocalContext.current
                                )
                            }
                            Text(VisionTestUtils().getTestNameByID(id.testID), fontSize = 12.sp)
                        }

                    }
                }
            }
        }
    }
}
