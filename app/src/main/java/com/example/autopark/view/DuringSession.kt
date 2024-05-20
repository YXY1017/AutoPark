package com.example.autopark.view

import LocationViewModel
import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.autopark.viewmodel.NavigationViewModel
import com.example.autopark.model.ParkingSession
import com.example.autopark.viewmodel.ParkingSessionViewModel



import java.time.Duration
import java.time.LocalDateTime

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.example.autopark.utils.calculateDurationMore

//A composable function to show the countdown timer of the current session.
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("NotConstructor")
@Composable
fun DuringSessionScreen(
    drawerState: DrawerState,
    navViewModel: NavigationViewModel,
    navController: NavController,
    parkingSessionViewModel: ParkingSessionViewModel,
    locationViewModel: LocationViewModel
) {
    val session = parkingSessionViewModel.getFinalSessionToDisplay()
    val openConfirmationDialog = remember { mutableStateOf(false) }
    val customColor = MaterialTheme.colorScheme.primary

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(customColor)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(50.dp))
            Text("Parking Session Details", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))

            session?.let {
                if (session.endTime == "Ongoing") {  // Ensure session is active

                    //timer
                    val startTime = LocalDateTime.parse(session.startTime)  // Parse startTime


                    // Default maxStay to 0 if null
                    //val maxStay = location?.maxStayInHours ?: 0

                    ParkingTimer(startTime = startTime, 1,parkingSessionViewModel,openConfirmationDialog)
                }
            } ?: Text("No active session", color = Color.White)

            Spacer(modifier = Modifier.height(20.dp))
            StopButton {
                openConfirmationDialog.value = true
                parkingSessionViewModel.stopParking()
            }
            if (openConfirmationDialog.value) {
                ReceiptDialog(navController, session, locationViewModel) { openConfirmationDialog.value = false }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StopButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
    ) {
        Text("STOP", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

//A composable function to show the receipt dialog.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReceiptDialog(navController: NavController, session: ParkingSession?, locationViewModel: LocationViewModel, onDismiss: () -> Unit) {
    if (session != null) {
        val durationText = if (session.endTime != "Ongoing") {
            val duration = calculateDurationMore(session.startTime, session.endTime)
            "$duration"
        } else {
            "Still in progress"
        }



        AlertDialog(
            onDismissRequest = {
                // Navigate to home when dialog is dismissed regardless of the method
                navController.navigate("home")
                onDismiss()
            },
            title = {
                Text("Parking Session Receipt", fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    letterSpacing = 0.15.sp)
            },
            text = {
                Column {
                    Text("Location Name: ${locationViewModel.getLocationName(session.locationId)}")
                    Text("Vehicle ID: ${session.carId}")
                    Text("Start Time: ${session.startTime}")
                    Text("End Time: ${session.endTime}")
                    Text("Duration: $durationText")
                    Text("Total Cost: $${session.cost}")
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        navController.navigate("home") // Assume "home" is the route for home screen
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

//A composable function to show the parking timer.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ParkingTimer(startTime: LocalDateTime, maxStay: Int,parkingSessionViewModel: ParkingSessionViewModel,openConfirmationDialog: MutableState<Boolean>) {
    val endTime = startTime.plusHours(maxStay.toLong())
    var now = remember { LocalDateTime.now() }
    var timeLeft by remember { mutableStateOf(Duration.between(now, endTime)) }
    val totalDuration = Duration.between(startTime, endTime).seconds.toFloat()
    var progress by remember { mutableStateOf((totalDuration - timeLeft.seconds.toFloat()) / totalDuration) }

    LaunchedEffect(key1 = "timer") {
        while (true) {
            now = LocalDateTime.now()
            if (now.isBefore(endTime)) {
                timeLeft = Duration.between(now, endTime)
                val currentProgress = (totalDuration - timeLeft.seconds.toFloat()) / totalDuration
                progress = currentProgress.coerceIn(0f, 1f)
            } else {

                timeLeft = Duration.ZERO
                progress = 1f

                // Trigger the confirmation dialog and stop parking when max stay is reached
                openConfirmationDialog.value = true

                parkingSessionViewModel.stopParking()

                break

            }
            kotlinx.coroutines.delay(1000)
        }
    }

    val textPaint = remember {
        Paint().apply {
            textAlign = Paint.Align.CENTER
            textSize = 80f
            color=android.graphics.Color.WHITE
            typeface = Typeface.DEFAULT_BOLD
        }
    }


    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //Text(
            //"Time Left",
            //color = Color.DarkGray,
            //fontSize = 18.sp,
            //fontWeight = FontWeight.Bold
        //)

        Spacer(modifier = Modifier.height(8.dp))

        //Text(
            //formatDuration(timeLeft),
            //fontSize = 24.sp,
            //fontWeight = FontWeight.Bold,
            //color = Color.Red
        //)

        Spacer(modifier = Modifier.height(16.dp))

        // Custom circular progress indicator with background
        Canvas(modifier = Modifier.size(250.dp)) {

            val strokeWidth = 12.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth / 2
            val adjustedSize = Size(
                width = size.width - strokeWidth * 2,
                height = size.height - strokeWidth * 2
            )

            drawCircle(
                color = Color.White,
                center = Offset(x = size.width / 2, y = size.height / 2),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                color = Color.Red,
                startAngle = -90f,
                sweepAngle = 360 * progress,  // Updated to use dynamic progress
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = Offset(strokeWidth, strokeWidth),
                size = adjustedSize
            )

            // Centering the text inside the circle
            val text = formatDuration(timeLeft)

            drawContext.canvas.nativeCanvas.drawText(
                text,
                size.width / 2,
                size.height / 2 + textPaint.fontMetrics.let { -it.ascent / 2 - it.descent / 2 },
                textPaint.apply {
                    textSize = 70f  // Set the text size as needed
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDuration(duration: Duration): String {
    val seconds = duration.seconds % 60
    val minutes = (duration.toMinutes() % 60).toString().padStart(2, '0')
    val hours = (duration.toHours() % 24).toString().padStart(2, '0')
    return "$hours:$minutes:${seconds.toString().padStart(2, '0')}"
}

