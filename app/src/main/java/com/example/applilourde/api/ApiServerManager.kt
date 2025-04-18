package com.example.applilourde.api

import android.content.Context
import android.widget.Toast
import com.example.applilourde.data.repository.DisponibiliteRepository
import com.example.applilourde.data.repository.EnfantRepository
import com.example.applilourde.data.repository.ParentRepository
import com.example.applilourde.data.repository.ReservationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Classe utilitaire pour gérer le cycle de vie du serveur API
 */
class ApiServerManager(private val context: Context) {
    private var apiServer: ApiServer? = null
    private var isServerRunning = false
    
    fun startServer(
        parentRepository: ParentRepository,
        enfantRepository: EnfantRepository,
        disponibiliteRepository: DisponibiliteRepository,
        reservationRepository: ReservationRepository,
        port: Int = 8080,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (isServerRunning) {
            onSuccess()
            return
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiServer = ApiServer(
                    parentRepository = parentRepository,
                    enfantRepository = enfantRepository,
                    disponibiliteRepository = disponibiliteRepository,
                    reservationRepository = reservationRepository
                )
                
                apiServer?.start(port)
                isServerRunning = true
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "API démarrée sur le port $port", Toast.LENGTH_LONG).show()
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    val errorMessage = "Erreur lors du démarrage de l'API: ${e.message}"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    onError(errorMessage)
                }
            }
        }
    }
    
    fun stopServer() {
        if (!isServerRunning) return
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiServer?.stop()
                isServerRunning = false
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                apiServer = null
            }
        }
    }
    
    fun isRunning(): Boolean = isServerRunning
}