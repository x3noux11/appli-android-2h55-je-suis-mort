package com.example.applilourde.data.model

import java.util.*

data class Parent(
    val id: String = UUID.randomUUID().toString(),
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val adresse: String,
    val enfants: MutableList<String> = mutableListOf() // Liste des IDs des enfants
)
