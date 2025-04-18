package com.example.applilourde.api

import com.example.applilourde.data.model.Reservation
import com.example.applilourde.data.model.StatutReservation
import com.example.applilourde.data.repository.DisponibiliteRepository
import com.example.applilourde.data.repository.EnfantRepository
import com.example.applilourde.data.repository.ReservationRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.reservationRoutes(
    reservationRepository: ReservationRepository,
    enfantRepository: EnfantRepository,
    disponibiliteRepository: DisponibiliteRepository
) {
    route("/api/reservations") {
        // GET /api/reservations - Ru00e9cupu00e9rer toutes les ru00e9servations
        get {
            call.respond(reservationRepository.getAllReservations())
        }

        // GET /api/reservations/{id} - Ru00e9cupu00e9rer une ru00e9servation par son ID
        get("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@get
            }

            val reservation = reservationRepository.getReservationById(id)
            if (reservation == null) {
                call.respond(HttpStatusCode.NotFound, "Ru00e9servation non trouvu00e9e")
                return@get
            }

            call.respond(reservation)
        }

        // GET /api/reservations/enfant/{enfantId} - Ru00e9cupu00e9rer les ru00e9servations d'un enfant
        get("/enfant/{enfantId}") {
            val enfantId = call.parameters["enfantId"]
            if (enfantId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID de l'enfant manquant")
                return@get
            }

            val enfant = enfantRepository.getEnfantById(enfantId)
            if (enfant == null) {
                call.respond(HttpStatusCode.NotFound, "Enfant non trouvu00e9")
                return@get
            }

            val reservations = reservationRepository.getReservationsByEnfantId(enfantId)
            call.respond(reservations)
        }

        // GET /api/reservations/date/{date} - Ru00e9cupu00e9rer les ru00e9servations pour une date donnu00e9e
        get("/date/{date}") {
            val date = call.parameters["date"]
            if (date == null) {
                call.respond(HttpStatusCode.BadRequest, "Date manquante")
                return@get
            }

            val reservations = reservationRepository.getReservationsByDate(date)
            call.respond(reservations)
        }

        // POST /api/reservations - Cru00e9er une nouvelle ru00e9servation
        post {
            val reservation = call.receive<Reservation>()
            
            // Vu00e9rifier si l'enfant existe
            val enfant = enfantRepository.getEnfantById(reservation.enfantId)
            if (enfant == null) {
                call.respond(HttpStatusCode.BadRequest, "L'enfant spu00e9cifiu00e9 n'existe pas")
                return@post
            }
            
            // Vu00e9rifier s'il y a une disponibilitu00e9 pour cette date et ces heures
            val disponibilites = disponibiliteRepository.getDisponibilitesByDate(reservation.date)
            val disponibilite = disponibilites.find { dispo ->
                reservation.heureDebut >= dispo.heureDebut && reservation.heureFin <= dispo.heureFin
            }
            
            if (disponibilite == null) {
                call.respond(HttpStatusCode.BadRequest, "Aucune disponibilitu00e9 pour cette date et ces heures")
                return@post
            }
            
            // Vu00e9rifier s'il reste des places disponibles
            if (disponibilite.placesReservees >= disponibilite.capaciteTotale) {
                call.respond(HttpStatusCode.Conflict, "Plus de places disponibles pour cette pu00e9riode")
                return@post
            }
            
            // Mettre u00e0 jour le nombre de places ru00e9servu00e9es
            val updatedDisponibilite = disponibilite.copy(
                placesReservees = disponibilite.placesReservees + 1
            )
            disponibiliteRepository.updateDisponibilite(updatedDisponibilite)
            
            // Cru00e9er la ru00e9servation
            reservationRepository.addReservation(reservation)
            call.respond(HttpStatusCode.Created, reservation)
        }

        // PUT /api/reservations/{id} - Mettre u00e0 jour une ru00e9servation
        put("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@put
            }

            val existingReservation = reservationRepository.getReservationById(id)
            if (existingReservation == null) {
                call.respond(HttpStatusCode.NotFound, "Ru00e9servation non trouvu00e9e")
                return@put
            }

            val updatedReservation = call.receive<Reservation>()
            
            // Si le statut change u00e0 ANNULEE, libu00e9rer la place
            if (existingReservation.statut != StatutReservation.ANNULEE && 
                updatedReservation.statut == StatutReservation.ANNULEE) {
                
                // Trouver la disponibilitu00e9 correspondante
                val disponibilites = disponibiliteRepository.getDisponibilitesByDate(existingReservation.date)
                val disponibilite = disponibilites.find { dispo ->
                    existingReservation.heureDebut >= dispo.heureDebut && existingReservation.heureFin <= dispo.heureFin
                }
                
                if (disponibilite != null) {
                    // Libu00e9rer une place
                    val updatedDisponibilite = disponibilite.copy(
                        placesReservees = (disponibilite.placesReservees - 1).coerceAtLeast(0)
                    )
                    disponibiliteRepository.updateDisponibilite(updatedDisponibilite)
                }
            }
            
            // Si on change la date ou les heures, vu00e9rifier les disponibilitu00e9s
            if (existingReservation.date != updatedReservation.date ||
                existingReservation.heureDebut != updatedReservation.heureDebut ||
                existingReservation.heureFin != updatedReservation.heureFin) {
                
                // Libu00e9rer la place dans l'ancienne disponibilitu00e9
                val anciennesDisponibilites = disponibiliteRepository.getDisponibilitesByDate(existingReservation.date)
                val ancienneDisponibilite = anciennesDisponibilites.find { dispo ->
                    existingReservation.heureDebut >= dispo.heureDebut && existingReservation.heureFin <= dispo.heureFin
                }
                
                if (ancienneDisponibilite != null) {
                    val updatedAncienneDisponibilite = ancienneDisponibilite.copy(
                        placesReservees = (ancienneDisponibilite.placesReservees - 1).coerceAtLeast(0)
                    )
                    disponibiliteRepository.updateDisponibilite(updatedAncienneDisponibilite)
                }
                
                // Vu00e9rifier la nouvelle disponibilitu00e9
                val nouvellesDisponibilites = disponibiliteRepository.getDisponibilitesByDate(updatedReservation.date)
                val nouvelleDisponibilite = nouvellesDisponibilites.find { dispo ->
                    updatedReservation.heureDebut >= dispo.heureDebut && updatedReservation.heureFin <= dispo.heureFin
                }
                
                if (nouvelleDisponibilite == null) {
                    call.respond(HttpStatusCode.BadRequest, "Aucune disponibilitu00e9 pour la nouvelle date et ces heures")
                    return@put
                }
                
                // Vu00e9rifier s'il reste des places disponibles
                if (nouvelleDisponibilite.placesReservees >= nouvelleDisponibilite.capaciteTotale) {
                    call.respond(HttpStatusCode.Conflict, "Plus de places disponibles pour cette pu00e9riode")
                    return@put
                }
                
                // Ru00e9server une place dans la nouvelle disponibilitu00e9
                val updatedNouvelleDisponibilite = nouvelleDisponibilite.copy(
                    placesReservees = nouvelleDisponibilite.placesReservees + 1
                )
                disponibiliteRepository.updateDisponibilite(updatedNouvelleDisponibilite)
            }
            
            reservationRepository.updateReservation(updatedReservation.copy(id = id))
            call.respond(HttpStatusCode.OK, updatedReservation.copy(id = id))
        }

        // DELETE /api/reservations/{id} - Supprimer une ru00e9servation
        delete("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@delete
            }

            val reservation = reservationRepository.getReservationById(id)
            if (reservation == null) {
                call.respond(HttpStatusCode.NotFound, "Ru00e9servation non trouvu00e9e")
                return@delete
            }
            
            // Libu00e9rer la place si la ru00e9servation n'est pas du00e9ju00e0 annulu00e9e
            if (reservation.statut != StatutReservation.ANNULEE) {
                val disponibilites = disponibiliteRepository.getDisponibilitesByDate(reservation.date)
                val disponibilite = disponibilites.find { dispo ->
                    reservation.heureDebut >= dispo.heureDebut && reservation.heureFin <= dispo.heureFin
                }
                
                if (disponibilite != null) {
                    val updatedDisponibilite = disponibilite.copy(
                        placesReservees = (disponibilite.placesReservees - 1).coerceAtLeast(0)
                    )
                    disponibiliteRepository.updateDisponibilite(updatedDisponibilite)
                }
            }

            reservationRepository.deleteReservation(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}