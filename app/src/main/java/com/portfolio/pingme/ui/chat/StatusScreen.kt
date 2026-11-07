package com.portfolio.pingme.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
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
fun StatusScreen() {
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
                Text(
                    text = "Status",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = WhatsAppTextDark
                )
            }

            // My Status Item
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(56.dp)) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(WhatsAppDarkGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Me", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(WhatsAppDarkGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "My status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = WhatsAppTextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tap to add status update",
                            fontSize = 13.sp,
                            color = WhatsAppTextMuted
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Recent updates",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = WhatsAppTextMuted
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No recent status updates",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = WhatsAppTextDark
                        )
                        Text(
                            text = "When your contacts share status updates, they will appear here.",
                            fontSize = 13.sp,
                            color = WhatsAppTextMuted
                        )
                    }
                }
            }
        }

        // Floating Action Buttons for Status
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SmallFloatingActionButton(
                onClick = { },
                containerColor = Color(0xFFE9EDEF),
                contentColor = WhatsAppTextDark
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Text Status")
            }

            FloatingActionButton(
                onClick = { },
                containerColor = WhatsAppDarkGreen,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera Status")
            }
        }
    }
}
