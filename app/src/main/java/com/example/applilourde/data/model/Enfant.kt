package com.example.applilourde.data.model

import java.util.*

data class Enfant(
    val id: String = UUID.randomUUID().toString(),
    val nom: String,
    val prenom: String,
    val dateNaissance: String,
    val parentId: String, // ID du parent responsable
    val informationsSpecifiques: String = ""
)
