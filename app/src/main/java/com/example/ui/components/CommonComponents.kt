package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.data.model.QueueStatus
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QlinicsTopBar(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    unreadNotificationCount: Int,
    onOpenNotifications: () -> Unit,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .testTag("btn_top_back")
                                .size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back",
                                tint = MedicalNavy,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MedicalBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = "App Logo",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Qlinics",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 21.sp,
                                    color = MedicalBluePrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDCFCE7),
                                border = BorderStroke(1.dp, Color(0xFF86EFAC))
                            ) {
                                Text(
                                    text = "LIVE QUEUE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Less Waiting. Better Care.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MedicalNavy,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Notification Bell with high-contrast count
                IconButton(
                    onClick = onOpenNotifications,
                    modifier = Modifier
                        .testTag("btn_notifications")
                        .size(46.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(
                                    containerColor = DangerRed,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = unreadNotificationCount.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MedicalNavy,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Role Switcher Chips Bar (Patient, Receptionist, Doctor)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEBF2FE))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                UserRole.values().forEach { role ->
                    val isSelected = currentRole == role
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("role_tab_${role.name.lowercase()}")
                            .clickable { onRoleSelected(role) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MedicalBluePrimary else Color.Transparent,
                        shadowElevation = if (isSelected) 3.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (role) {
                                    UserRole.PATIENT -> Icons.Default.Person
                                    UserRole.RECEPTIONIST -> Icons.Default.Assignment
                                    UserRole.DOCTOR -> Icons.Default.MedicalServices
                                },
                                contentDescription = role.displayName,
                                tint = if (isSelected) Color.White else MedicalNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = role.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) Color.White else MedicalNavy,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: QueueStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status) {
        QueueStatus.JOINED -> Triple(Color(0xFFE0EDFF), Color(0xFF0D62FE), Icons.Default.HowToReg)
        QueueStatus.WAITING -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), Icons.Default.HourglassEmpty)
        QueueStatus.ALMOST_YOUR_TURN -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), Icons.Default.NotificationImportant)
        QueueStatus.CALLED -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), Icons.Default.Campaign)
        QueueStatus.WITH_DOCTOR -> Triple(Color(0xFFDCFCE7), Color(0xFF047857), Icons.Default.MeetingRoom)
        QueueStatus.COMPLETED -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), Icons.Default.CheckCircle)
        QueueStatus.SKIPPED -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), Icons.Default.FastForward)
        QueueStatus.CANCELLED -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), Icons.Default.Cancel)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, textColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.label,
                tint = textColor,
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun NotificationSheet(
    notifications: List<NotificationItem>,
    onDismiss: () -> Unit,
    onNotificationClick: (NotificationItem) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_close_notifications"),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MedicalBluePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Live Notifications",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MedicalNavy,
                        fontSize = 19.sp
                    )
                )
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notifications yet.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MedicalSlate, fontSize = 14.sp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    notifications.take(8).forEach { item ->
                        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNotificationClick(item) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (item.isRead) Color.White else Color(0xFFEBF2FE),
                            border = BorderStroke(1.dp, MedicalBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (item.type) {
                                                "ROOM_CALL" -> Color(0xFFDCFCE7)
                                                "TURN_APPROACHING" -> Color(0xFFFEF3C7)
                                                else -> Color(0xFFE0EDFF)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (item.type) {
                                            "ROOM_CALL" -> Icons.Default.Campaign
                                            "TURN_APPROACHING" -> Icons.Default.Timer
                                            "COMPLETED" -> Icons.Default.Check
                                            else -> Icons.Default.Info
                                        },
                                        contentDescription = null,
                                        tint = when (item.type) {
                                            "ROOM_CALL" -> Color(0xFF15803D)
                                            "TURN_APPROACHING" -> Color(0xFFB45309)
                                            else -> MedicalBluePrimary
                                        },
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MedicalNavy,
                                                fontSize = 14.sp
                                            )
                                        )
                                        Text(
                                            text = timeStr,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MedicalNavy,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.message,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MedicalNavy,
                                            fontSize = 13.sp,
                                            lineHeight = 17.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun LiveNotificationBanner(
    message: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        if (message != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                color = MedicalNavy,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(LiveGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PatientBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    hasActiveQueue: Boolean
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxWidth(),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(Screen.HOME, "Home", Icons.Default.Home),
            Triple(Screen.SPECIALIST_SEARCH, "Specialists", Icons.Default.HealthAndSafety),
            Triple(Screen.HOSPITAL_MAP, "Map", Icons.Default.Place),
            Triple(Screen.LIVE_QUEUE, "Live Queue", Icons.Default.AccessTime),
            Triple(Screen.APPOINTMENTS, "Bookings", Icons.Default.EventNote),
            Triple(Screen.PROFILE, "Profile", Icons.Default.Person)
        )

        navItems.forEach { (screen, label, icon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                modifier = Modifier.testTag("nav_${label.lowercase().replace(" ", "_")}"),
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (screen == Screen.LIVE_QUEUE && hasActiveQueue) {
                                Badge(containerColor = LiveGreen) {
                                    Text("LIVE", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(24.dp),
                            tint = if (isSelected) MedicalBluePrimary else MedicalNavy
                        )
                    }
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) MedicalBluePrimary else MedicalNavy
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFE0EDFF),
                    selectedIconColor = MedicalBluePrimary,
                    unselectedIconColor = MedicalNavy
                )
            )
        }
    }
}
