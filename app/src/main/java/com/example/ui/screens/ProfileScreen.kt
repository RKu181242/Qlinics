package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun ProfileScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val scrollState = rememberScrollState()
    var infoDialogTitle by remember { mutableStateOf<String?>(null) }
    var infoDialogMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Patient Profile",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = MedicalNavy,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = "Manage your health profile, records, and settings",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MedicalNavy,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFDCFCE7),
                border = BorderStroke(1.dp, Color(0xFF86EFAC))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "VERIFIED",
                        color = Color(0xFF15803D),
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Profile Identity Card (Ultra-visible, bold contrast)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_patient_profile"),
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            border = BorderStroke(1.5.dp, MedicalBorder),
            shadowElevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEBF2FE))
                            .border(2.5.dp, MedicalBluePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Rehan",
                            tint = MedicalBluePrimary,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rehan",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = MedicalNavy,
                                fontSize = 22.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+1 (555) 019-9281",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MedicalNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "rehan.patient@qlinics.com",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MedicalBluePrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }

                // Quick Health Badges Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F6FE))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("29 Yrs", fontWeight = FontWeight.Black, fontSize = 15.sp, color = MedicalNavy)
                        Text("Age", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedicalNavy)
                    }
                    Divider(modifier = Modifier.height(30.dp).width(1.5.dp), color = Color(0xFFCBD5E1))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("O+ Positive", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFFDC2626))
                        Text("Blood Group", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedicalNavy)
                    }
                    Divider(modifier = Modifier.height(30.dp).width(1.5.dp), color = Color(0xFFCBD5E1))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Token A-27", fontWeight = FontWeight.Black, fontSize = 15.sp, color = MedicalBluePrimary)
                        Text("Active Queue", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedicalNavy)
                    }
                }
            }
        }

        // Section Title: Account & Medical Management
        Text(
            text = "Health & Profile Information",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = MedicalNavy,
                fontSize = 17.sp
            )
        )

        // Menu Sections Card
        val menuItems = listOf(
            Triple(
                "Personal Information",
                "Full Name: Rehan • Age: 29 • Gender: Male • Emergency Contact: +1 (555) 019-9282",
                Icons.Default.Badge
            ),
            Triple(
                "Medical History",
                "Allergies: No known drug allergies • Conditions: Mild seasonal asthma • Blood Pressure: Normal",
                Icons.Default.MedicalInformation
            ),
            Triple(
                "Dependents & Family",
                "2 Linked family profiles: Amina (Spouse, 28) & Farhan (Child, 4)",
                Icons.Default.FamilyRestroom
            ),
            Triple(
                "Preferred Language",
                "English (United States) • Notifications & Audio Alerts Enabled",
                Icons.Default.Translate
            ),
            Triple(
                "Help & Clinic Support",
                "24/7 CareQueue Clinic Support Hotline: 1-800-QLINICS • FAQ & Queue Guide",
                Icons.Default.HelpOutline
            )
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, MedicalBorder),
            shadowElevation = 2.dp
        ) {
            Column {
                menuItems.forEachIndexed { index, (title, subtitle, icon) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                infoDialogTitle = title
                                infoDialogMessage = subtitle
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEBF2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = MedicalBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = MedicalNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    color = MedicalNavy,
                                    lineHeight = 16.sp
                                ),
                                maxLines = 2
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = MedicalNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (index < menuItems.size - 1) {
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MedicalBorder
                        )
                    }
                }
            }
        }

        // Section Title: Quick Actions
        Text(
            text = "Actions & Controls",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = MedicalNavy,
                fontSize = 17.sp
            )
        )

        // View Appointments CTA
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(Screen.APPOINTMENTS) },
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, MedicalBorder),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EventAvailable, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("View My Bookings & Queue Tokens", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedicalNavy)
                    Text("Track upcoming consultations or reschedule", fontSize = 12.sp, color = MedicalNavy)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MedicalNavy)
            }
        }

        // Demo Queue Reset Button
        Button(
            onClick = { viewModel.resetDemoQueue() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_profile_reset_queue"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reset Demo Queue to Initial State",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }

    // High-Contrast Information Dialog
    if (infoDialogMessage != null) {
        AlertDialog(
            onDismissRequest = { infoDialogMessage = null },
            title = {
                Text(
                    text = infoDialogTitle ?: "Information",
                    fontWeight = FontWeight.Black,
                    color = MedicalNavy,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = infoDialogMessage ?: "",
                    color = MedicalNavy,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            confirmButton = {
                Button(
                    onClick = { infoDialogMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
