package com.example.applilourde.api

import com.example.applilourde.data.model.Parent
import com.example.applilourde.data.repository.ParentRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.parentRoutes(parentRepository: ParentRepository) {
    route("/api/parents") {
        // GET /api/parents - Récupérer tous les parents
        get {
            call.respond(parentRepository.getAllParents())
        }

        // GET /api/parents/{id} - Récupérer un parent par son ID
        get("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@get
            }

            val parent = parentRepository.getParentById(id)
            if (parent == null) {
                call.respond(HttpStatusCode.NotFound, "Parent non trouvé")
                return@get
            }

            call.respond(parent)
        }

        // POST /api/parents - Créer un nouveau parent
        post {
            val parent = call.receive<Parent>()
            
            // Vérifier si l'email existe déjà
            if (parentRepository.getParentByEmail(parent.email) != null) {
                call.respond(HttpStatusCode.Conflict, "Un parent avec cet email existe déjà")
                return@post
            }
            
            parentRepository.addParent(parent)
            call.respond(HttpStatusCode.Created, parent)
        }

        // PUT /api/parents/{id} - Mettre à jour un parent
        put("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@put
            }

            val existingParent = parentRepository.getParentById(id)
            if (existingParent == null) {
                call.respond(HttpStatusCode.NotFound, "Parent non trouvé")
                return@put
            }

            val updatedParent = call.receive<Parent>()
            
            // Vérifier si l'email existe déjà pour un autre parent
            val parentWithSameEmail = parentRepository.getParentByEmail(updatedParent.email)
            if (parentWithSameEmail != null && parentWithSameEmail.id != id) {
                call.respond(HttpStatusCode.Conflict, "Un autre parent avec cet email existe déjà")
                return@put
            }
            
            parentRepository.updateParent(updatedParent.copy(id = id))
            call.respond(HttpStatusCode.OK, updatedParent.copy(id = id))
        }

        // DELETE /api/parents/{id} - Supprimer un parent
        delete("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID manquant")
                return@delete
            }

            val parent = parentRepository.getParentById(id)
            if (parent == null) {
                call.respond(HttpStatusCode.NotFound, "Parent non trouvé")
                return@delete
            }

            parentRepository.deleteParent(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}