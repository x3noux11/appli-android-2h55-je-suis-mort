package com.example.applilourde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.applilourde.data.model.Reservation
import com.example.applilourde.data.model.StatutReservation
import com.example.applilourde.data.repository.EnfantRepository
import com.example.applilourde.data.repository.ReservationRepository
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MesReservationsScreen(
    parentId: String,
    enfantRepository: EnfantRepository,
    reservationRepository: ReservationRepository,
    onNavigateBack: () -> Unit
) {
    // Obtenir tous les enfants du parent
    val enfants = enfantRepository.getEnfantsByParentId(parentId)
    
    // Obtenir toutes les réservations pour les enfants du parent
    val reservationsParEnfant = remember(enfants) {
        enfants.flatMap { enfant ->
            reservationRepository.getReservationsByEnfantId(enfant.id).map { reservation ->
                reservation to enfant
            }
        }.sortedBy { (reservation, _) -> reservation.date }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes réservations") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
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
            if (reservationsParEnfant.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aucune réservation",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Vous n'avez pas encore réservé de place en crèche pour vos enfants.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(reservationsParEnfant) { (reservation, enfant) ->
                        val cardColor = when(reservation.statut) {
                            StatutReservation.CONFIRMEE -> Color(0xFFE0F7E0)
                            StatutReservation.EN_ATTENTE -> Color(0xFFFFF9E0)
                            StatutReservation.ANNULEE -> Color(0xFFF7E0E0)
                        }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardColor
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "${enfant.prenom} ${enfant.nom}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = "Date: ${formatDate(reservation.date)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                
                                Text(
                                    text = "Horaires: ${reservation.heureDebut} - ${reservation.heureFin}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                val statusText = when(reservation.statut) {
                                    StatutReservation.CONFIRMEE -> "Confirmée"
                                    StatutReservation.EN_ATTENTE -> "En attente de confirmation"
                                    StatutReservation.ANNULEE -> "Annulée"
                                }
                                
                                val statusColor = when(reservation.statut) {
                                    StatutReservation.CONFIRMEE -> Color(0xFF0A6B0A)
                                    StatutReservation.EN_ATTENTE -> Color(0xFF6B6B0A)
                                    StatutReservation.ANNULEE -> Color(0xFF6B0A0A)
                                }
                                
                                Text(
                                    text = "Statut: $statusText",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                if (reservation.statut == StatutReservation.EN_ATTENTE) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Button(
                                        onClick = {
                                            reservationRepository.annulerReservation(reservation.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFD32F2F)
                                        ),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Annuler")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
        val outputFormat = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE)
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date).replaceFirstChar { it.uppercase() }
    } catch (e: Exception) {
        return dateString
    }
}
