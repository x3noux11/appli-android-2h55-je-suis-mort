package com.example.applilourde.api

import com.example.applilourde.data.model.Disponibilite
import com.example.applilourde.data.repository.DisponibiliteRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.disponibiliteRoutes(disponibiliteRepository: DisponibiliteRepository) {
    route("/api/disponibilites") {
        // GET /api/disponibilites - Récupérer toutes les disponibilités
        get {
            call.respond(disponibiliteRepository.getAllDisponibilites())
        }

        // GET /api/disponibilites/{id} - Récupérer une disponibilité par son ID
        get("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@get
            }

            val disponibilite = disponibiliteRepository.getDisponibiliteById(id)
            if (disponibilite == null) {
                call.respond(HttpStatusCode.NotFound, "Disponibilité non trouvée")
                return@get
            }

            call.respond(disponibilite)
        }

        // GET /api/disponibilites/date/{date} - Récupérer les disponibilités pour une date donnée
        get("/date/{date}") {
            val date = call.parameters["date"]
            if (date == null) {
                call.respond(HttpStatusCode.BadRequest, "Date manquante")
                return@get
            }

            val disponibilites = disponibiliteRepository.getDisponibilitesByDate(date)
            call.respond(disponibilites)
        }

        // POST /api/disponibilites - Créer une nouvelle disponibilité
        post {
            val disponibilite = call.receive<Disponibilite>()
            
            // Vérifier si une disponibilité existe déjà pour cette date et ces heures
            val existingDisponibilites = disponibiliteRepository.getDisponibilitesByDate(disponibilite.date)
            val overlap = existingDisponibilites.any { existing ->
                (disponibilite.heureDebut < existing.heureFin && disponibilite.heureFin > existing.heureDebut) &&
                disponibilite.typeGarde == existing.typeGarde
            }
            
            if (overlap) {
                call.respond(HttpStatusCode.Conflict, "Une disponibilité existe déjà pour cette période et ce type de garde")
                return@post
            }
            
            disponibiliteRepository.addDisponibilite(disponibilite)
            call.respond(HttpStatusCode.Created, disponibilite)
        }

        // PUT /api/disponibilites/{id} - Mettre à jour une disponibilité
        put("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@put
            }

            val existingDisponibilite = disponibiliteRepository.getDisponibiliteById(id)
            if (existingDisponibilite == null) {
                call.respond(HttpStatusCode.NotFound, "Disponibilité non trouvée")
                return@put
            }

            val updatedDisponibilite = call.receive<Disponibilite>()
            
            // Vérifier si la mise à jour crée un chevauchement
            if (existingDisponibilite.date != updatedDisponibilite.date ||
                existingDisponibilite.heureDebut != updatedDisponibilite.heureDebut ||
                existingDisponibilite.heureFin != updatedDisponibilite.heureFin ||
                existingDisponibilite.typeGarde != updatedDisponibilite.typeGarde) {
                
                val existingDisponibilites = disponibiliteRepository.getDisponibilitesByDate(updatedDisponibilite.date)
                val overlap = existingDisponibilites.any { existing ->
                    existing.id != id &&
                    (updatedDisponibilite.heureDebut < existing.heureFin && updatedDisponibilite.heureFin > existing.heureDebut) &&
                    updatedDisponibilite.typeGarde == existing.typeGarde
                }
                
                if (overlap) {
                    call.respond(HttpStatusCode.Conflict, "La mise à jour crée un chevauchement avec une disponibilité existante")
                    return@put
                }
            }
            
            // Vérifier que le nombre de places réservées ne dépasse pas la capacité totale
            if (updatedDisponibilite.placesReservees > updatedDisponibilite.capaciteTotale) {
                call.respond(HttpStatusCode.BadRequest, "Le nombre de places réservées ne peut pas dépasser la capacité totale")
                return@put
            }
            
            disponibiliteRepository.updateDisponibilite(updatedDisponibilite.copy(id = id))
            call.respond(HttpStatusCode.OK, updatedDisponibilite.copy(id = id))
        }

        // DELETE /api/disponibilites/{id} - Supprimer une disponibilité
        delete("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@delete
            }

            val disponibilite = disponibiliteRepository.getDisponibiliteById(id)
            if (disponibilite == null) {
                call.respond(HttpStatusCode.NotFound, "Disponibilité non trouvée")
                return@delete
            }
            
            // Vérifier s'il y a des réservations pour cette disponibilité
            if (disponibilite.placesReservees > 0) {
                call.respond(HttpStatusCode.Conflict, "Impossible de supprimer une disponibilité avec des réservations existantes")
                return@delete
            }

            disponibiliteRepository.deleteDisponibilite(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}