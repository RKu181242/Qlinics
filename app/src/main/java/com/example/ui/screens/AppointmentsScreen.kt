package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.Appointment
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AppointmentsScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val appointments by viewModel.appointments.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Upcoming", "Completed", "Cancelled")

    var appointmentToReschedule by remember { mutableStateOf<Appointment?>(null) }
    var rescheduleDate by remember { mutableStateOf("") }
    var rescheduleTime by remember { mutableStateOf("11:00 AM") }

    val filteredList = remember(appointments, selectedTab) {
        when (selectedTab) {
            0 -> appointments.filter { it.status != "COMPLETED" && it.status != "CANCELLED" }
            1 -> appointments.filter { it.status == "COMPLETED" }
            2 -> appointments.filter { it.status == "CANCELLED" }
            else -> appointments
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "My Appointments",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MedicalNavy
            )
        )
        Text(
            text = "Track clinic bookings, live queue tokens, and consultation history",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MedicalSlate,
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MedicalBluePrimary,
            divider = { Divider(color = MedicalBorder) }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.testTag("tab_appointments_${title.lowercase()}"),
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedTab == index) MedicalBluePrimary else MedicalSlate
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = MedicalMuted,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No ${tabTitles[selectedTab].lowercase()} appointments.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MedicalSlate)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { appt ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("appointment_card_${appt.id}"),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, MedicalBorder),
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MedicalBlueLight
                                ) {
                                    Text(
                                        text = "ID: ${appt.id}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MedicalBluePrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = when (appt.status) {
                                        "COMPLETED" -> Color(0xFFDCFCE7)
                                        "CANCELLED" -> DangerRedLight
                                        else -> LiveGreenLight
                                    }
                                ) {
                                    Text(
                                        text = appt.status,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = when (appt.status) {
                                                "COMPLETED" -> Color(0xFF15803D)
                                                "CANCELLED" -> DangerRed
                                                else -> LiveGreen
                                            },
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = appt.doctorName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MedicalNavy,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = "${appt.specialty} • ${appt.hospitalName}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MedicalSlate,
                                            fontSize = 12.sp
                                        )
                                    )
                                }

                                if (appt.visitMode == "LIVE_QUEUE") {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MedicalBlueLight
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("TOKEN", fontSize = 8.sp, color = MedicalSlate, fontWeight = FontWeight.Bold)
                                            Text(appt.tokenNumber, fontSize = 16.sp, color = MedicalBluePrimary, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                }
                            }

                            Divider(color = MedicalBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(15.dp))
                                    Text(appt.date, fontSize = 12.sp, color = MedicalNavy, fontWeight = FontWeight.SemiBold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(15.dp))
                                    Text(appt.timeSlot, fontSize = 12.sp, color = MedicalNavy, fontWeight = FontWeight.SemiBold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = MedicalSlate, modifier = Modifier.size(15.dp))
                                    Text(appt.roomNumber, fontSize = 12.sp, color = MedicalNavy)
                                }
                            }

                            // Action buttons (Only for upcoming active appointments)
                            if (appt.status != "COMPLETED" && appt.status != "CANCELLED") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (appt.visitMode == "LIVE_QUEUE") {
                                        Button(
                                            onClick = { viewModel.navigateTo(Screen.LIVE_QUEUE) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .testTag("btn_track_queue_${appt.id}"),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary)
                                        ) {
                                            Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Track Queue", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            appointmentToReschedule = appt
                                            rescheduleDate = appt.date
                                            rescheduleTime = appt.timeSlot
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("btn_reschedule_${appt.id}"),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, MedicalBorder)
                                    ) {
                                        Text("Reschedule", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MedicalNavy)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.cancelAppointment(appt.id) },
                                        modifier = Modifier
                                            .height(40.dp)
                                            .testTag("btn_cancel_${appt.id}"),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, DangerRedLight),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
                                    ) {
                                        Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Reschedule Dialog
    appointmentToReschedule?.let { appt ->
        AlertDialog(
            onDismissRequest = { appointmentToReschedule = null },
            title = {
                Text("Reschedule Appointment", fontWeight = FontWeight.Bold, color = MedicalNavy)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select a new date and time slot for ${appt.doctorName}:", fontSize = 13.sp, color = MedicalSlate)

                    // Quick Dates
                    val cal = Calendar.getInstance()
                    val fmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val dates = (1..3).map {
                        cal.add(Calendar.DAY_OF_YEAR, 1)
                        fmt.format(cal.time)
                    }

                    Text("New Date:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        dates.forEach { d ->
                            val isSel = rescheduleDate == d
                            Surface(
                                modifier = Modifier
                                    .clickable { rescheduleDate = d },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MedicalBluePrimary else MedicalBlueSubtle,
                                border = BorderStroke(1.dp, if (isSel) MedicalBluePrimary else MedicalBorder)
                            ) {
                                Text(
                                    text = d.substringBefore(","),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MedicalNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Text("New Time Slot:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("10:00 AM", "11:30 AM", "02:00 PM").forEach { t ->
                            val isSel = rescheduleTime == t
                            Surface(
                                modifier = Modifier
                                    .clickable { rescheduleTime = t },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MedicalBluePrimary else MedicalBlueSubtle,
                                border = BorderStroke(1.dp, if (isSel) MedicalBluePrimary else MedicalBorder)
                            ) {
                                Text(
                                    text = t,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MedicalNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rescheduleAppointment(appt.id, rescheduleDate.ifEmpty { appt.date }, rescheduleTime)
                        appointmentToReschedule = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary)
                ) {
                    Text("Confirm Reschedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { appointmentToReschedule = null }) {
                    Text("Close")
                }
            }
        )
    }
}
