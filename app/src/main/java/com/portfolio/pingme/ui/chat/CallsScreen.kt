package com.portfolio.pingme.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.pingme.ui.theme.*

@Composable
fun CallsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhatsAppLightBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calls",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = WhatsAppTextDark
                    )
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "New Call", tint = WhatsAppDarkGreen)
                    }
                }
            }

            item {
                Text(
                    text = "Recent",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = WhatsAppTextMuted
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(WhatsAppLightBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = WhatsAppDarkGreen)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "End-to-end encrypted",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = WhatsAppTextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Your personal calls are end-to-end encrypted",
                                fontSize = 12.sp,
                                color = WhatsAppTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
