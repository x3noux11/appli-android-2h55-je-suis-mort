package com.example.applilourde.data.model

import java.util.*

data class Reservation(
    val id: String = UUID.randomUUID().toString(),
    val enfantId: String,
    val date: String, // Format: YYYY-MM-DD
    val heureDebut: String, // Format: HH:MM
    val heureFin: String, // Format: HH:MM
    val statut: StatutReservation = StatutReservation.EN_ATTENTE,
    val creeLe: Long = System.currentTimeMillis()
)

enum class StatutReservation {
    EN_ATTENTE,
    CONFIRMEE,
    ANNULEE
}
