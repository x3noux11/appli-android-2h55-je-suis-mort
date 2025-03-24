package com.example.applilourde.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.example.applilourde.data.model.Disponibilite
import com.example.applilourde.data.model.Enfant
import com.example.applilourde.data.model.Reservation
import com.example.applilourde.data.model.StatutReservation
import com.example.applilourde.data.repository.DisponibiliteRepository
import com.example.applilourde.data.repository.EnfantRepository
import com.example.applilourde.data.repository.ReservationRepository
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    enfantId: String,
    enfantRepository: EnfantRepository,
    disponibiliteRepository: DisponibiliteRepository,
    reservationRepository: ReservationRepository,
    onNavigateBack: () -> Unit
) {
    val enfant = enfantRepository.getEnfantById(enfantId)
    val disponibilites = disponibiliteRepository.getDisponibilitesDisponibles()
    var selectedDisponibiliteId by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Réserver une place") },
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
            enfant?.let { currentEnfant ->
                Text(
                    text = "Réserver pour ${currentEnfant.prenom} ${currentEnfant.nom}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                if (successMessage.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE0F7E0)
                        )
                    ) {
                        Text(
                            text = successMessage,
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF0A6B0A)
                        )
                    }
                }
                
                if (errorMessage.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF7E0E0)
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF6B0A0A)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Disponibilités (places occasionnelles)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (disponibilites.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Aucune disponibilité pour le moment",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(disponibilites) { disponibilite ->
                            val isSelected = selectedDisponibiliteId == disponibilite.id
                            val cardColor = if (isSelected) {
                                Color(0xFFE0E0FF)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = cardColor
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = formatDate(disponibilite.date),
                                            fontWeight = FontWeight.Bold
                                        )
                                        
                                        Text(
                                            text = "Horaires: ${disponibilite.heureDebut} - ${disponibilite.heureFin}"
                                        )
                                        
                                        Text(
                                            text = "Places disponibles: ${disponibilite.capaciteTotale - disponibilite.placesReservees} / ${disponibilite.capaciteTotale}"
                                        )
                                    }
                                    
                                    Button(
                                        onClick = { selectedDisponibiliteId = disponibilite.id },
                                        modifier = Modifier.padding(start = 8.dp),
                                        enabled = !isSelected
                                    ) {
                                        Text(if (isSelected) "Sélectionné" else "Sélectionner")
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        if (selectedDisponibiliteId.isEmpty()) {
                            errorMessage = "Veuillez sélectionner une disponibilité"
                            return@Button
                        }
                        
                        val disponibilite = disponibiliteRepository.getDisponibiliteById(selectedDisponibiliteId)
                        
                        if (disponibilite != null) {
                            try {
                                // Créer la réservation
                                val reservation = Reservation(
                                    enfantId = enfantId,
                                    date = disponibilite.date,
                                    heureDebut = disponibilite.heureDebut,
                                    heureFin = disponibilite.heureFin,
                                    statut = StatutReservation.EN_ATTENTE
                                )
                                
                                reservationRepository.addReservation(reservation)
                                
                                // Mettre à jour la disponibilité
                                disponibiliteRepository.incrementerPlacesReservees(disponibilite.id)
                                
                                successMessage = "Réservation effectuée avec succès pour le ${formatDate(disponibilite.date)}"
                                errorMessage = ""
                                selectedDisponibiliteId = ""
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Erreur lors de la réservation"
                                successMessage = ""
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = selectedDisponibiliteId.isNotEmpty()
                ) {
                    Text("Confirmer la réservation")
                }
            } ?: run {
                Text(
                    text = "Enfant non trouvé",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
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
