package com.example.autopark.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toFormattedString(): String {
    return this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateDuration(startTime: String, endTime: String): Long {
    val start = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val end = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    return ChronoUnit.MINUTES.between(start, end)
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateDurationMore(startTime: String, endTime: String): String {
    val start = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val end = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val duration = Duration.between(start, end)

    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60)  // Gets the remainder minutes after dividing by 60
    val seconds = (duration.seconds % 60)  // Gets the remainder seconds after dividing by 60

    return "${hours}h ${minutes}m ${seconds}s"
}
