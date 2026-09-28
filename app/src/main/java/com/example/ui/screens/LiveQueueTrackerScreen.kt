package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QueuePatient
import com.example.data.model.QueueStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel

@Composable
fun LiveQueueTrackerScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val priyaQueue by viewModel.priyaQueue.collectAsState()
    val queueCalculations by viewModel.queueCalculations.collectAsState()
    val (aheadCount, estWait, queueStatus) = queueCalculations
    val myToken = viewModel.patientToken // A-27

    // Single unified LazyColumn allows the entire screen to scroll up and down smoothly
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .testTag("live_queue_scroll_container"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ITEM 1: Header Bar with Live Indicator & Status
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LIVE CLINIC QUEUE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = MedicalBluePrimary,
                                fontSize = 12.sp,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dr. Priya Mehta • Room 2",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MedicalNavy,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Sunrise Multi-Speciality Hospital",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MedicalNavy,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )
                }

                StatusBadge(status = queueStatus)
            }
        }

        // ITEM 2: HERO LIVE TOKEN CARD (Ultra clear, bold, high-contrast)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_token_card"),
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                shadowElevation = 5.dp,
                border = BorderStroke(
                    if (queueStatus == QueueStatus.CALLED) 3.dp else 2.dp,
                    if (queueStatus == QueueStatus.CALLED) Color(0xFF10B981) else MedicalBluePrimary
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "YOUR LIVE TOKEN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MedicalNavy,
                            fontSize = 13.sp,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = myToken,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (queueStatus == QueueStatus.CALLED) Color(0xFF10B981) else MedicalBluePrimary,
                            fontSize = 58.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real-time Dynamic Metrics Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when (queueStatus) {
                                    QueueStatus.CALLED -> Color(0xFFDCFCE7)
                                    QueueStatus.ALMOST_YOUR_TURN -> Color(0xFFFFEDD5)
                                    else -> Color(0xFFEBF2FE)
                                }
                            )
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnimatedContent(targetState = aheadCount, label = "ahead_counter") { targetAhead ->
                                Text(
                                    text = if (queueStatus == QueueStatus.CALLED) "0" else "$targetAhead",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = if (queueStatus == QueueStatus.CALLED) Color(0xFF15803D) else MedicalNavy,
                                        fontSize = 32.sp
                                    )
                                )
                            }
                            Text(
                                text = if (aheadCount == 1) "PERSON AHEAD" else "PEOPLE AHEAD",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MedicalNavy,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .height(42.dp)
                                .width(2.dp),
                            color = Color(0xFFCBD5E1)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnimatedContent(targetState = estWait, label = "est_wait_counter") { targetWait ->
                                Text(
                                    text = if (queueStatus == QueueStatus.CALLED) "NOW" else "~$targetWait MIN",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = when {
                                            queueStatus == QueueStatus.CALLED -> Color(0xFF15803D)
                                            aheadCount <= 1 -> Color(0xFFB45309)
                                            else -> MedicalBluePrimary
                                        },
                                        fontSize = 32.sp
                                    )
                                )
                            }
                            Text(
                                text = "ESTIMATED WAIT",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MedicalNavy,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Urgent Alert Banner if CALLED
                    if (queueStatus == QueueStatus.CALLED) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF15803D)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PLEASE PROCEED TO ROOM 2 NOW",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else if (aheadCount <= 1 && queueStatus != QueueStatus.COMPLETED) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(1.5.dp, Color(0xFFF59E0B))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationImportant,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "YOUR TURN IS APPROACHING! Be near Room 2.",
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // ITEM 3: STEP PROGRESS INDICATOR (High contrast, clearly visible)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, MedicalBorder),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUEUE PROGRESSION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = MedicalNavy,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val stages = listOf(
                        Triple(QueueStatus.JOINED, "Joined", Icons.Default.Check),
                        Triple(QueueStatus.WAITING, "Waiting", Icons.Default.HourglassBottom),
                        Triple(QueueStatus.ALMOST_YOUR_TURN, "Almost Turn", Icons.Default.NotificationImportant),
                        Triple(QueueStatus.WITH_DOCTOR, "With Doctor", Icons.Default.MeetingRoom),
                        Triple(QueueStatus.COMPLETED, "Completed", Icons.Default.DoneAll)
                    )

                    val activeIndex = when (queueStatus) {
                        QueueStatus.JOINED -> 0
                        QueueStatus.WAITING -> 1
                        QueueStatus.ALMOST_YOUR_TURN -> 2
                        QueueStatus.CALLED, QueueStatus.WITH_DOCTOR -> 3
                        QueueStatus.COMPLETED -> 4
                        else -> 1
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        stages.forEachIndexed { index, (stage, label, icon) ->
                            val isDone = index <= activeIndex
                            val isCurrent = index == activeIndex

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(62.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCurrent -> MedicalBluePrimary
                                                isDone -> Color(0xFF10B981)
                                                else -> Color(0xFFE2E8F0)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isDone || isCurrent) Color.White else Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                                        color = if (isCurrent) MedicalBluePrimary else if (isDone) MedicalNavy else Color(0xFF64748B)
                                    )
                                )
                            }

                            if (index < stages.size - 1) {
                                Divider(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(3.dp)
                                        .padding(bottom = 18.dp),
                                    color = if (index < activeIndex) Color(0xFF10B981) else Color(0xFFCBD5E1)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ITEM 4: HACKATHON LIVE QUEUE SIMULATOR CONTROL BAR
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFEBF2FE),
                border = BorderStroke(1.5.dp, MedicalBluePrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Live Test Step:",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = MedicalNavy
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.callNextPatient("doc_priya") },
                            modifier = Modifier
                                .testTag("btn_demo_advance_queue")
                                .height(40.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary)
                        ) {
                            Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Next ⏩", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetDemoQueue() },
                            modifier = Modifier
                                .testTag("btn_demo_reset_queue")
                                .height(40.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, MedicalNavy.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Reset",
                                modifier = Modifier.size(16.dp),
                                tint = MedicalNavy
                            )
                        }
                    }
                }
            }
        }

        // ITEM 5: SECTION TITLE: Patients in Doctor's Queue
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Patients in Queue (${priyaQueue.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MedicalNavy,
                        fontSize = 17.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEBF2FE)
                ) {
                    Text(
                        text = "ROOM 2",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MedicalBluePrimary
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // ITEMS 6+: INDIVIDUAL QUEUE PATIENT CARDS
        items(priyaQueue) { patient ->
            val isMe = patient.tokenNumber == myToken
            val pStatus = try { QueueStatus.valueOf(patient.status) } catch (e: Exception) { QueueStatus.WAITING }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("queue_patient_row_${patient.tokenNumber}"),
                shape = RoundedCornerShape(16.dp),
                color = if (isMe) Color(0xFFEBF2FE) else Color.White,
                border = BorderStroke(
                    if (isMe) 2.dp else 1.dp,
                    if (isMe) MedicalBluePrimary else MedicalBorder
                ),
                shadowElevation = if (isMe) 3.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isMe) MedicalBluePrimary else Color(0xFFE0EDFF)
                        ) {
                            Text(
                                text = patient.tokenNumber,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (isMe) Color.White else MedicalBlueDark,
                                    fontSize = 17.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isMe) "YOU (${patient.patientName})" else patient.patientName,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isMe) FontWeight.Black else FontWeight.Bold,
                                        color = if (isMe) MedicalBluePrimary else MedicalNavy,
                                        fontSize = 15.sp
                                    )
                                )
                                if (isMe) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MedicalBluePrimary
                                    ) {
                                        Text(
                                            text = "MY TURN",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black
                                            ),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = patient.chiefComplaint,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MedicalNavy,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(status = pStatus)
                }
            }
        }
    }
}
