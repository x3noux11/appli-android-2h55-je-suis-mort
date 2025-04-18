package com.example.applilourde.data.repository

import com.example.applilourde.data.model.Reservation
import com.example.applilourde.data.model.StatutReservation

class ReservationRepository {
    private val reservations = mutableListOf<Reservation>()

    fun addReservation(reservation: Reservation) {
        reservations.add(reservation)
    }

    fun getReservationById(id: String): Reservation? {
        return reservations.find { it.id == id }
    }

    fun getReservationsByEnfantId(enfantId: String): List<Reservation> {
        return reservations.filter { it.enfantId == enfantId }
    }

    fun getReservationsByDate(date: String): List<Reservation> {
        return reservations.filter { it.date == date }
    }

    fun updateReservation(reservation: Reservation) {
        val index = reservations.indexOfFirst { it.id == reservation.id }
        if (index != -1) {
            reservations[index] = reservation
        }
    }

    fun deleteReservation(id: String) {
        reservations.removeIf { it.id == id }
    }

    fun getAllReservations(): List<Reservation> {
        return reservations.toList()
    }
    
    fun annulerReservation(id: String) {
        val reservation = getReservationById(id)
        if (reservation != null) {
            val updatedReservation = reservation.copy(statut = StatutReservation.ANNULEE)
            updateReservation(updatedReservation)
        }
    }

    // Simuler quelques donnu00e9es initiales
    init {
        // Ru00e9servations pour aujourd'hui
        val today = java.time.LocalDate.now().toString()
        
        addReservation(Reservation(
            id = "1",
            enfantId = "1",
            date = today,
            heureDebut = "08:00",
            heureFin = "12:00",
            statut = StatutReservation.CONFIRMEE
        ))
        
        addReservation(Reservation(
            id = "2",
            enfantId = "2",
            date = today,
            heureDebut = "08:00",
            heureFin = "12:00",
            statut = StatutReservation.CONFIRMEE
        ))
        
        addReservation(Reservation(
            id = "3",
            enfantId = "3",
            date = today,
            heureDebut = "14:00",
            heureFin = "18:00",
            statut = StatutReservation.CONFIRMEE
        ))
        
        // Ru00e9servation en attente pour demain
        val tomorrow = java.time.LocalDate.now().plusDays(1).toString()
        
        addReservation(Reservation(
            id = "4",
            enfantId = "1",
            date = tomorrow,
            heureDebut = "08:00",
            heureFin = "12:00",
            statut = StatutReservation.EN_ATTENTE
        ))
    }
}