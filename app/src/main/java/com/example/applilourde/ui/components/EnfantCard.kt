package com.example.applilourde.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.applilourde.data.model.Enfant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnfantCard(enfant: Enfant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${enfant.prenom} ${enfant.nom}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Nu00e9(e) le ${enfant.dateNaissance}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                
                if (enfant.informationsSpecifiques.isNotEmpty()) {
                    Text(
                        text = enfant.informationsSpecifiques,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Button(
                onClick = { onClick() },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Ru00e9server")
            }
        }
    }
}
