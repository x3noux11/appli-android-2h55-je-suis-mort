package com.example.applilourde.data.repository

import com.example.applilourde.data.model.Reservation
import com.example.applilourde.data.model.StatutReservation
import java.text.SimpleDateFormat
import java.util.*

class ReservationRepository {
    private val reservations = mutableListOf<Reservation>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)

    fun addReservation(reservation: Reservation) {
        // Vérifie que la réservation est au moins 24h à l'avance
        val dateReservation = dateFormat.parse(reservation.date)
        val now = Calendar.getInstance().time
        val diff = dateReservation.time - now.time
        val joursDiff = diff / (1000 * 60 * 60 * 24)
        
        if (joursDiff < 1) {
            throw IllegalArgumentException("Les réservations doivent être faites au moins 24 heures à l'avance")
        }
        
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

    fun annulerReservation(id: String) {
        val index = reservations.indexOfFirst { it.id == id }
        if (index != -1) {
            val reservation = reservations[index].copy(statut = StatutReservation.ANNULEE)
            reservations[index] = reservation
        }
    }

    fun getReservationsActives(): List<Reservation> {
        return reservations.filter { it.statut == StatutReservation.CONFIRMEE || it.statut == StatutReservation.EN_ATTENTE }
    }

    fun getAllReservations(): List<Reservation> {
        return reservations.toList()
    }
}
