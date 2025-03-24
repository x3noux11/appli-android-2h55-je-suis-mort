package com.example.applilourde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.applilourde.data.repository.ParentRepository

@Composable
fun LoginScreen(
    parentRepository: ParentRepository, 
    onLoginSuccess: (String) -> Unit,
    onNavigateToInscription: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "RAM - Service de Crèche",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        Button(
            onClick = {
                val parent = parentRepository.getParentByEmail(email)
                if (parent != null) {
                    onLoginSuccess(parent.id)
                    errorMessage = ""
                } else {
                    errorMessage = "Email non reconnu. Veuillez vérifier votre adresse email."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Se connecter")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = { onNavigateToInscription() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nouveau parent ? S'inscrire")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Application réservée aux parents inscrits au service de crèche du RAM",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
