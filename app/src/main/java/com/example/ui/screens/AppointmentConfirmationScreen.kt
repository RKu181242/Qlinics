package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun AppointmentConfirmationScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val appt = viewModel.lastBookedAppointment.collectAsState().value
        ?: viewModel.appointments.collectAsState().value.firstOrNull()

    val doctorName = appt?.doctorName ?: "Dr. Priya Mehta"
    val specialty = appt?.specialty ?: "Cardiology"
    val hospitalName = appt?.hospitalName ?: "Sunrise Multi-Speciality Hospital"
    val date = appt?.date ?: "Today"
    val time = appt?.timeSlot ?: "10:30 AM"
    val appointmentId = appt?.id ?: "CQ-4821"
    val tokenNumber = appt?.tokenNumber ?: "A-27"
    val isLiveQueue = appt?.visitMode == "LIVE_QUEUE"

    val queueCalculations by viewModel.queueCalculations.collectAsState()
    val (aheadCount, estWait, _) = queueCalculations
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Success Checkmark Circle
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(LiveGreenLight)
                .border(2.dp, LiveGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = LiveGreen,
                modifier = Modifier.size(42.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Appointment Confirmed",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MedicalNavy,
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Appointment ID: $appointmentId",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MedicalBluePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            )
        }

        // Live Queue Token Highlight Box (If Live Queue)
        if (isLiveQueue) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_confirmation_token"),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(1.5.dp, MedicalBluePrimary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VIRTUAL QUEUE TOKEN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MedicalSlate,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tokenNumber,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MedicalBluePrimary,
                            fontSize = 46.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MedicalBlueSubtle)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$aheadCount",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MedicalNavy
                            )
                            Text("people ahead", fontSize = 11.sp, color = MedicalSlate)
                        }
                        Divider(modifier = Modifier.height(28.dp).width(1.dp), color = MedicalBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "~$estWait min",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = WarningOrange
                            )
                            Text("estimated wait", fontSize = 11.sp, color = MedicalSlate)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = LiveGreen, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Live notifications enabled for your turn",
                            fontSize = 12.sp,
                            color = LiveGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Appointment Details Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, MedicalBorder),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Consultation Summary",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MedicalNavy
                    )
                )

                Divider(color = MedicalBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Doctor", color = MedicalSlate, fontSize = 13.sp)
                    Text(doctorName, fontWeight = FontWeight.Bold, color = MedicalNavy, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Specialty", color = MedicalSlate, fontSize = 13.sp)
                    Text(specialty, fontWeight = FontWeight.SemiBold, color = MedicalNavy, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Hospital", color = MedicalSlate, fontSize = 13.sp)
                    Text(hospitalName, fontWeight = FontWeight.SemiBold, color = MedicalNavy, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Date & Time", color = MedicalSlate, fontSize = 13.sp)
                    Text("$date at $time", fontWeight = FontWeight.Bold, color = MedicalNavy, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Consultation Room", color = MedicalSlate, fontSize = 13.sp)
                    Text("Room 2 (Ground Floor OPD)", fontWeight = FontWeight.SemiBold, color = MedicalBluePrimary, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // CTA Buttons
        if (isLiveQueue) {
            Button(
                onClick = { viewModel.navigateTo(Screen.LIVE_QUEUE) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_confirmation_track_queue"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary)
            ) {
                Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Track Live Queue",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }

        OutlinedButton(
            onClick = { viewModel.navigateTo(Screen.HOME) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_confirmation_back_home"),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MedicalBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalNavy)
        ) {
            Text("Back to Home Dashboard", fontWeight = FontWeight.SemiBold)
        }
    }
}
