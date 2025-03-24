package com.example.applilourde.data.repository

import com.example.applilourde.data.model.Enfant

class EnfantRepository {
    private val enfants = mutableListOf<Enfant>()

    fun addEnfant(enfant: Enfant) {
        enfants.add(enfant)
    }

    fun getEnfantById(id: String): Enfant? {
        return enfants.find { it.id == id }
    }

    fun getEnfantsByParentId(parentId: String): List<Enfant> {
        return enfants.filter { it.parentId == parentId }
    }

    fun updateEnfant(enfant: Enfant) {
        val index = enfants.indexOfFirst { it.id == enfant.id }
        if (index != -1) {
            enfants[index] = enfant
        }
    }

    fun deleteEnfant(id: String) {
        enfants.removeIf { it.id == id }
    }

    fun getAllEnfants(): List<Enfant> {
        return enfants.toList()
    }

    // Simuler quelques donnu00e9es initiales
    init {
        addEnfant(Enfant(
            id = "1",
            nom = "Dupont",
            prenom = "Lu00e9a",
            dateNaissance = "2020-05-15",
            parentId = "1"
        ))
        addEnfant(Enfant(
            id = "2",
            nom = "Dupont",
            prenom = "Lucas",
            dateNaissance = "2022-02-10",
            parentId = "1"
        ))
        addEnfant(Enfant(
            id = "3",
            nom = "Martin",
            prenom = "Emma",
            dateNaissance = "2021-11-20",
            parentId = "2"
        ))
    }
}
