import requests
import json
import sys
from datetime import datetime, timedelta

# Configuration de base
BASE_URL = "http://localhost:8080"

# Fonction pour afficher les résultats de manière formatée
def print_response(response, message=""):
    print(f"\n{message}")
    print(f"Status Code: {response.status_code}")
    try:
        print(json.dumps(response.json(), indent=2))
    except:
        print(response.text)

# Fonction pour tester les endpoints des parents
def test_parents():
    print("\n=== Test des endpoints Parents ===")
    
    # GET tous les parents
    response = requests.get(f"{BASE_URL}/api/parents")
    print_response(response, "Liste de tous les parents:")
    
    # POST créer un nouveau parent
    new_parent = {
        "nom": "Dupont",
        "prenom": "Jean",
        "email": "jean.dupont@example.com",
        "telephone": "0612345678",
        "adresse": "123 Rue de Paris, 75001 Paris"
    }
    response = requests.post(f"{BASE_URL}/api/parents", json=new_parent)
    print_response(response, "Création d'un nouveau parent:")
    
    # Récupérer l'ID du parent créé
    try:
        parent_id = response.json().get("id")
        
        # GET parent par ID
        response = requests.get(f"{BASE_URL}/api/parents/{parent_id}")
        print_response(response, f"Récupération du parent avec ID {parent_id}:")
        
        # PUT mettre à jour un parent
        updated_parent = {
            "nom": "Dupont",
            "prenom": "Jean-Pierre",  # Prénom modifié
            "email": "jeanpierre.dupont@example.com",  # Email modifié
            "telephone": "0612345678",
            "adresse": "123 Rue de Paris, 75001 Paris"
        }
        response = requests.put(f"{BASE_URL}/api/parents/{parent_id}", json=updated_parent)
        print_response(response, f"Mise à jour du parent avec ID {parent_id}:")
        
        # DELETE supprimer un parent (commenté pour ne pas supprimer le parent pour les tests suivants)
        # response = requests.delete(f"{BASE_URL}/api/parents/{parent_id}")
        # print_response(response, f"Suppression du parent avec ID {parent_id}:")
        
        return parent_id
    except:
        print("Erreur lors de la récupération de l'ID du parent")
        return None

# Fonction pour tester les endpoints des enfants
def test_enfants(parent_id=None):
    print("\n=== Test des endpoints Enfants ===")
    
    # GET tous les enfants
    response = requests.get(f"{BASE_URL}/api/enfants")
    print_response(response, "Liste de tous les enfants:")
    
    if parent_id:
        # POST créer un nouvel enfant
        today = datetime.now()
        birth_date = (today - timedelta(days=365*3)).strftime("%Y-%m-%d")  # 3 ans
        
        new_enfant = {
            "nom": "Dupont",
            "prenom": "Sophie",
            "dateNaissance": birth_date,
            "parentId": parent_id,
            "informationsSpecifiques": "Allergique aux arachides"
        }
        response = requests.post(f"{BASE_URL}/api/enfants", json=new_enfant)
        print_response(response, "Création d'un nouvel enfant:")
        
        # Récupérer l'ID de l'enfant créé
        try:
            enfant_id = response.json().get("id")
            
            # GET enfant par ID
            response = requests.get(f"{BASE_URL}/api/enfants/{enfant_id}")
            print_response(response, f"Récupération de l'enfant avec ID {enfant_id}:")
            
            # GET enfants d'un parent
            response = requests.get(f"{BASE_URL}/api/enfants/parent/{parent_id}")
            print_response(response, f"Récupération des enfants du parent avec ID {parent_id}:")
            
            # PUT mettre à jour un enfant
            updated_enfant = {
                "nom": "Dupont",
                "prenom": "Sophie",
                "dateNaissance": birth_date,
                "parentId": parent_id,
                "informationsSpecifiques": "Allergique aux arachides et aux fruits de mer"  # Info modifiée
            }
            response = requests.put(f"{BASE_URL}/api/enfants/{enfant_id}", json=updated_enfant)
            print_response(response, f"Mise à jour de l'enfant avec ID {enfant_id}:")
            
            # DELETE supprimer un enfant (commenté pour ne pas supprimer l'enfant pour les tests suivants)
            # response = requests.delete(f"{BASE_URL}/api/enfants/{enfant_id}")
            # print_response(response, f"Suppression de l'enfant avec ID {enfant_id}:")
            
            return enfant_id
        except:
            print("Erreur lors de la récupération de l'ID de l'enfant")
            return None
    else:
        print("Impossible de créer un enfant sans parent_id")
        return None

