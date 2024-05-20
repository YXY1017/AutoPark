package com.example.autopark.view

import LocationViewModel
import android.graphics.Typeface
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width

import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.autopark.viewmodel.ParkingSessionViewModel

import com.example.autopark.model.ParkingSession

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

//A composable function to display a report chart.
@RequiresApi(64)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(drawerState: DrawerState, viewModel: ParkingSessionViewModel,locationViewModel: LocationViewModel) {

    val coroutineScope = rememberCoroutineScope()
    val parkingSessions by viewModel.userParkingSessions.observeAsState(emptyList())

    // Calculate total cost and most visited location
    val totalCost = parkingSessions.sumOf { it.cost }
    val mostVisited = parkingSessions.groupingBy { it.locationId }.eachCount().maxByOrNull { it.value }?.key
    val mostVisitedLocationName = mostVisited?.let { locationViewModel.getLocationName(it) } ?: "N/A"


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Page", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding).padding(top = 8.dp)) {

                //no sessions
                if (parkingSessions.isEmpty()) {
                    Text("No Session Data found!")

                } else {
                    BarChartView(parkingSessions,locationViewModel)

                    // Display summary cards
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SummaryCard(title = "Total Cost", content = "$${totalCost.format(2)}")
                        SummaryCard(title = "Most Visited Location", content = mostVisitedLocationName)
                    }

                }

            }
        }
    )
}

@Composable
fun SummaryCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(150.dp)
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = content, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Composable
fun BarChartView(parkingSessions: List<ParkingSession>, locationViewModel: LocationViewModel) {
    // Prepare data for the bar chart
    val entries = parkingSessions.groupBy { it.locationId }
        .entries
        .sortedBy { it.key } // Sort by locationId for consistent order
        .mapIndexed { index, (locationId, sessions) ->
            val locationName = locationViewModel.getLocationName(locationId)
            BarEntry(index.toFloat(), sessions.sumOf { it.cost }.toFloat(), locationName)
        }

    val dataSet = BarDataSet(entries, "Cost by Location").apply {
        colors = ColorTemplate.COLORFUL_COLORS.toList()
        valueTextSize = 10f // Increase text size for visibility
        valueTypeface = Typeface.DEFAULT_BOLD // Make the value text bold
    }

    val barData = BarData(dataSet).apply {
        barWidth = 0.9f // Set the width of each bar
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp), // Adjust the height as necessary
        factory = { context ->
            BarChart(context).apply {
                data = barData
                description.isEnabled = false
                setFitBars(true)

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setLabelCount(entries.size, true)
                xAxis.setDrawLabels(true)
                xAxis.labelRotationAngle = 0f
                xAxis.spaceMax = 0.5f
                xAxis.textSize = 10f // Increase X-axis label size
                xAxis.valueFormatter = IndexAxisValueFormatter(entries.map { it.data.toString() })

                axisLeft.textSize = 10f // Increase Y-axis label size

                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                legend.form = Legend.LegendForm.LINE
                legend.textSize = 10f

                animateY(1000)
            }
        }
    )
}


