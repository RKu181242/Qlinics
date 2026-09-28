package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun HospitalRecommendationScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val recommendation by viewModel.recommendation.collectAsState()
    val selectedSpecialist by viewModel.selectedSpecialist.collectAsState()
    val scrollState = rememberScrollState()

    val specialistName = selectedSpecialist?.name ?: "Specialty"
    val rec = recommendation ?: viewModel.repository.getRecommendationForSpecialist(specialistName)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MedicalBlueLight
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MedicalBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SMART HOSPITAL RECOMMENDATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MedicalBluePrimary,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Recommended for $specialistName",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MedicalNavy
                )
            )
            Text(
                text = "Calculated via multi-factor queue optimization: queue length, doctor availability, distance, and rating.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MedicalSlate,
                    fontSize = 12.sp
                )
            )
        }

        if (rec != null) {
            val hospital = rec.hospital

            // Primary Recommendation Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_smart_recommendation"),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(1.5.dp, MedicalBluePrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Hospital Hero Photo Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
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
                                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                            }
                        }

                        // Gradient overlay & Recommended Badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MedicalBluePrimary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "TOP MATCH • 98% SCORE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = hospital.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MedicalNavy,
                                fontSize = 19.sp
                            )
                        )
                        Text(
                            text = hospital.address,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MedicalSlate,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid (Rating, Distance, Cardiologists, Waiting, Est Wait)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MedicalBlueSubtle)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${hospital.rating}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MedicalNavy)
                                }
                                Text("${hospital.reviewsCount} reviews", fontSize = 10.sp, color = MedicalSlate)
                            }

                            Divider(modifier = Modifier.height(28.dp).width(1.dp), color = MedicalBorder)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${hospital.distanceKm} km", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MedicalNavy)
                                Text("Distance", fontSize = 10.sp, color = MedicalSlate)
                            }

                            Divider(modifier = Modifier.height(28.dp).width(1.dp), color = MedicalBorder)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${rec.doctorCount}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MedicalBluePrimary)
                                Text("${specialistName}s", fontSize = 10.sp, color = MedicalSlate)
                            }

                            Divider(modifier = Modifier.height(28.dp).width(1.dp), color = MedicalBorder)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${hospital.waitingCount} waiting", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LiveGreen)
                                Text("~${hospital.estWaitMinutes} min", fontSize = 10.sp, color = MedicalSlate)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Recommendation Reasons Box
                        Text(
                            text = "Why this hospital is recommended for you:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MedicalNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            rec.reasons.forEach { reason ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(LiveGreenLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = LiveGreen, modifier = Modifier.size(13.dp))
                                    }
                                    Text(
                                        text = reason.removePrefix("✓ ").removePrefix("• "),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MedicalNavy,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Action Buttons
                        Button(
                            onClick = { viewModel.selectHospital(hospital) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_view_hospital_details"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary)
                        ) {
                            Text(
                                "View Hospital Details",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.HOSPITAL_MAP) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_see_other_hospitals"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MedicalBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalSlate)
                        ) {
                            Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("See Other Hospitals", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
