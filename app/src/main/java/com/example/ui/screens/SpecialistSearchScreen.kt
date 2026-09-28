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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun SpecialistSearchScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val searchQuery by viewModel.specialistSearchQuery.collectAsState()
    val allSpecialists = viewModel.repository.specialists

    val filteredList = remember(searchQuery, allSpecialists) {
        if (searchQuery.isBlank()) allSpecialists
        else allSpecialists.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Find a Specialist",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MedicalNavy
            )
        )
        Text(
            text = "Browse medical fields to view top hospitals & live doctor queues",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MedicalSlate,
                fontSize = 13.sp
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSpecialistSearch(it) },
            placeholder = { Text("Search specialist...", color = MedicalMuted) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MedicalBluePrimary)
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.setSpecialistSearch("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MedicalSlate)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_specialist_search"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = MedicalBluePrimary,
                unfocusedBorderColor = MedicalBorder
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { specialist ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("specialist_item_${specialist.name.lowercase()}")
                        .clickable { viewModel.selectSpecialist(specialist) },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, MedicalBorder),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEBF2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getSpecialistIcon(specialist.iconName),
                                contentDescription = specialist.name,
                                tint = MedicalBluePrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = specialist.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MedicalNavy,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = specialist.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MedicalNavy,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MedicalBlueSubtle
                                ) {
                                    Text(
                                        text = "${specialist.doctorCount} Doctors",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MedicalBluePrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = LiveGreenLight
                                ) {
                                    Text(
                                        text = "${specialist.availableCount} Available Today",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = LiveGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Select",
                            tint = MedicalMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
