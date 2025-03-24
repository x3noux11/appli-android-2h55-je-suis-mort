package com.example.applilourde.data.repository

import com.example.applilourde.data.model.Disponibilite
import com.example.applilourde.data.model.TypeGarde
import java.text.SimpleDateFormat
import java.util.*

class DisponibiliteRepository {
    private val disponibilites = mutableListOf<Disponibilite>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
    
    fun addDisponibilite(disponibilite: Disponibilite) {
        disponibilites.add(disponibilite)
    }
    
    fun getDisponibiliteById(id: String): Disponibilite? {
        return disponibilites.find { it.id == id }
    }
    
    fun getDisponibilitesByDate(date: String): List<Disponibilite> {
        return disponibilites.filter { it.date == date }
    }
    
    fun getDisponibilitesParType(typeGarde: TypeGarde): List<Disponibilite> {
        return disponibilites.filter { it.typeGarde == typeGarde }
    }
    
    fun getDisponibilitesDisponibles(): List<Disponibilite> {
        val maintenant = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
        
        return disponibilites.filter { dispo ->
            val dateDisponibilite = dateFormat.parse(dispo.date)
            val placesDisponibles = dispo.capaciteTotale - dispo.placesReservees
            
            // Seulement les dates futures et avec des places disponibles
            dateDisponibilite.after(maintenant) && placesDisponibles > 0
        }
    }
    
    fun updateDisponibilite(disponibilite: Disponibilite) {
        val index = disponibilites.indexOfFirst { it.id == disponibilite.id }
        if (index != -1) {
            disponibilites[index] = disponibilite
        }
    }
    
    fun incrementerPlacesReservees(id: String): Boolean {
        val disponibilite = getDisponibiliteById(id) ?: return false
        
        if (disponibilite.placesReservees < disponibilite.capaciteTotale) {
            val updated = disponibilite.copy(placesReservees = disponibilite.placesReservees + 1)
            updateDisponibilite(updated)
            return true
        }
        return false
    }
    
    fun decrementerPlacesReservees(id: String): Boolean {
        val disponibilite = getDisponibiliteById(id) ?: return false
        
        if (disponibilite.placesReservees > 0) {
            val updated = disponibilite.copy(placesReservees = disponibilite.placesReservees - 1)
            updateDisponibilite(updated)
            return true
        }
        return false
    }
    
    fun deleteDisponibilite(id: String) {
        disponibilites.removeIf { it.id == id }
    }
    
    fun getAllDisponibilites(): List<Disponibilite> {
        return disponibilites.toList()
    }
    
    // Simuler quelques données initiales
    init {
        // Générer quelques disponibilités pour la semaine à venir
        val calendar = Calendar.getInstance()
        
        // Commencer par demain
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        
        // Ajouter des disponibilités pour les 10 prochains jours
        for (i in 0 until 10) {
            val date = dateFormat.format(calendar.time)
            
            // Ajouter une disponibilité pour la garde occasionnelle
            addDisponibilite(Disponibilite(
                date = date,
                heureDebut = "08:00",
                heureFin = "18:00",
                capaciteTotale = 10,
                placesReservees = (0..3).random(), // Simuler quelques places déjà réservées
                typeGarde = TypeGarde.OCCASIONNEL
            ))
            
            // Passer au jour suivant
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}
