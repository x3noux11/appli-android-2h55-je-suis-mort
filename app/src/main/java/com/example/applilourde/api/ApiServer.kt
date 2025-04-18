package com.example.applilourde.api

import com.example.applilourde.data.repository.DisponibiliteRepository
import com.example.applilourde.data.repository.EnfantRepository
import com.example.applilourde.data.repository.ParentRepository
import com.example.applilourde.data.repository.ReservationRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

class ApiServer(
    private val parentRepository: ParentRepository,
    private val enfantRepository: EnfantRepository,
    private val disponibiliteRepository: DisponibiliteRepository,
    private val reservationRepository: ReservationRepository
) {
    private var server: ApplicationEngine? = null

    fun start(port: Int = 8080) {
        server = embeddedServer(Netty, port = port) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
            configureRouting()
        }.start(wait = false)
        
        println("API Server started on port $port")
    }

    private fun Application.configureRouting() {
        routing {
            // Configurer les routes pour les parents
            parentRoutes(parentRepository)
            
            // Configurer les routes pour les enfants
            enfantRoutes(enfantRepository, parentRepository)
            
            // Configurer les routes pour les disponibilités
            disponibiliteRoutes(disponibiliteRepository)
            
            // Configurer les routes pour les réservations
            reservationRoutes(reservationRepository, enfantRepository, disponibiliteRepository)
        }
    }

    fun stop() {
        server?.stop(1000, 2000)
        println("API Server stopped")
    }
}