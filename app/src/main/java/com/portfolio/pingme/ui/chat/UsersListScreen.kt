package com.portfolio.pingme.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.domain.repository.Resource
import com.portfolio.pingme.ui.theme.*

@Composable
fun UsersListScreen(
    viewModel: ChatViewModel,
    onUserClick: (User) -> Unit
) {
    val usersState by viewModel.usersState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = PurpleLightBg
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Custom Banner Blueprint
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(PurpleMain)
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "PINGME MESSENGER",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 20.sp,
                            letterSpacing = 1.sp
                        )

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            placeholder = { Text("Search chats...", color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted) },
                            shape = RoundedCornerShape(26.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                }

                // Scrollable Active Contacts Mesh
                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 10.dp)) {
                    when (usersState) {
                        is Resource.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = PurpleMain
                            )
                        }
                        is Resource.Success -> {
                            val users = (usersState as Resource.Success<List<User>>).data
                            val filteredUsers = if (searchQuery.isEmpty()) {
                                users
                            } else {
                                users.filter { it.name.contains(searchQuery, ignoreCase = true) || it.phoneNumber.contains(searchQuery) }
                            }
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredUsers) { user ->
                                    PremiumUserRow(user = user, onClick = { onUserClick(user) })
                                }
                            }
                        }
                        is Resource.Error -> {
                            Text(
                                text = "Error loading contacts",
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.Red
                            )
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumUserRow(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(PurpleLightBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (user.name.isNotEmpty()) user.name.take(1).uppercase() else "?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PurpleMain
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (user.name.isEmpty()) user.phoneNumber else user.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = "Tap to chat real-time...", fontSize = 13.sp, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Active", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier.size(18.dp).clip(CircleShape).background(BadgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✓", color = Color.White, fontSize = 10.sp)
                }
            }
        }
    }
}
