package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.QlinicsViewModel

@Composable
fun DoctorListScreen(
    viewModel: QlinicsViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val selectedSpecialist by viewModel.selectedSpecialist.collectAsState()
    val specName = selectedSpecialist?.name ?: "Cardiology"
    val doctors = viewModel.repository.doctors.filter { it.specialty.equals(specName, ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "$specName Doctors",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MedicalNavy
                )
            )
            Text(
                text = "Compare current queue waiting times and book from home",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MedicalNavy,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(doctors) { doctor ->
                DoctorCardRow(
                    doctor = doctor,
                    onSelect = { viewModel.selectDoctor(doctor) }
                )
            }
        }
    }
}
