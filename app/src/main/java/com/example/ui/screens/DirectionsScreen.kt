package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel

@Composable
fun DirectionsScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val hospital = viewModel.directionsHospital.collectAsState().value
        ?: viewModel.repository.hospitals.first()

    var selectedMode by remember { mutableStateOf("Driving") }
    var isNavigating by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
    ) {
        // Interactive Canvas Route View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Road map canvas
                drawRect(Color(0xFFE9F2F9))

                // Street grid
                val gridColor = Color(0xFFD3E4F2)
                for (y in 0..h.toInt() step 70) {
                    drawLine(gridColor, Offset(0f, y.toFloat()), Offset(w, y.toFloat()), strokeWidth = 2f)
                }
                for (x in 0..w.toInt() step 70) {
                    drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), h), strokeWidth = 2f)
                }

                // Park
                drawRoundRect(
                    color = Color(0xFFD6EED9),
                    topLeft = Offset(w * 0.15f, h * 0.2f),
                    size = androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.3f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )

                // Turn-by-Turn Route Path
                val start = Offset(w * 0.25f, h * 0.75f)
                val p1 = Offset(w * 0.25f, h * 0.45f)
                val p2 = Offset(w * 0.65f, h * 0.45f)
                val destination = Offset(w * 0.65f, h * 0.18f)

                val routePath = Path().apply {
                    moveTo(start.x, start.y)
                    lineTo(p1.x, p1.y)
                    lineTo(p2.x, p2.y)
                    lineTo(destination.x, destination.y)
                }

                // Route halo shadow
                drawPath(
                    path = routePath,
                    color = MedicalBluePrimary.copy(alpha = 0.25f),
                    style = Stroke(width = 16f)
                )

                // Route main line
                drawPath(
                    path = routePath,
                    color = MedicalBluePrimary,
                    style = Stroke(
                        width = 8f,
                        pathEffect = if (selectedMode == "Walking") PathEffect.dashPathEffect(floatArrayOf(15f, 15f)) else null
                    )
                )

                // Start Marker (User Location)
                drawCircle(MedicalBluePrimary.copy(alpha = 0.3f), radius = 24f, center = start)
                drawCircle(Color.White, radius = 14f, center = start)
                drawCircle(MedicalBluePrimary, radius = 9f, center = start)

                // Destination Marker (Hospital)
                drawCircle(DangerRed.copy(alpha = 0.25f), radius = 32f, center = destination)
                drawCircle(Color.White, radius = 18f, center = destination)
                drawCircle(DangerRed, radius = 14f, center = destination)
            }

            // Top-left Floating Back/Close Button
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, MedicalBorder),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
                    .clickable { viewModel.navigateBack() }
                    .testTag("btn_back_directions")
            ) {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MedicalNavy, modifier = Modifier.size(20.dp))
                }
            }

            // Live Navigation Simulation Banner (if active)
            if (isNavigating) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MedicalNavy,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LiveGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "In 200m, turn right onto Health Blvd",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Remaining: 1.8 km • ~7 min • OPD Gate 2",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = { isNavigating = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Stop", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Bottom Directions Control Sheet
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, MedicalBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = hospital.name,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalNavy,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = hospital.address,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MedicalSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LiveGreenLight
                        ) {
                            Text(
                                text = "${hospital.distanceKm} km",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = LiveGreen,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Transport Mode Selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("Driving", "8 min", Icons.Default.DirectionsCar),
                            Triple("Transit", "15 min", Icons.Default.DirectionsBus),
                            Triple("Walking", "26 min", Icons.Default.DirectionsWalk)
                        ).forEach { (mode, time, icon) ->
                            val isSelected = selectedMode == mode
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mode_${mode.lowercase()}")
                                    .clickable { selectedMode = mode },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MedicalBlueLight else MedicalBlueSubtle,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MedicalBluePrimary else MedicalBorder
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = mode,
                                        tint = if (isSelected) MedicalBluePrimary else MedicalSlate,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = time,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) MedicalBluePrimary else MedicalNavy
                                    )
                                    Text(
                                        text = mode,
                                        fontSize = 10.sp,
                                        color = MedicalSlate
                                    )
                                }
                            }
                        }
                    }

                    // Queue advisory card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MedicalBlueLight.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MedicalBlueLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Current OPD wait is ~${hospital.estWaitMinutes} min (${hospital.waitingCount} waiting). Plan departure to arrive 5 mins before your turn.",
                                fontSize = 11.sp,
                                color = MedicalNavy,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Start Navigation Button
                Button(
                    onClick = { isNavigating = !isNavigating },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_start_navigation"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNavigating) DangerRed else MedicalBluePrimary
                    )
                ) {
                    Icon(
                        imageVector = if (isNavigating) Icons.Default.Stop else Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isNavigating) "Stop Navigation" else "Start Navigation",
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
