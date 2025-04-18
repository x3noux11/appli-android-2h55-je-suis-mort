package com.example.applilourde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiAdminScreen(
    apiBaseUrl: String,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var apiStatus by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedEndpoint by remember { mutableStateOf(ApiEndpoint.PARENTS) }
    var responseData by remember { mutableStateOf<String?>(null) }
    
    val endpoints = listOf(
        ApiEndpoint.PARENTS,
        ApiEndpoint.ENFANTS,
        ApiEndpoint.DISPONIBILITES,
        ApiEndpoint.RESERVATIONS
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administration API") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Statut de l'API
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Statut de l'API",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when {
                                isLoading -> "Vérification..."
                                apiStatus != null -> "Statut: $apiStatus"
                                else -> "Statut: Non vérifié"
                            }
                        )
                        
                        Button(
                            onClick = {
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        val url = URL("$apiBaseUrl/api/parents")
                                        val connection = url.openConnection() as HttpURLConnection
                                        connection.requestMethod = "GET"
                                        connection.connectTimeout = 5000
                                        
                                        val responseCode = connection.responseCode
                                        apiStatus = if (responseCode == 200) {
                                            "En ligne (Code: $responseCode)"
                                        } else {
                                            "Erreur (Code: $responseCode)"
                                        }
                                    } catch (e: Exception) {
                                        apiStatus = "Hors ligne (${e.message})"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading
                        ) {
                            Text("Vérifier")
                        }
                    }
                }
            }
            
            // Sélection de l'endpoint
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tester les endpoints",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        endpoints.forEach { endpoint ->
                            FilterChip(
                                selected = selectedEndpoint == endpoint,
                                onClick = { selectedEndpoint = endpoint },
                                label = { Text(endpoint.displayName) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    val url = URL("$apiBaseUrl/api/${selectedEndpoint.path}")
                                    val connection = url.openConnection() as HttpURLConnection
                                    connection.requestMethod = "GET"
                                    
                                    val responseCode = connection.responseCode
                                    if (responseCode == 200) {
                                        val inputStream = connection.inputStream
                                        val response = inputStream.bufferedReader().use { it.readText() }
                                        responseData = response
                                    } else {
                                        responseData = "Erreur: Code $responseCode"
                                    }
                                } catch (e: Exception) {
                                    responseData = "Erreur: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Text("Tester GET ${selectedEndpoint.path}")
                    }
                }
            }
            
            // Résultat de la requête
            if (responseData != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Réponse:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                item {
                                    Text(responseData ?: "")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class ApiEndpoint(val path: String, val displayName: String) {
    PARENTS("parents", "Parents"),
    ENFANTS("enfants", "Enfants"),
    DISPONIBILITES("disponibilites", "Disponibilités"),
    RESERVATIONS("reservations", "Réservations")
}