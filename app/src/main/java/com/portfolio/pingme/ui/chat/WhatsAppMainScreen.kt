package com.portfolio.pingme.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppMainScreen(
    viewModel: ChatViewModel,
    onUserClick: (User) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PingMe",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppDarkGreen)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = WhatsAppDarkGreen
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(imageVector = Icons.Default.Chat, contentDescription = "Chats") },
                    label = { Text("Chats", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppDarkGreen,
                        selectedTextColor = WhatsAppDarkGreen,
                        unselectedIconColor = WhatsAppTextMuted,
                        unselectedTextColor = WhatsAppTextMuted,
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(imageVector = Icons.Default.DonutLarge, contentDescription = "Updates") },
                    label = { Text("Updates", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppDarkGreen,
                        selectedTextColor = WhatsAppDarkGreen,
                        unselectedIconColor = WhatsAppTextMuted,
                        unselectedTextColor = WhatsAppTextMuted,
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(imageVector = Icons.Default.Call, contentDescription = "Calls") },
                    label = { Text("Calls", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppDarkGreen,
                        selectedTextColor = WhatsAppDarkGreen,
                        unselectedIconColor = WhatsAppTextMuted,
                        unselectedTextColor = WhatsAppTextMuted,
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
            }
        },
        containerColor = WhatsAppLightBg
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTab) {
                0 -> UsersListScreen(viewModel = viewModel, onUserClick = onUserClick)
                1 -> StatusScreen(viewModel = viewModel)
                2 -> CallsScreen(
                    viewModel = viewModel,
                    onStartCall = { user, _ ->
                        onUserClick(user)
                    }
                )
            }
        }
    }
}
