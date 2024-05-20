package com.example.autopark.view

import UserViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autopark.Routes


//A composable function to display the drawer content.
@Composable
fun DrawerContent(
    menus: Array<DrawerMenu>,
    userViewModel: UserViewModel,
    onMenuClick: (String) -> Unit
) {
    LaunchedEffect(true) {
        userViewModel.loadCurrentUser() // Ensures listener is always fresh
    }
    val user = userViewModel.user.value
    LaunchedEffect(user) {
        Log.d("DrawerContent", "User data recomposed: $user")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)

    ) {
        Row(
            Modifier
                .height(65.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = { onMenuClick(Routes.Profile.value) }),
            verticalAlignment = Alignment.CenterVertically)
        {
            Spacer(modifier = Modifier.width(3.dp))
            Image(
                modifier = Modifier
                    .size(55.dp)
                    .padding(10.dp),
                imageVector = Icons.Filled.Person,
                contentScale = ContentScale.Fit,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
            Column {
                Text(text = user.firstName+" "+ user.lastName, fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary)
                Text("Registered User", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        menus.forEach {
            NavigationDrawerItem(
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
                ,
                label = { Text(text = it.title, color = MaterialTheme.colorScheme.onSecondaryContainer) },
                icon = { Icon(imageVector = it.icon,
                    contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer) },
                selected = false,
                onClick = {
                    onMenuClick(it.route)
                }
            )
        }
    }
}