package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QueueStatus
import com.example.data.model.Specialist
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun PatientHomeScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val homeSearch by viewModel.homeSearchQuery.collectAsState()
    val queueCalculations by viewModel.queueCalculations.collectAsState()
    val (aheadCount, estWait, queueStatus) = queueCalculations
    val allSpecialists = viewModel.repository.specialists

    val filteredSpecialists = remember(homeSearch, allSpecialists) {
        if (homeSearch.isBlank()) allSpecialists
        else allSpecialists.filter {
            it.name.contains(homeSearch, ignoreCase = true) ||
                    it.description.contains(homeSearch, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Patient Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good Morning, Rehan 👋",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = MedicalNavy,
                        fontSize = 24.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Take control of your healthcare, your way.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MedicalNavy,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEBF2FE))
                    .border(2.5.dp, MedicalBluePrimary, CircleShape)
                    .clickable { viewModel.navigateTo(Screen.PROFILE) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = MedicalBluePrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = homeSearch,
            onValueChange = { viewModel.setHomeSearch(it) },
            placeholder = {
                Text(
                    "Search specialist, doctor, hospital...",
                    color = MedicalSlate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MedicalBluePrimary,
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon = {
                if (homeSearch.isNotBlank()) {
                    IconButton(onClick = { viewModel.setHomeSearch("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MedicalNavy, modifier = Modifier.size(22.dp))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_home_search"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = MedicalBluePrimary,
                unfocusedBorderColor = MedicalBorder
            ),
            singleLine = true
        )

        // INDIAN CITY SELECTOR ROW
        val selectedCity by viewModel.selectedCity.collectAsState()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEBF2FE)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location",
                        tint = MedicalBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "City",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = MedicalBluePrimary
                    )
                }
            }

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.repository.indianCities) { city ->
                    val isCitySelected = selectedCity == city
                    Surface(
                        modifier = Modifier
                            .testTag("home_city_${city.lowercase().replace(" ", "_")}")
                            .clickable { viewModel.selectCity(city) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCitySelected) MedicalBluePrimary else Color.White,
                        border = BorderStroke(1.dp, if (isCitySelected) MedicalBluePrimary else MedicalBorder),
                        shadowElevation = if (isCitySelected) 2.dp else 1.dp
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isCitySelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isCitySelected) Color.White else MedicalNavy,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
        if (queueStatus != QueueStatus.COMPLETED && queueStatus != QueueStatus.CANCELLED) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_active_queue_home"),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(2.dp, if (aheadCount <= 1) Color(0xFFD97706) else MedicalBluePrimary)
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
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACTIVE LIVE QUEUE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MedicalBluePrimary,
                                    letterSpacing = 1.2.sp,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        StatusBadge(status = queueStatus)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dr. Priya Mehta",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MedicalNavy,
                                    fontSize = 19.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cardiology • Sunrise Hospital",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MedicalNavy,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            )
                        }

                        // Token Box
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFEBF2FE),
                            border = BorderStroke(1.5.dp, MedicalBluePrimary)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "YOUR TOKEN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MedicalNavy,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                )
                                Text(
                                    text = viewModel.patientToken,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = MedicalBluePrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Queue Metrics Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF1F6FE))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = MedicalBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$aheadCount people ahead",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MedicalNavy,
                                    fontSize = 15.sp
                                )
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .height(26.dp)
                                .width(2.dp),
                            color = Color(0xFFCBD5E1)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "~$estWait min wait",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MedicalNavy,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.navigateTo(Screen.LIVE_QUEUE) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_track_live_queue_home"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedicalBluePrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
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
            }
        }

        // Two Primary Action Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card 1: Book Appointment from Home
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_action_book_home")
                    .clickable { viewModel.navigateTo(Screen.SPECIALIST_SEARCH) },
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, MedicalBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE0EDFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = null,
                            tint = MedicalBluePrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Book from Home",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = MedicalNavy,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pick specialist & doctor in minutes.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MedicalNavy,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            // Card 2: Find Nearby Hospitals
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_action_find_hospitals")
                    .clickable { viewModel.navigateTo(Screen.HOSPITAL_MAP) },
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, MedicalBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Nearby Hospitals",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = MedicalNavy,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Live queue & distance map.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MedicalNavy,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        // Specialist Grid Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Medical Specialties",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = MedicalNavy,
                        fontSize = 19.sp
                    )
                )
                Text(
                    text = "Select a specialty for instant smart recommendations",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MedicalNavy,
                        fontSize = 13.sp
                    )
                )
            }
            TextButton(
                onClick = { viewModel.navigateTo(Screen.SPECIALIST_SEARCH) },
                modifier = Modifier.testTag("btn_view_all_specialists")
            ) {
                Text(
                    "View All",
                    color = MedicalBluePrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }

        // Specialties Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredSpecialists.take(8).chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { specialist ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("specialist_card_${specialist.name.lowercase()}")
                                .clickable { viewModel.selectSpecialist(specialist) },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, MedicalBorder),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFEBF2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getSpecialistIcon(specialist.iconName),
                                        contentDescription = specialist.name,
                                        tint = MedicalBluePrimary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = specialist.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = MedicalNavy,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${specialist.doctorCount} Doctors",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF15803D),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                }
                            }
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Demo Flow Assist Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFEBF2FE),
            border = BorderStroke(1.5.dp, MedicalBluePrimary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TipsAndUpdates,
                    contentDescription = null,
                    tint = MedicalBluePrimary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "Tip: You can switch between Patient, Receptionist, and Doctor roles at any time using the top role switcher.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MedicalNavy,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

fun getSpecialistIcon(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "Favorite" -> Icons.Default.Favorite
        "Face" -> Icons.Default.Face
        "Accessibility" -> Icons.Default.Accessibility
        "Visibility" -> Icons.Default.Visibility
        "ChildCare" -> Icons.Default.ChildCare
        "Psychology" -> Icons.Default.Psychology
        "PregnantWoman" -> Icons.Default.PregnantWoman
        "MedicalServices" -> Icons.Default.MedicalServices
        "Air" -> Icons.Default.Air
        "Spa" -> Icons.Default.Spa
        "Hearing" -> Icons.Default.Hearing
        "LocalPharmacy" -> Icons.Default.LocalPharmacy
        "Bloodtype" -> Icons.Default.Bloodtype
        "Biotech" -> Icons.Default.Biotech
        "Sanitizer" -> Icons.Default.Sanitizer
        "Healing" -> Icons.Default.Healing
        "SelfImprovement" -> Icons.Default.SelfImprovement
        else -> Icons.Default.LocalHospital
    }
}
