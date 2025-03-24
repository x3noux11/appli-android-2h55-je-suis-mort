package com.example.applilourde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.applilourde.data.model.Enfant
import com.example.applilourde.data.model.Parent
import com.example.applilourde.data.repository.EnfantRepository
import com.example.applilourde.data.repository.ParentRepository
import com.example.applilourde.data.repository.ReservationRepository
import com.example.applilourde.ui.components.EnfantCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    parentId: String,
    parentRepository: ParentRepository,
    enfantRepository: EnfantRepository,
    reservationRepository: ReservationRepository,
    onNavigateToReservation: (String) -> Unit,
    onNavigateToMesReservations: () -> Unit
) {
    val parent = parentRepository.getParentById(parentId)
    val enfants = enfantRepository.getEnfantsByParentId(parentId)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accueil RAM") },
                actions = {
                    IconButton(onClick = { onNavigateToMesReservations() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Mes réservations")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            parent?.let {
                Text(
                    text = "Bonjour ${it.prenom} ${it.nom}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            Text(
                text = "Vos enfants",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            
            if (enfants.isEmpty()) {
                Text(
                    text = "Aucun enfant enregistré",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(enfants) { enfant ->
                        EnfantCard(enfant = enfant, onClick = {
                            onNavigateToReservation(enfant.id)
                        })
                    }
                }
            }
        }
    }
}
