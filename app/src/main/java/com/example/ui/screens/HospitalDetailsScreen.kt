package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.Doctor
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun HospitalDetailsScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val hospital = viewModel.selectedHospital.collectAsState().value ?: viewModel.repository.hospitals.first()
    val allDoctors = remember(hospital) { viewModel.repository.getDoctorsForHospital(hospital.id) }
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Doctors", "Facilities", "Reviews")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // Header Image Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                val imageRes = context.resources.getIdentifier(
                    hospital.imageDrawableName, "drawable", context.packageName
                )
                if (imageRes != 0) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = hospital.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MedicalBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.White, modifier = Modifier.size(54.dp))
                    }
                }

                // Top badges & Close/Back Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Close / Back button to remove overview
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .clickable { viewModel.navigateBack() }
                            .testTag("btn_close_hospital_overview")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Close Overview", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LiveGreen
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ACCREDITED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.65f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${hospital.rating} (${hospital.reviewsCount})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Name & Type
                Text(
                    text = hospital.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MedicalNavy,
                        fontSize = 21.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(18.dp))
                    Text(
                        text = "${hospital.address} • ${hospital.city}, ${hospital.state} • ${hospital.distanceKm} km away",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MedicalNavy,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                    Text(
                        text = "OPD Desk: ${hospital.phone} (${hospital.landmark})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MedicalNavy,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Queue & OPD Stats Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, MedicalBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${hospital.waitingCount} Patients",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LiveGreen
                            )
                        )
                        Text("Current Waiting", fontSize = 10.sp, color = MedicalSlate)
                    }
                    Divider(modifier = Modifier.height(28.dp).width(1.dp), color = MedicalBorder)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "~${hospital.estWaitMinutes} min",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MedicalNavy
                            )
                        )
                        Text("Average Wait", fontSize = 10.sp, color = MedicalSlate)
                    }
                    Divider(modifier = Modifier.height(28.dp).width(1.dp), color = MedicalBorder)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (hospital.emergencyAvailable) "24/7 Active" else "OPD Only",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (hospital.emergencyAvailable) LiveGreen else WarningOrange
                            )
                        )
                        Text("Emergency", fontSize = 10.sp, color = MedicalSlate)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MedicalBluePrimary,
                    divider = { Divider(color = MedicalBorder) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
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

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        // Overview
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Operating Hours
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, MedicalBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = MedicalBluePrimary)
                                    Column {
                                        Text("Operating Hours", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MedicalNavy)
                                        Text(hospital.openingHours, fontSize = 12.sp, color = MedicalSlate)
                                    }
                                }
                            }

                            // Available Specialties Chips
                            Text("Available Specialties", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MedicalNavy)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                hospital.specialties.take(4).forEach { spec ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MedicalBlueLight
                                    ) {
                                        Text(
                                            text = spec,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MedicalBluePrimary,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Top Doctors Preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Available Doctors (${allDoctors.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MedicalNavy)
                                TextButton(onClick = { selectedTab = 1 }) {
                                    Text("See All", color = MedicalBluePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            allDoctors.take(2).forEach { doctor ->
                                DoctorCardRow(doctor = doctor, onSelect = { viewModel.selectDoctor(doctor) })
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedButton(
                                onClick = { viewModel.navigateBack() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("btn_close_overview_tab"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.5.dp, MedicalBorder)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = MedicalNavy, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Close Hospital Overview", color = MedicalNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                    1 -> {
                        // Doctors Tab
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            allDoctors.forEach { doctor ->
                                DoctorCardRow(doctor = doctor, onSelect = { viewModel.selectDoctor(doctor) })
                            }
                        }
                    }
                    2 -> {
                        // Facilities Tab
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            hospital.facilities.forEach { facility ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, MedicalBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.size(32.dp).clip(CircleShape).background(MedicalBlueLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(18.dp))
                                        }
                                        Text(facility, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MedicalNavy)
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // Reviews Tab
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf(
                                Triple("Anil Sharma", 5, "Live queue saved me 45 minutes! Arrived 5 mins before my turn and saw Dr. Priya immediately."),
                                Triple("Sarah Jenkins", 5, "Very smooth appointment booking from home. Outstanding cardiology department."),
                                Triple("Pooja Nair", 4, "Clean hospital, courteous staff, queue system is remarkably accurate.")
                            ).forEach { (author, rating, text) ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, MedicalBorder)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(author, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MedicalNavy)
                                            Row {
                                                repeat(rating) {
                                                    Icon(Icons.Default.Star, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text, fontSize = 12.sp, color = MedicalSlate)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Fixed CTA Actions
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, MedicalBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.openDirections(hospital) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("btn_hospital_get_directions"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, MedicalBluePrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalBluePrimary)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Get Directions", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val firstDoc = allDoctors.firstOrNull() ?: viewModel.repository.doctors.first()
                        viewModel.selectDoctor(firstDoc)
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(50.dp)
                        .testTag("btn_hospital_book_appointment"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary)
                ) {
                    Text("Book Appointment", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun DoctorCardRow(
    doctor: Doctor,
    onSelect: () -> Unit
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("doctor_row_${doctor.id}")
            .clickable { onSelect() },
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MedicalBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val imgRes = context.resources.getIdentifier(
                doctor.imageDrawableName, "drawable", context.packageName
            )
            if (imgRes != 0) {
                Image(
                    painter = painterResource(id = imgRes),
                    contentDescription = doctor.name,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MedicalBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(28.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedicalNavy)
                Text(doctor.title, fontSize = 11.sp, color = MedicalSlate)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = RoundedCornerShape(4.dp), color = LiveGreenLight) {
                        Text(
                            "${doctor.currentQueueSize} waiting (~${doctor.estWaitMinutes}m)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LiveGreen,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Text("Fee: ${doctor.fee}", fontSize = 11.sp, color = MedicalSlate, fontWeight = FontWeight.SemiBold)
                }
            }

            Button(
                onClick = onSelect,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Book", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
