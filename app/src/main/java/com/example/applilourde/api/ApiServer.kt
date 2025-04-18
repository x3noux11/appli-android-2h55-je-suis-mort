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
        try {
            server = embeddedServer(Netty, port = port) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    })
                }
                configureRouting()
            }.start(wait = false)
            
            println("API Server started on port $port")
        } catch (e: Exception) {
            println("Failed to start API server: ${e.message}")
            e.printStackTrace()
            throw e
        }
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
        try {
            server?.stop(1000, 2000)
            println("API Server stopped")
        } catch (e: Exception) {
            println("Error stopping API server: ${e.message}")
            e.printStackTrace()
        } finally {
            server = null
        }
    }
}