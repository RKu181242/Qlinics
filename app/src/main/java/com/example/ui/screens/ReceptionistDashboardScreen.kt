package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QueuePatient
import com.example.data.model.QueueStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceptionistDashboardScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val priyaQueue by viewModel.priyaQueue.collectAsState()
    val todayDate = remember { SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(Date()) }

    var showAddPatientDialog by remember { mutableStateOf(false) }
    var newPatientName by remember { mutableStateOf("") }
    var newPatientPhone by remember { mutableStateOf("") }
    var newPatientComplaint by remember { mutableStateOf("") }

    val currentWithDoc = priyaQueue.find { it.status == QueueStatus.WITH_DOCTOR.name }
    val waitingCount = priyaQueue.count { it.status == QueueStatus.WAITING.name || it.status == QueueStatus.ALMOST_YOUR_TURN.name }
    val completedCount = priyaQueue.count { it.status == QueueStatus.COMPLETED.name }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dashboard Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Clinic Dashboard",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MedicalNavy
                    )
                )
                Text(
                    text = "Sunrise Multi-Speciality Hospital • $todayDate",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MedicalSlate,
                        fontSize = 12.sp
                    )
                )
            }

            Button(
                onClick = { showAddPatientDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary),
                modifier = Modifier.testTag("btn_receptionist_add_patient")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Patient", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // TOP CLINIC STATISTICS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                Triple("48", "Total Patients", MedicalBluePrimary),
                Triple("${waitingCount + 14}", "Waiting", WarningOrange),
                Triple("${if (currentWithDoc != null) 15 else 14}", "With Doctor", LiveGreen),
                Triple("${completedCount + 12}", "Completed", Color(0xFF6366F1))
            ).forEach { (num, label, color) ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stat_${label.lowercase().replace(" ", "_")}"),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, MedicalBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = num,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = color
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = MedicalSlate,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // CLINIC ALERTS BANNER (High Wait Time & Alternative Available)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Alert 1: High Wait Time
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = WarningOrangeLight,
                border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HIGH WAIT TIME ALERT: Orthopedics (Dr. Vikram Rao)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFB45309)
                        )
                        Text(
                            text = "11 patients in queue (~42 min wait). Receptionist advised to offer telemedicine triage.",
                            fontSize = 11.sp,
                            color = MedicalNavy
                        )
                    }
                }
            }

            // Alert 2: Alternative Available
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = LiveGreenLight,
                border = BorderStroke(1.dp, LiveGreen.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LiveGreen, modifier = Modifier.size(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ALTERNATIVE AVAILABLE: Cardiology OPD",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF047857)
                        )
                        Text(
                            text = "Dr. Sandeep Reddy in Room 3 has short wait (3 patients, ~9 min). Patients can cross-consult.",
                            fontSize = 11.sp,
                            color = MedicalNavy
                        )
                    }
                }
            }
        }

        // ALL DOCTORS LIVE QUEUE OVERVIEW TABLE
        Text(
            text = "LIVE QUEUE — ALL DOCTORS",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MedicalNavy,
                letterSpacing = 0.5.sp
            )
        )

        val doctorQueuesOverview = listOf(
            Triple("Cardiology", "Dr. Priya Mehta (Room 2)", "8 waiting • 1 consulting • Active • 18 min"),
            Triple("Dentistry", "Dr. Ananya Singh (Room 5)", "4 waiting • 1 consulting • Active • 10 min"),
            Triple("Orthopedics", "Dr. Vikram Rao (Room 6)", "11 waiting • 2 consulting • Busy • 42 min"),
            Triple("Cardiology", "Dr. Rajesh Sharma (Room 4)", "4 waiting • 1 consulting • Active • 12 min")
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            doctorQueuesOverview.forEach { (specialty, doc, metrics) ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, MedicalBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(specialty, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MedicalBluePrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(LiveGreen))
                            }
                            Text(doc, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MedicalNavy)
                            Text(metrics, fontSize = 11.sp, color = MedicalSlate)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MedicalBlueLight
                        ) {
                            Text("ACTIVE", color = MedicalBluePrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // RECEPTIONIST CONTROLS FOR DR. PRIYA'S QUEUE
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dr. Priya Mehta Queue Actions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MedicalNavy
                )
            )

            Button(
                onClick = { viewModel.callNextPatient("doc_priya") },
                modifier = Modifier
                    .testTag("btn_receptionist_call_next")
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary)
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("CALL NEXT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Patients in Dr. Priya's Queue with individual Action Buttons (Recall, Skip, Complete)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            priyaQueue.forEach { patient ->
                val pStatus = try { QueueStatus.valueOf(patient.status) } catch (e: Exception) { QueueStatus.WAITING }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, MedicalBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(shape = RoundedCornerShape(6.dp), color = MedicalBlueLight) {
                                    Text(patient.tokenNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedicalBluePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                                Column {
                                    Text(patient.patientName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedicalNavy)
                                    Text(patient.chiefComplaint, fontSize = 11.sp, color = MedicalSlate)
                                }
                            }
                            StatusBadge(status = pStatus)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Controls Bar for each patient
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { viewModel.recallPatient(patient.tokenNumber) },
                                modifier = Modifier.testTag("btn_recall_${patient.tokenNumber}")
                            ) {
                                Text("Recall", fontSize = 12.sp, color = MedicalBluePrimary, fontWeight = FontWeight.SemiBold)
                            }

                            TextButton(
                                onClick = { viewModel.skipPatient(patient.tokenNumber) },
                                modifier = Modifier.testTag("btn_skip_${patient.tokenNumber}")
                            ) {
                                Text("Skip", fontSize = 12.sp, color = WarningOrange, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = { viewModel.callNextPatient("doc_priya") },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LiveGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_advance_${patient.tokenNumber}")
                            ) {
                                Text("Advance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Walk-in Patient Dialog
    if (showAddPatientDialog) {
        AlertDialog(
            onDismissRequest = { showAddPatientDialog = false },
            title = { Text("Add Walk-in Patient", fontWeight = FontWeight.Bold, color = MedicalNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newPatientName,
                        onValueChange = { newPatientName = it },
                        label = { Text("Patient Name") },
                        modifier = Modifier.fillMaxWidth().testTag("input_new_patient_name")
                    )
                    OutlinedTextField(
                        value = newPatientPhone,
                        onValueChange = { newPatientPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth().testTag("input_new_patient_phone")
                    )
                    OutlinedTextField(
                        value = newPatientComplaint,
                        onValueChange = { newPatientComplaint = it },
                        label = { Text("Chief Complaint") },
                        modifier = Modifier.fillMaxWidth().testTag("input_new_patient_complaint")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPatientName.isNotBlank()) {
                            viewModel.addWalkInPatient("doc_priya", newPatientName, newPatientPhone, newPatientComplaint)
                            showAddPatientDialog = false
                            newPatientName = ""
                            newPatientPhone = ""
                            newPatientComplaint = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary),
                    modifier = Modifier.testTag("btn_submit_add_patient")
                ) {
                    Text("Add to Queue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPatientDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
