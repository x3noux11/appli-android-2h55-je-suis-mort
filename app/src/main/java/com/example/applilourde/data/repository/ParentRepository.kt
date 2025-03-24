package com.example.applilourde.data.repository

import com.example.applilourde.data.model.Parent

class ParentRepository {
    private val parents = mutableListOf<Parent>()

    fun addParent(parent: Parent) {
        parents.add(parent)
    }

    fun getParentById(id: String): Parent? {
        return parents.find { it.id == id }
    }

    fun getParentByEmail(email: String): Parent? {
        return parents.find { it.email == email }
    }

    fun updateParent(parent: Parent) {
        val index = parents.indexOfFirst { it.id == parent.id }
        if (index != -1) {
            parents[index] = parent
        }
    }

    fun deleteParent(id: String) {
        parents.removeIf { it.id == id }
    }

    fun getAllParents(): List<Parent> {
        return parents.toList()
    }

    // Simuler quelques données initiales
    init {
        addParent(Parent(
            id = "1",
            nom = "Dupont",
            prenom = "Jean",
            email = "jean.dupont@example.com",
            telephone = "0612345678",
            adresse = "123 Avenue des Lilas, 75001 Paris"
        ))
        addParent(Parent(
            id = "2",
            nom = "Martin",
            prenom = "Sophie",
            email = "sophie.martin@example.com",
            telephone = "0687654321",
            adresse = "456 Rue des Roses, 75002 Paris"
        ))
    }
}
