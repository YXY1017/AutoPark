package com.example.autopark.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autopark.viewmodel.NavigationViewModel
import com.example.autopark.R
import kotlinx.coroutines.launch


//A composable function to display the about app info.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(navViewModel: NavigationViewModel, drawerState: DrawerState) {
    navViewModel.name.value
    Scaffold(
        topBar = {
            val coroutineScope = rememberCoroutineScope()
            TopAppBar(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .shadow(10.dp,
                        shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
                        clip = true)
                    .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp)),
                title = { Text(text = "Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    )
    {paddingValue ->
        Column(modifier = Modifier
            .padding(paddingValue)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Image(
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp),
                painter = painterResource(id = R.drawable.ic_launcher_round),
                contentScale = ContentScale.Fit,
                contentDescription = "Auto-Park Icon"
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Auto Park", fontWeight = FontWeight.Bold, fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row{
                Text("Parking Simplified.", fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                Text("Build: 1.0β (March, 24)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row{
                Text("Copyright Team03@FIT5056", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}