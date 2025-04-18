package com.example.applilourde.data.repository

import com.example.applilourde.data.model.Disponibilite
import com.example.applilourde.data.model.TypeGarde

class DisponibiliteRepository {
    private val disponibilites = mutableListOf<Disponibilite>()

    fun addDisponibilite(disponibilite: Disponibilite) {
        disponibilites.add(disponibilite)
    }

    fun getDisponibiliteById(id: String): Disponibilite? {
        return disponibilites.find { it.id == id }
    }

    fun getDisponibilitesByDate(date: String): List<Disponibilite> {
        return disponibilites.filter { it.date == date }
    }

    fun updateDisponibilite(disponibilite: Disponibilite) {
        val index = disponibilites.indexOfFirst { it.id == disponibilite.id }
        if (index != -1) {
            disponibilites[index] = disponibilite
        }
    }

    fun deleteDisponibilite(id: String) {
        disponibilites.removeIf { it.id == id }
    }

    fun getAllDisponibilites(): List<Disponibilite> {
        return disponibilites.toList()
    }

    // Simuler quelques donnu00e9es initiales
    init {
        // Disponibilitu00e9s pour aujourd'hui
        val today = java.time.LocalDate.now().toString()
        
        addDisponibilite(Disponibilite(
            id = "1",
            date = today,
            heureDebut = "08:00",
            heureFin = "12:00",
            capaciteTotale = 10,
            placesReservees = 3,
            typeGarde = TypeGarde.REGULIER
        ))
        
        addDisponibilite(Disponibilite(
            id = "2",
            date = today,
            heureDebut = "14:00",
            heureFin = "18:00",
            capaciteTotale = 8,
            placesReservees = 2,
            typeGarde = TypeGarde.REGULIER
        ))
        
        // Disponibilitu00e9s pour demain
        val tomorrow = java.time.LocalDate.now().plusDays(1).toString()
        
        addDisponibilite(Disponibilite(
            id = "3",
            date = tomorrow,
            heureDebut = "08:00",
            heureFin = "12:00",
            capaciteTotale = 10,
            placesReservees = 0,
            typeGarde = TypeGarde.REGULIER
        ))
        
        addDisponibilite(Disponibilite(
            id = "4",
            date = tomorrow,
            heureDebut = "14:00",
            heureFin = "18:00",
            capaciteTotale = 8,
            placesReservees = 0,
            typeGarde = TypeGarde.REGULIER
        ))
        
        // Disponibilitu00e9 occasionnelle
        addDisponibilite(Disponibilite(
            id = "5",
            date = tomorrow,
            heureDebut = "18:00",
            heureFin = "20:00",
            capaciteTotale = 5,
            placesReservees = 0,
            typeGarde = TypeGarde.OCCASIONNEL
        ))
    }
}