# Fonction pour tester les endpoints des disponibilités
def test_disponibilites():
    print("\n=== Test des endpoints Disponibilités ===")
    
    # GET toutes les disponibilités
    response = requests.get(f"{BASE_URL}/api/disponibilites")
    print_response(response, "Liste de toutes les disponibilités:")
    
    # POST créer une nouvelle disponibilité
    tomorrow = (datetime.now() + timedelta(days=1)).strftime("%Y-%m-%d")
    
    new_disponibilite = {
        "date": tomorrow,
        "heureDebut": "08:00",
        "heureFin": "18:00",
        "capaciteTotale": 10,
        "placesReservees": 0,
        "typeGarde": "REGULIER"
    }
    response = requests.post(f"{BASE_URL}/api/disponibilites", json=new_disponibilite)
    print_response(response, "Création d'une nouvelle disponibilité:")
    
    # Récupérer l'ID de la disponibilité créée
    try:
        disponibilite_id = response.json().get("id")
        
        # GET disponibilité par ID
        response = requests.get(f"{BASE_URL}/api/disponibilites/{disponibilite_id}")
        print_response(response, f"Récupération de la disponibilité avec ID {disponibilite_id}:")
        
        # GET disponibilités pour une date
        response = requests.get(f"{BASE_URL}/api/disponibilites/date/{tomorrow}")
        print_response(response, f"Récupération des disponibilités pour la date {tomorrow}:")
        
        # PUT mettre à jour une disponibilité
        updated_disponibilite = {
            "date": tomorrow,
            "heureDebut": "08:00",
            "heureFin": "18:00",
            "capaciteTotale": 15,  # Capacité modifiée
            "placesReservees": 0,
            "typeGarde": "REGULIER"
        }
        response = requests.put(f"{BASE_URL}/api/disponibilites/{disponibilite_id}", json=updated_disponibilite)
        print_response(response, f"Mise à jour de la disponibilité avec ID {disponibilite_id}:")
        
        # DELETE supprimer une disponibilité (commenté pour ne pas supprimer la disponibilité pour les tests suivants)
        # response = requests.delete(f"{BASE_URL}/api/disponibilites/{disponibilite_id}")
        # print_response(response, f"Suppression de la disponibilité avec ID {disponibilite_id}:")
        
        return disponibilite_id, tomorrow
    except:
        print("Erreur lors de la récupération de l'ID de la disponibilité")
        return None, tomorrow

# Fonction pour tester les endpoints des réservations
def test_reservations(enfant_id=None, date=None):
    print("\n=== Test des endpoints Réservations ===")
    
    # GET toutes les réservations
    response = requests.get(f"{BASE_URL}/api/reservations")
    print_response(response, "Liste de toutes les réservations:")
    
    if enfant_id and date:
        # POST créer une nouvelle réservation
        new_reservation = {
            "enfantId": enfant_id,
            "date": date,
            "heureDebut": "09:00",
            "heureFin": "12:00",
            "statut": "EN_ATTENTE"
        }
        response = requests.post(f"{BASE_URL}/api/reservations", json=new_reservation)
        print_response(response, "Création d'une nouvelle réservation:")
        
        # Récupérer l'ID de la réservation créée
        try:
            reservation_id = response.json().get("id")
            
            # GET réservation par ID
            response = requests.get(f"{BASE_URL}/api/reservations/{reservation_id}")
            print_response(response, f"Récupération de la réservation avec ID {reservation_id}:")
            
            # GET réservations d'un enfant
            response = requests.get(f"{BASE_URL}/api/reservations/enfant/{enfant_id}")
            print_response(response, f"Récupération des réservations de l'enfant avec ID {enfant_id}:")
            
            # GET réservations pour une date
            response = requests.get(f"{BASE_URL}/api/reservations/date/{date}")
            print_response(response, f"Récupération des réservations pour la date {date}:")
            
            # PUT mettre à jour une réservation
            updated_reservation = {
                "enfantId": enfant_id,
                "date": date,
                "heureDebut": "09:00",
                "heureFin": "12:00",
                "statut": "CONFIRMEE"  # Statut modifié
            }
            response = requests.put(f"{BASE_URL}/api/reservations/{reservation_id}", json=updated_reservation)
            print_response(response, f"Mise à jour de la réservation avec ID {reservation_id}:")
            
            # DELETE supprimer une réservation
            # response = requests.delete(f"{BASE_URL}/api/reservations/{reservation_id}")
            # print_response(response, f"Suppression de la réservation avec ID {reservation_id}:")
            
            return reservation_id
        except:
            print("Erreur lors de la récupération de l'ID de la réservation")
            return None
    else:
        print("Impossible de créer une réservation sans enfant_id ou date")
        return None

# Fonction principale pour exécuter tous les tests
def run_all_tests():
    try:
        print("\n==== DÉBUT DES TESTS DE L'API DE GESTION DE CRÈCHE ====\n")
        print(f"URL de base: {BASE_URL}")
        
        # Test de connexion à l'API
        try:
            response = requests.get(f"{BASE_URL}/api/parents")
            if response.status_code == 200:
                print("✅ Connexion à l'API réussie!")
            else:
                print(f"❌ Erreur de connexion à l'API. Code: {response.status_code}")
                return
        except requests.exceptions.ConnectionError:
            print("❌ Impossible de se connecter à l'API. Assurez-vous que l'application est en cours d'exécution.")
            return
        
        # Exécuter les tests dans l'ordre logique
        parent_id = test_parents()
        enfant_id = test_enfants(parent_id)
        disponibilite_id, date = test_disponibilites()
        reservation_id = test_reservations(enfant_id, date)
        
        print("\n==== FIN DES TESTS DE L'API DE GESTION DE CRÈCHE ====\n")
        print("Résumé:")
        print(f"- Parent créé avec ID: {parent_id}")
        print(f"- Enfant créé avec ID: {enfant_id}")
        print(f"- Disponibilité créée avec ID: {disponibilite_id}")
        print(f"- Réservation créée avec ID: {reservation_id}")
        
    except Exception as e:
        print(f"\n❌ Une erreur s'est produite lors des tests: {str(e)}")

# Point d'entrée du script
if __name__ == "__main__":
    run_all_tests()