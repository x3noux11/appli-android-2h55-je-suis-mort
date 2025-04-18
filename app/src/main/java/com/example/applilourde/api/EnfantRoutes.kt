package com.example.applilourde.api

import com.example.applilourde.data.model.Enfant
import com.example.applilourde.data.repository.EnfantRepository
import com.example.applilourde.data.repository.ParentRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.enfantRoutes(enfantRepository: EnfantRepository, parentRepository: ParentRepository) {
    route("/api/enfants") {
        // GET /api/enfants - Ru00e9cupu00e9rer tous les enfants
        get {
            call.respond(enfantRepository.getAllEnfants())
        }

        // GET /api/enfants/{id} - Ru00e9cupu00e9rer un enfant par son ID
        get("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@get
            }

            val enfant = enfantRepository.getEnfantById(id)
            if (enfant == null) {
                call.respond(HttpStatusCode.NotFound, "Enfant non trouvu00e9")
                return@get
            }

            call.respond(enfant)
        }

        // GET /api/enfants/parent/{parentId} - Ru00e9cupu00e9rer tous les enfants d'un parent
        get("/parent/{parentId}") {
            val parentId = call.parameters["parentId"]
            if (parentId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID du parent manquant")
                return@get
            }

            val parent = parentRepository.getParentById(parentId)
            if (parent == null) {
                call.respond(HttpStatusCode.NotFound, "Parent non trouvu00e9")
                return@get
            }

            val enfants = enfantRepository.getEnfantsByParentId(parentId)
            call.respond(enfants)
        }

        // POST /api/enfants - Cru00e9er un nouvel enfant
        post {
            val enfant = call.receive<Enfant>()
            
            // Vu00e9rifier si le parent existe
            val parent = parentRepository.getParentById(enfant.parentId)
            if (parent == null) {
                call.respond(HttpStatusCode.BadRequest, "Le parent spu00e9cifiu00e9 n'existe pas")
                return@post
            }
            
            enfantRepository.addEnfant(enfant)
            
            // Mettre u00e0 jour la liste des enfants du parent
            val updatedParent = parent.copy(
                enfants = parent.enfants.apply { add(enfant.id) }
            )
            parentRepository.updateParent(updatedParent)
            
            call.respond(HttpStatusCode.Created, enfant)
        }

        // PUT /api/enfants/{id} - Mettre u00e0 jour un enfant
        put("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@put
            }

            val existingEnfant = enfantRepository.getEnfantById(id)
            if (existingEnfant == null) {
                call.respond(HttpStatusCode.NotFound, "Enfant non trouvu00e9")
                return@put
            }

            val updatedEnfant = call.receive<Enfant>()
            
            // Vu00e9rifier si le parent a changu00e9
            if (existingEnfant.parentId != updatedEnfant.parentId) {
                // Vu00e9rifier si le nouveau parent existe
                val newParent = parentRepository.getParentById(updatedEnfant.parentId)
                if (newParent == null) {
                    call.respond(HttpStatusCode.BadRequest, "Le nouveau parent spu00e9cifiu00e9 n'existe pas")
                    return@put
                }
                
                // Retirer l'enfant de l'ancien parent
                val oldParent = parentRepository.getParentById(existingEnfant.parentId)
                if (oldParent != null) {
                    val updatedOldParent = oldParent.copy(
                        enfants = oldParent.enfants.apply { remove(id) }
                    )
                    parentRepository.updateParent(updatedOldParent)
                }
                
                // Ajouter l'enfant au nouveau parent
                val updatedNewParent = newParent.copy(
                    enfants = newParent.enfants.apply { add(id) }
                )
                parentRepository.updateParent(updatedNewParent)
            }
            
            enfantRepository.updateEnfant(updatedEnfant.copy(id = id))
            call.respond(HttpStatusCode.OK, updatedEnfant.copy(id = id))
        }

        // DELETE /api/enfants/{id} - Supprimer un enfant
        delete("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@delete
            }

            val enfant = enfantRepository.getEnfantById(id)
            if (enfant == null) {
                call.respond(HttpStatusCode.NotFound, "Enfant non trouvu00e9")
                return@delete
            }

            // Retirer l'enfant du parent
            val parent = parentRepository.getParentById(enfant.parentId)
            if (parent != null) {
                val updatedParent = parent.copy(
                    enfants = parent.enfants.apply { remove(id) }
                )
                parentRepository.updateParent(updatedParent)
            }
            
            enfantRepository.deleteEnfant(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}