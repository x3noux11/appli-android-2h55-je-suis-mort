package com.example.applilourde.data.model

import java.util.*

data class Disponibilite(
    val id: String = UUID.randomUUID().toString(),
    val date: String, // Format: YYYY-MM-DD
    val heureDebut: String, // Format: HH:MM
    val heureFin: String, // Format: HH:MM
    val capaciteTotale: Int, // Nombre total de places disponibles
    val placesReservees: Int = 0, // Nombre de places déjà réservées
    val typeGarde: TypeGarde
)

enum class TypeGarde {
    REGULIER,
    OCCASIONNEL
}
