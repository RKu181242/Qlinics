package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.QueueStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel

@Composable
fun DoctorDashboardScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val priyaQueue by viewModel.priyaQueue.collectAsState()

    // Find current patient (WITH_DOCTOR or CALLED)
    val currentPatient = priyaQueue.find {
        it.status == QueueStatus.WITH_DOCTOR.name || it.status == QueueStatus.CALLED.name
    } ?: priyaQueue.firstOrNull { it.status == QueueStatus.WAITING.name }

    // Next patient
    val nextPatient = priyaQueue.filter {
        it.status == QueueStatus.WAITING.name || it.status == QueueStatus.ALMOST_YOUR_TURN.name
    }.getOrNull(if (currentPatient?.status == QueueStatus.WAITING.name) 1 else 0)

    val isCurrentWithDoctor = currentPatient?.status == QueueStatus.WITH_DOCTOR.name

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Doctor Profile Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, MedicalBorder),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val imgRes = context.resources.getIdentifier(
                    "doctor_priya", "drawable", context.packageName
                )
                if (imgRes != 0) {
                    Image(
                        painter = painterResource(id = imgRes),
                        contentDescription = "Dr. Priya Mehta",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MedicalBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(32.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dr. Priya Mehta",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MedicalNavy
                        )
                    )
                    Text(
                        text = "Senior Cardiologist • Room 2",
                        style = MaterialTheme.typography.bodySmall.copy(color = MedicalSlate)
                    )
                    Text(
                        text = "Sunrise Multi-Speciality Hospital",
                        style = MaterialTheme.typography.bodySmall.copy(color = MedicalBluePrimary, fontWeight = FontWeight.SemiBold)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LiveGreenLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LiveGreen))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ON DUTY", color = LiveGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }

        // CURRENT PATIENT CARD
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_doctor_current_patient"),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            border = BorderStroke(1.5.dp, if (isCurrentWithDoctor) LiveGreen else MedicalBluePrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MeetingRoom,
                            contentDescription = null,
                            tint = if (isCurrentWithDoctor) LiveGreen else MedicalBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CURRENT PATIENT IN ROOM 2",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrentWithDoctor) LiveGreen else MedicalBluePrimary,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    if (currentPatient != null) {
                        val status = try { QueueStatus.valueOf(currentPatient.status) } catch (e: Exception) { QueueStatus.WAITING }
                        StatusBadge(status = status)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (currentPatient != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MedicalBlueLight
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("TOKEN", fontSize = 9.sp, color = MedicalSlate, fontWeight = FontWeight.Bold)
                                Text(
                                    text = currentPatient.tokenNumber,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MedicalBluePrimary
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentPatient.patientName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalNavy,
                                    fontSize = 17.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Complaint: ${currentPatient.chiefComplaint}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MedicalSlate,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "Contact: ${currentPatient.patientPhone}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MedicalBluePrimary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons (Start Consultation vs Complete Consultation)
                    if (isCurrentWithDoctor) {
                        Button(
                            onClick = {
                                viewModel.doctorCompleteConsultation(currentPatient.tokenNumber)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_doctor_complete_consultation"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LiveGreen)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Complete Consultation ✓", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.doctorStartConsultation(currentPatient.tokenNumber)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_doctor_start_consultation"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Consultation", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                } else {
                    Text("No patients currently in consultation.", color = MedicalMuted, fontSize = 13.sp)
                }
            }
        }

        // NEXT PATIENT CARD
        if (nextPatient != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_doctor_next_patient"),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, MedicalBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "NEXT PATIENT UP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MedicalSlate,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MedicalBlueSubtle
                            ) {
                                Text(
                                    nextPatient.tokenNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MedicalNavy,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Column {
                                Text(nextPatient.patientName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedicalNavy)
                                Text("Complaint: ${nextPatient.chiefComplaint}", fontSize = 11.sp, color = MedicalSlate)
                            }
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFEF3C7)) {
                            Text(
                                "Est. ~9 min",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningOrange,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // FULL QUEUE LIST FOR THIS DOCTOR
        Text(
            text = "Today's Clinic Queue (${priyaQueue.size} Total)",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MedicalNavy
            )
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            priyaQueue.forEach { patient ->
                val pStatus = try { QueueStatus.valueOf(patient.status) } catch (e: Exception) { QueueStatus.WAITING }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, MedicalBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(shape = RoundedCornerShape(6.dp), color = MedicalBlueSubtle) {
                                Text(patient.tokenNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MedicalNavy, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Column {
                                Text(patient.patientName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MedicalNavy)
                                Text(patient.chiefComplaint, fontSize = 10.sp, color = MedicalSlate)
                            }
                        }
                        StatusBadge(status = pStatus)
                    }
                }
            }
        }
    }
}
