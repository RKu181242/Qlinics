package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Hospital
import com.example.data.model.HospitalType
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel

@Composable
fun HospitalMapScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val searchQuery by viewModel.mapSearchQuery.collectAsState()
    val selectedFilter by viewModel.mapFilter.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val mapZoom by viewModel.mapZoom.collectAsState()
    val mapStyle by viewModel.mapStyle.collectAsState()
    val allHospitals = viewModel.repository.hospitals
    val indianCities = viewModel.repository.indianCities

    var panOffset by remember { mutableStateOf(Offset.Zero) }

    val filteredHospitals = remember(searchQuery, selectedFilter, selectedCity, allHospitals) {
        allHospitals.filter { hospital ->
            val matchesCity = selectedCity == "All Cities" || hospital.city.equals(selectedCity, ignoreCase = true)
            val matchesFilter = selectedFilter == HospitalType.ALL || hospital.type == selectedFilter
            val matchesSearch = searchQuery.isBlank() ||
                    hospital.name.contains(searchQuery, ignoreCase = true) ||
                    hospital.address.contains(searchQuery, ignoreCase = true) ||
                    hospital.city.contains(searchQuery, ignoreCase = true)
            matchesCity && matchesFilter && matchesSearch
        }
    }

    var selectedHospitalOnMap by remember {
        mutableStateOf<Hospital?>(null)
    }

    // Reset pan and dismiss overview when city changes
    LaunchedEffect(selectedCity) {
        panOffset = Offset.Zero
        selectedHospitalOnMap = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                when (mapStyle) {
                    "Satellite" -> Color(0xFF101B24)
                    "Terrain" -> Color(0xFFF1F0E8)
                    else -> Color(0xFFF4F3F0)
                }
            )
    ) {
        // GOOGLE MAPS CANVASES
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        panOffset += dragAmount
                    }
                }
                .pointerInput(filteredHospitals, mapZoom, panOffset, selectedCity) {
                    detectTapGestures { tapOffset ->
                        val width = size.width.toFloat()
                        val height = size.height.toFloat()

                        // Marker tap threshold radius (~100 px)
                        val tapThresholdSq = 100f * 100f
                        val nearest = filteredHospitals.map { hosp ->
                            val (mx, my) = getIndianHospitalCoord(hosp, width, height, mapZoom, panOffset, selectedCity)
                            val dx = tapOffset.x - mx
                            val dy = tapOffset.y - my
                            Pair(hosp, dx * dx + dy * dy)
                        }.minByOrNull { it.second }

                        if (nearest != null && nearest.second <= tapThresholdSq) {
                            selectedHospitalOnMap = nearest.first
                        } else {
                            // Tapped away on the map background -> Remove & close hospital overview!
                            selectedHospitalOnMap = null
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // 1. BASE MAP TERRAIN (Google Maps Color Scheme)
            when (mapStyle) {
                "Satellite" -> {
                    drawRect(Color(0xFF13202A))
                    // Satellite water (Deep Navy Blue)
                    val waterPath = Path().apply {
                        moveTo(0f, h * 0.35f)
                        cubicTo(w * 0.35f, h * 0.38f, w * 0.65f, h * 0.45f, w, h * 0.48f)
                        lineTo(w, h * 0.58f)
                        cubicTo(w * 0.65f, h * 0.55f, w * 0.35f, h * 0.48f, 0f, h * 0.45f)
                        close()
                    }
                    drawPath(waterPath, Color(0xFF09141D))
                }
                "Terrain" -> {
                    drawRect(Color(0xFFEDECE2))
                    // Parks
                    drawRoundRect(
                        color = Color(0xFFC7E2B6),
                        topLeft = Offset(w * 0.15f + panOffset.x * 0.2f, h * 0.22f + panOffset.y * 0.2f),
                        size = Size(w * 0.35f * mapZoom, h * 0.18f * mapZoom),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                    )
                }
                else -> {
                    // Google Maps Default (Clean Cream & Blue)
                    drawRect(Color(0xFFF4F3F0))

                    // River / Water body (Google Maps Cyan Blue)
                    val riverPath = Path().apply {
                        val riverY = h * 0.42f + panOffset.y * 0.3f
                        moveTo(0f, riverY)
                        cubicTo(w * 0.3f, riverY - 40f, w * 0.7f, riverY + 60f, w, riverY + 10f)
                        lineTo(w, riverY + 55f)
                        cubicTo(w * 0.7f, riverY + 105f, w * 0.3f, riverY + 5f, 0f, riverY + 45f)
                        close()
                    }
                    drawPath(riverPath, Color(0xFFA5D7F0))

                    // City Green Areas / Botanical Gardens (Google Maps Green #C8E6C9)
                    drawRoundRect(
                        color = Color(0xFFCDE8C5),
                        topLeft = Offset(w * 0.18f + panOffset.x * 0.3f, h * 0.25f + panOffset.y * 0.3f),
                        size = Size(w * 0.32f * mapZoom, h * 0.15f * mapZoom),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
                    )

                    drawRoundRect(
                        color = Color(0xFFCDE8C5),
                        topLeft = Offset(w * 0.62f + panOffset.x * 0.3f, h * 0.62f + panOffset.y * 0.3f),
                        size = Size(w * 0.26f * mapZoom, h * 0.14f * mapZoom),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f)
                    )
                }
            }

            // 2. ROAD NETWORK (Google Maps Highways & Streets)
            val highwayYellow = if (mapStyle == "Satellite") Color(0xFFE5A93C) else Color(0xFFFDD877)
            val streetWhite = if (mapStyle == "Satellite") Color(0xFF2A3D4D) else Color.White
            val roadBorder = if (mapStyle == "Satellite") Color(0xFF1E2D38) else Color(0xFFDAD6CE)

            // Grid local streets
            for (stepY in -200..h.toInt() + 400 step (95 * mapZoom).toInt().coerceAtLeast(40)) {
                val y = stepY.toFloat() + (panOffset.y % 95)
                drawLine(roadBorder, Offset(0f, y), Offset(w, y), strokeWidth = 5f)
                drawLine(streetWhite, Offset(0f, y), Offset(w, y), strokeWidth = 3f)
            }
            for (stepX in -200..w.toInt() + 400 step (95 * mapZoom).toInt().coerceAtLeast(40)) {
                val x = stepX.toFloat() + (panOffset.x % 95)
                drawLine(roadBorder, Offset(x, 0f), Offset(x, h), strokeWidth = 5f)
                drawLine(streetWhite, Offset(x, 0f), Offset(x, h), strokeWidth = 3f)
            }

            // Major National Highway / Ring Road (Golden Orange - NH 48 / Outer Ring Road)
            val nhY = h * 0.32f + panOffset.y * 0.5f
            drawLine(Color(0xFFE2A237), Offset(0f, nhY), Offset(w, nhY + 50f), strokeWidth = 11f)
            drawLine(highwayYellow, Offset(0f, nhY), Offset(w, nhY + 50f), strokeWidth = 8f)

            val nhX = w * 0.44f + panOffset.x * 0.5f
            drawLine(Color(0xFFE2A237), Offset(nhX, 0f), Offset(nhX + 40f, h), strokeWidth = 11f)
            drawLine(highwayYellow, Offset(nhX, 0f), Offset(nhX + 40f, h), strokeWidth = 8f)

            // Metro Transit Line (Indigo dashed line with stations)
            val metroY = h * 0.54f + panOffset.y * 0.4f
            drawLine(Color(0xFF4338CA), Offset(0f, metroY), Offset(w, metroY - 30f), strokeWidth = 3.5f)

            // 3. USER LOCATION (Pulsing Google Maps Blue Dot)
            val userX = w * 0.48f + panOffset.x * 0.6f
            val userY = h * 0.50f + panOffset.y * 0.6f

            drawCircle(Color(0xFF4285F4).copy(alpha = 0.20f), radius = 48f * mapZoom, center = Offset(userX, userY))
            drawCircle(Color(0xFF4285F4).copy(alpha = 0.40f), radius = 28f * mapZoom, center = Offset(userX, userY))
            drawCircle(Color.White, radius = 14f, center = Offset(userX, userY))
            drawCircle(Color(0xFF1A73E8), radius = 9f, center = Offset(userX, userY))

            // 4. ROUTE CONNECTOR LINE (From user to selected hospital)
            selectedHospitalOnMap?.let { selectedHosp ->
                val (sx, sy) = getIndianHospitalCoord(selectedHosp, w, h, mapZoom, panOffset, selectedCity)
                drawLine(
                    color = Color(0xFF1A73E8).copy(alpha = 0.5f),
                    start = Offset(userX, userY),
                    end = Offset(sx, sy),
                    strokeWidth = 6f
                )
            }

            // 5. GOOGLE MAPS HOSPITAL TEARDROP PINS
            filteredHospitals.forEach { hospital ->
                val (mx, my) = getIndianHospitalCoord(hospital, w, h, mapZoom, panOffset, selectedCity)
                val isSelected = hospital.id == selectedHospitalOnMap?.id

                // Marker shadow
                drawCircle(
                    color = Color.Black.copy(alpha = 0.25f),
                    radius = if (isSelected) 14f else 9f,
                    center = Offset(mx, my + 6f)
                )

                // Classic Red Google Maps Teardrop Pin
                val pinColor = when {
                    isSelected -> Color(0xFFD93025) // Vibrant Google Red
                    hospital.waitingCount <= 5 -> Color(0xFF1E8E3E) // Google Maps Green
                    else -> Color(0xFFEA4335) // Standard Google Red
                }

                // Pin circle bulb
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 22f else 17f,
                    center = Offset(mx, my - 14f)
                )
                drawCircle(
                    color = pinColor,
                    radius = if (isSelected) 19f else 14f,
                    center = Offset(mx, my - 14f)
                )

                // White Hospital Cross inside Pin
                val crossSize = if (isSelected) 9f else 7f
                drawLine(
                    color = Color.White,
                    start = Offset(mx - crossSize, my - 14f),
                    end = Offset(mx + crossSize, my - 14f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color.White,
                    start = Offset(mx, my - 14f - crossSize),
                    end = Offset(mx, my - 14f + crossSize),
                    strokeWidth = 3f
                )

                // Pin Point Bottom Triangle
                val pinPath = Path().apply {
                    moveTo(mx - 7f, my - 6f)
                    lineTo(mx + 7f, my - 6f)
                    lineTo(mx, my + 3f)
                    close()
                }
                drawPath(pinPath, pinColor)
            }
        }

        // TOP GOOGLE MAPS FLOATING SEARCH & CONTROLS BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 12.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Google Maps Style Floating Search Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF4285F4),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setMapSearch(it) },
                        placeholder = {
                            Text(
                                "Search hospitals in India...",
                                color = MedicalNavy,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_map_search"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setMapSearch("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MedicalNavy)
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFE8F0FE)
                        ) {
                            Text(
                                text = selectedCity,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = Color(0xFF1967D2),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // INDIAN CITIES FILTER CHIPS
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(indianCities) { city ->
                    val isCitySelected = selectedCity == city
                    Surface(
                        modifier = Modifier
                            .testTag("city_chip_${city.lowercase().replace(" ", "_")}")
                            .clickable { viewModel.selectCity(city) },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isCitySelected) Color(0xFF1A73E8) else Color.White,
                        border = BorderStroke(1.dp, if (isCitySelected) Color(0xFF1A73E8) else Color(0xFFDADCE0)),
                        shadowElevation = if (isCitySelected) 3.dp else 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (city != "All Cities") {
                                Icon(
                                    imageVector = Icons.Default.LocationCity,
                                    contentDescription = null,
                                    tint = if (isCitySelected) Color.White else Color(0xFF5F6368),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = city,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isCitySelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isCitySelected) Color.White else MedicalNavy,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // HOSPITAL TYPE CHIPS (Multi-Speciality, Government, Private)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(HospitalType.values()) { type ->
                    val isTypeSelected = selectedFilter == type
                    Surface(
                        modifier = Modifier
                            .clickable { viewModel.setMapFilter(type) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isTypeSelected) MedicalNavy else Color.White.copy(alpha = 0.95f),
                        border = BorderStroke(1.dp, if (isTypeSelected) MedicalNavy else Color(0xFFCBD5E1)),
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = type.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isTypeSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isTypeSelected) Color.White else MedicalNavy,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // GOOGLE MAPS FLOATING ACTION TOOLS (Right Hand Side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Map Layers Button (Default / Satellite / Terrain)
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clickable {
                        val next = when (mapStyle) {
                            "Normal" -> "Satellite"
                            "Satellite" -> "Terrain"
                            else -> "Normal"
                        }
                        viewModel.setMapStyle(next)
                    },
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, Color(0xFFDADCE0))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Map Style: $mapStyle",
                        tint = Color(0xFF1A73E8),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Recenter My Location (GPS Crosshair)
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clickable {
                        panOffset = Offset.Zero
                        viewModel.recenterMap()
                    },
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, Color(0xFFDADCE0))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "My Location",
                        tint = Color(0xFF1A73E8),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Zoom In (+)
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { viewModel.zoomIn() },
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, Color(0xFFDADCE0))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = MedicalNavy)
                }
            }

            // Zoom Out (-)
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { viewModel.zoomOut() },
                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, Color(0xFFDADCE0))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = MedicalNavy)
                }
            }
        }

        // BOTTOM GOOGLE MAPS PREVIEW CARD (Floating Sheet)
        AnimatedVisibility(
            visible = selectedHospitalOnMap != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 80.dp)
        ) {
            selectedHospitalOnMap?.let { hospital ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_hospital_preview_card"),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.5.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Drag handle / Dismiss bar
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(42.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFCBD5E1))
                                .clickable { selectedHospitalOnMap = null }
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = hospital.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = MedicalNavy,
                                            fontSize = 17.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${hospital.landmark} • ${hospital.city}, ${hospital.state}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MedicalNavy,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Star Rating Badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${hospital.rating}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF92400E)
                                            )
                                        )
                                    }
                                }

                                // Close Button (X) to remove hospital overview
                                IconButton(
                                    onClick = { selectedHospitalOnMap = null },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F5F9))
                                        .testTag("btn_close_hospital_overview")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Overview",
                                        tint = MedicalNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live Metrics Row (Distance, OPD Live Queue, Est. Wait)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F6FE))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${hospital.distanceKm} km",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = MedicalNavy
                                    )
                                )
                                Text("Distance", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MedicalNavy)
                            }
                            Divider(modifier = Modifier.height(28.dp).width(1.5.dp), color = Color(0xFFCBD5E1))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${hospital.waitingCount} waiting",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = if (hospital.waitingCount <= 6) Color(0xFF15803D) else Color(0xFFB45309)
                                    )
                                )
                                Text("Live OPD Queue", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MedicalNavy)
                            }
                            Divider(modifier = Modifier.height(28.dp).width(1.5.dp), color = Color(0xFFCBD5E1))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "~${hospital.estWaitMinutes} min",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = MedicalNavy
                                    )
                                )
                                Text("Est. Wait", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MedicalNavy)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Google Maps Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Directions Button
                            Button(
                                onClick = { viewModel.openDirections(hospital) },
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(46.dp)
                                    .testTag("btn_map_directions"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Directions", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            // View Details Button
                            Button(
                                onClick = { viewModel.selectHospital(hospital) },
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(46.dp)
                                    .testTag("btn_map_view_details"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalNavy)
                            ) {
                                Text("View OPD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            // Phone Call Button
                            Surface(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hospital.phone}"))
                                        context.startActivity(intent)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEBF2FE),
                                border = BorderStroke(1.dp, MedicalBorder)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = MedicalBluePrimary, modifier = Modifier.size(18.dp))
                                }
                            }

                            // Dismiss / Close Button
                            Surface(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clickable { selectedHospitalOnMap = null }
                                    .testTag("btn_dismiss_overview"),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Overview", tint = MedicalNavy, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Coordinate projection helper for Indian city hospitals
private fun getIndianHospitalCoord(
    hospital: Hospital,
    width: Float,
    height: Float,
    zoom: Float,
    pan: Offset,
    selectedCity: String
): Pair<Float, Float> {
    val (baseX, baseY) = if (selectedCity == "All Cities") {
        // India bounds: lat 8.0 (South) to 33.0 (North), lng 69.0 (West) to 89.0 (East)
        val normX = (0.15f + ((hospital.lng - 69.0) / (89.0 - 69.0)).toFloat() * 0.70f).coerceIn(0.12f, 0.88f)
        val normY = (0.18f + ((33.0 - hospital.lat) / (33.0 - 8.0)).toFloat() * 0.64f).coerceIn(0.15f, 0.85f)
        Pair(normX, normY)
    } else {
        // City local distribution using deterministic offset
        val hash = (hospital.id.hashCode() and 0x7FFFFFFF)
        val angle = (hash % 360) * (Math.PI / 180.0)
        val radius = 0.12f + ((hash % 100) / 100f) * 0.28f
        val normX = (0.50f + kotlin.math.cos(angle).toFloat() * radius).coerceIn(0.16f, 0.84f)
        val normY = (0.50f + kotlin.math.sin(angle).toFloat() * radius).coerceIn(0.20f, 0.80f)
        Pair(normX, normY)
    }

    val centerX = width * 0.5f
    val centerY = height * 0.5f

    val projectedX = centerX + (width * (baseX - 0.5f) * zoom) + (pan.x * 0.8f)
    val projectedY = centerY + (height * (baseY - 0.5f) * zoom) + (pan.y * 0.8f)

    return Pair(projectedX, projectedY)
}
