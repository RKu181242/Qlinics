package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VisitMode
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun BookingScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val doctor = viewModel.selectedDoctor.collectAsState().value ?: viewModel.repository.doctors.first()
    val hospital = viewModel.selectedHospital.collectAsState().value ?: viewModel.repository.hospitals.first()

    val bookingDate by viewModel.bookingDate.collectAsState()
    val bookingTime by viewModel.bookingTime.collectAsState()
    val bookingMode by viewModel.bookingVisitMode.collectAsState()

    // 7 Days Calendar Options
    val calendarDays = remember {
        val list = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val fullFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        for (i in 0..6) {
            val d = cal.time
            val dayName = if (i == 0) "Today" else dayFormat.format(d)
            val dateLabel = fullFormat.format(d)
            list.add(Pair(dayName, dateLabel))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    // Time Slots with status
    data class SlotInfo(val time: String, val slotsLeft: Int, val state: String)
    val timeSlots = remember {
        listOf(
            SlotInfo("09:00 AM", 3, "Available"),
            SlotInfo("09:30 AM", 2, "Limited"),
            SlotInfo("10:00 AM", 5, "Available"),
            SlotInfo("10:30 AM", 1, "Limited"),
            SlotInfo("11:00 AM", 4, "Available"),
            SlotInfo("11:30 AM", 0, "Full"),
            SlotInfo("02:00 PM", 6, "Available"),
            SlotInfo("02:30 PM", 2, "Limited"),
            SlotInfo("03:00 PM", 4, "Available")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Info
            Column {
                Text(
                    text = "Book from Home",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MedicalNavy
                    )
                )
                Text(
                    text = "Confirm doctor consultation without waiting in line at the clinic.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MedicalSlate,
                        fontSize = 12.sp
                    )
                )
            }

            // Doctor Summary Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, MedicalBorder),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val imgRes = context.resources.getIdentifier(
                        doctor.imageDrawableName, "drawable", context.packageName
                    )
                    if (imgRes != 0) {
                        Image(
                            painter = painterResource(id = imgRes),
                            contentDescription = doctor.name,
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MedicalBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(30.dp))
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MedicalNavy)
                        Text("${doctor.specialty} • ${doctor.roomNumber}", fontSize = 12.sp, color = MedicalSlate)
                        Text(hospital.name, fontSize = 11.sp, color = MedicalBluePrimary, fontWeight = FontWeight.SemiBold)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LiveGreenLight
                    ) {
                        Text(
                            "Fee: ${doctor.fee}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = LiveGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 1. SELECT DATE (Horizontal Calendar)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "1. Select Date",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MedicalNavy
                    )
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(calendarDays) { (dayLabel, fullDate) ->
                        val isSelected = bookingDate == fullDate
                        Surface(
                            modifier = Modifier
                                .testTag("date_chip_${dayLabel.lowercase()}")
                                .clickable { viewModel.updateBookingDate(fullDate) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MedicalBluePrimary else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) MedicalBluePrimary else MedicalBorder),
                            shadowElevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(78.dp)
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dayLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MedicalSlate
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = fullDate.substringBefore(",").substringAfter(" "),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MedicalNavy
                                )
                                Text(
                                    text = fullDate.substringBefore(" "),
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MedicalSlate
                                )
                            }
                        }
                    }
                }
            }

            // 2. SELECT TIME SLOT
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. Select Time Slot",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MedicalNavy
                        )
                    )
                    Text("Selected: $bookingTime", fontSize = 12.sp, color = MedicalBluePrimary, fontWeight = FontWeight.Bold)
                }

                // Grid of Time Slots
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    timeSlots.chunked(3).forEach { rowSlots ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowSlots.forEach { slot ->
                                val isSelected = bookingTime == slot.time
                                val isFull = slot.state == "Full"

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("slot_${slot.time.replace(" ", "_")}")
                                        .clickable(enabled = !isFull) { viewModel.updateBookingTime(slot.time) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = when {
                                        isSelected -> MedicalBluePrimary
                                        isFull -> Color(0xFFF1F5F9)
                                        else -> Color.White
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        when {
                                            isSelected -> MedicalBluePrimary
                                            isFull -> Color.Transparent
                                            else -> MedicalBorder
                                        }
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = slot.time,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = when {
                                                isSelected -> Color.White
                                                isFull -> MedicalMuted
                                                else -> MedicalNavy
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isFull) "Full" else "${slot.slotsLeft} left",
                                            fontSize = 10.sp,
                                            color = when {
                                                isSelected -> Color.White.copy(alpha = 0.8f)
                                                slot.state == "Limited" -> WarningOrange
                                                isFull -> MedicalMuted
                                                else -> LiveGreen
                                            },
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                            if (rowSlots.size < 3) {
                                repeat(3 - rowSlots.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // 3. TWO VISIT MODES
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "3. Choose Visit Mode",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MedicalNavy
                    )
                )

                // Option A: LIVE QUEUE (Recommended)
                val isLiveQueue = bookingMode == VisitMode.LIVE_QUEUE
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mode_live_queue")
                        .clickable { viewModel.updateBookingMode(VisitMode.LIVE_QUEUE) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isLiveQueue) MedicalBlueLight.copy(alpha = 0.5f) else Color.White,
                    border = BorderStroke(
                        if (isLiveQueue) 2.dp else 1.dp,
                        if (isLiveQueue) MedicalBluePrimary else MedicalBorder
                    ),
                    shadowElevation = if (isLiveQueue) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = isLiveQueue,
                            onClick = { viewModel.updateBookingMode(VisitMode.LIVE_QUEUE) },
                            colors = RadioButtonDefaults.colors(selectedColor = MedicalBluePrimary)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Live Queue (Recommended)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MedicalNavy,
                                        fontSize = 15.sp
                                    )
                                )
                                Surface(shape = RoundedCornerShape(4.dp), color = LiveGreenLight) {
                                    Text("NO WAITING IN LINE", color = LiveGreen, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Join the live virtual queue from home. Receive live token, track people ahead in real time, and arrive at the clinic right when your turn approaches.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MedicalSlate,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }

                // Option B: FIXED APPOINTMENT
                val isFixed = bookingMode == VisitMode.FIXED
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mode_fixed_appointment")
                        .clickable { viewModel.updateBookingMode(VisitMode.FIXED) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isFixed) MedicalBlueLight.copy(alpha = 0.5f) else Color.White,
                    border = BorderStroke(
                        if (isFixed) 2.dp else 1.dp,
                        if (isFixed) MedicalBluePrimary else MedicalBorder
                    ),
                    shadowElevation = if (isFixed) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = isFixed,
                            onClick = { viewModel.updateBookingMode(VisitMode.FIXED) },
                            colors = RadioButtonDefaults.colors(selectedColor = MedicalBluePrimary)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Fixed Appointment Time",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalNavy,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Traditional scheduled time slot. Arrive 15 minutes before your scheduled $bookingTime appointment.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MedicalSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Fixed Confirm Booking CTA
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, MedicalBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { viewModel.confirmBooking() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_confirm_booking_submit"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (bookingMode == VisitMode.LIVE_QUEUE) "Join Live Queue & Confirm" else "Confirm Appointment",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}
