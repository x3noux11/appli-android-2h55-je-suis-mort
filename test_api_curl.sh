#!/bin/bash

echo "Test de l'API de Gestion de Cru00e8che avec cURL"
echo "------------------------------------------"

# Configuration
BASE_URL="http://localhost:8080"
TODAY=$(date +"%Y-%m-%d")
TOMORROW=$(date -d "tomorrow" +"%Y-%m-%d" 2>/dev/null || date -v+1d +"%Y-%m-%d")

# Fonction pour afficher les ru00e9sultats
display_result() {
    echo "\n$1"
    echo "------------------------------------------"
    if [ "$3" == "pretty" ] && command -v jq &> /dev/null; then
        echo "$2" | jq .
    else
        echo "$2"
    fi
    echo "------------------------------------------"
}

# Vu00e9rifier si curl est installu00e9
if ! command -v curl &> /dev/null; then
    echo "u274c cURL n'est pas installu00e9. Veuillez l'installer pour exu00e9cuter les tests."
    exit 1
fi

# Vu00e9rifier si jq est installu00e9 (optionnel, pour un affichage plus joli)
if ! command -v jq &> /dev/null; then
    echo "u26a0ufe0f jq n'est pas installu00e9. L'affichage JSON ne sera pas formatu00e9."
    echo "Pour installer jq: 'apt-get install jq' (Debian/Ubuntu) ou 'brew install jq' (macOS)"
    PRETTY=""
else
    PRETTY="pretty"
fi

# Vu00e9rifier si l'API est accessible
echo "Vu00e9rification de la disponibilitu00e9 de l'API..."
API_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/parents")
if [ "$API_STATUS" != "200" ]; then
    echo "u274c L'API n'est pas accessible (code HTTP: $API_STATUS). Assurez-vous que l'application est en cours d'exu00e9cution."
    exit 1
fi
echo "u2705 L'API est accessible."

echo "\nDu00e9but des tests..."

# 1. Test des endpoints Parents
echo "\n=== Test des endpoints Parents ==="

# GET tous les parents
RESULT=$(curl -s "$BASE_URL/api/parents")
display_result "Liste de tous les parents:" "$RESULT" "$PRETTY"

# POST cru00e9er un nouveau parent
echo "Cru00e9ation d'un nouveau parent..."
PARENT_RESULT=$(curl -s -X POST "$BASE_URL/api/parents" \
    -H "Content-Type: application/json" \
    -d '{"nom": "Martin", "prenom": "Sophie", "email": "sophie.martin@example.com", "telephone": "0612345678", "adresse": "123 Rue des Lilas, 75004 Paris"}')
display_result "Parent cru00e9u00e9:" "$PARENT_RESULT" "$PRETTY"

# Extraire l'ID du parent cru00e9u00e9
if command -v jq &> /dev/null; then
    PARENT_ID=$(echo "$PARENT_RESULT" | jq -r '.id')
else
    # Extraction basique sans jq (moins fiable)
    PARENT_ID=$(echo "$PARENT_RESULT" | grep -o '"id":"[^"]*"' | cut -d '"' -f 4)
fi

if [ -z "$PARENT_ID" ] || [ "$PARENT_ID" == "null" ]; then
    echo "u274c Impossible d'extraire l'ID du parent. Les tests suivants peuvent u00e9chouer."
else
    echo "u2705 ID du parent extrait: $PARENT_ID"
    
    # GET parent par ID
    RESULT=$(curl -s "$BASE_URL/api/parents/$PARENT_ID")
    display_result "Ru00e9cupu00e9ration du parent avec ID $PARENT_ID:" "$RESULT" "$PRETTY"
    
    # PUT mettre u00e0 jour un parent
    echo "Mise u00e0 jour du parent..."
    RESULT=$(curl -s -X PUT "$BASE_URL/api/parents/$PARENT_ID" \
        -H "Content-Type: application/json" \
        -d '{"nom": "Martin", "prenom": "Sophie", "email": "sophie.martin.pro@example.com", "telephone": "0612345678", "adresse": "123 Rue des Lilas, 75004 Paris"}')
    display_result "Parent mis u00e0 jour:" "$RESULT" "$PRETTY"
    
    # 2. Test des endpoints Enfants
    echo "\n=== Test des endpoints Enfants ==="
    
    # GET tous les enfants
    RESULT=$(curl -s "$BASE_URL/api/enfants")
    display_result "Liste de tous les enfants:" "$RESULT" "$PRETTY"
    
    # POST cru00e9er un nouvel enfant
    echo "Cru00e9ation d'un nouvel enfant..."
    ENFANT_RESULT=$(curl -s -X POST "$BASE_URL/api/enfants" \
        -H "Content-Type: application/json" \
        -d '{"nom": "Martin", "prenom": "Lucas", "dateNaissance": "2020-01-15", "parentId": "'"$PARENT_ID"'", "informationsSpecifiques": "Allergique au lait"}')
    display_result "Enfant cru00e9u00e9:" "$ENFANT_RESULT" "$PRETTY"
    
    # Extraire l'ID de l'enfant cru00e9u00e9
    if command -v jq &> /dev/null; then
        ENFANT_ID=$(echo "$ENFANT_RESULT" | jq -r '.id')
    else
        # Extraction basique sans jq (moins fiable)
        ENFANT_ID=$(echo "$ENFANT_RESULT" | grep -o '"id":"[^"]*"' | cut -d '"' -f 4)
    fi
    
    if [ -z "$ENFANT_ID" ] || [ "$ENFANT_ID" == "null" ]; then
        echo "u274c Impossible d'extraire l'ID de l'enfant. Les tests suivants peuvent u00e9chouer."
    else
        echo "u2705 ID de l'enfant extrait: $ENFANT_ID"
        
        # GET enfant par ID
        RESULT=$(curl -s "$BASE_URL/api/enfants/$ENFANT_ID")
        display_result "Ru00e9cupu00e9ration de l'enfant avec ID $ENFANT_ID:" "$RESULT" "$PRETTY"
        
        # GET enfants d'un parent
        RESULT=$(curl -s "$BASE_URL/api/enfants/parent/$PARENT_ID")
        display_result "Ru00e9cupu00e9ration des enfants du parent avec ID $PARENT_ID:" "$RESULT" "$PRETTY"
        
        # 3. Test des endpoints Disponibilitu00e9s
        echo "\n=== Test des endpoints Disponibilitu00e9s ==="
        
        # GET toutes les disponibilitu00e9s
        RESULT=$(curl -s "$BASE_URL/api/disponibilites")
        display_result "Liste de toutes les disponibilitu00e9s:" "$RESULT" "$PRETTY"
        
        # POST cru00e9er une nouvelle disponibilitu00e9
        echo "Cru00e9ation d'une nouvelle disponibilitu00e9..."
        DISPO_RESULT=$(curl -s -X POST "$BASE_URL/api/disponibilites" \
            -H "Content-Type: application/json" \
            -d '{"date": "'"$TOMORROW"'", "heureDebut": "08:00", "heureFin": "18:00", "capaciteTotale": 10, "placesReservees": 0, "typeGarde": "REGULIER"}')
        display_result "Disponibilitu00e9 cru00e9u00e9e:" "$DISPO_RESULT" "$PRETTY"
        
        # Extraire l'ID de la disponibilitu00e9 cru00e9u00e9e
        if command -v jq &> /dev/null; then
            DISPO_ID=$(echo "$DISPO_RESULT" | jq -r '.id')
        else
            # Extraction basique sans jq (moins fiable)
            DISPO_ID=$(echo "$DISPO_RESULT" | grep -o '"id":"[^"]*"' | cut -d '"' -f 4)
        fi
        
        if [ -z "$DISPO_ID" ] || [ "$DISPO_ID" == "null" ]; then
            echo "u274c Impossible d'extraire l'ID de la disponibilitu00e9. Les tests suivants peuvent u00e9chouer."
        else
            echo "u2705 ID de la disponibilitu00e9 extrait: $DISPO_ID"
            
            # GET disponibilitu00e9 par ID
            RESULT=$(curl -s "$BASE_URL/api/disponibilites/$DISPO_ID")
            display_result "Ru00e9cupu00e9ration de la disponibilitu00e9 avec ID $DISPO_ID:" "$RESULT" "$PRETTY"
            
            # GET disponibilitu00e9s pour une date
            RESULT=$(curl -s "$BASE_URL/api/disponibilites/date/$TOMORROW")
            display_result "Ru00e9cupu00e9ration des disponibilitu00e9s pour la date $TOMORROW:" "$RESULT" "$PRETTY"
            
            # 4. Test des endpoints Ru00e9servations
            echo "\n=== Test des endpoints Ru00e9servations ==="
            
            # GET toutes les ru00e9servations
            RESULT=$(curl -s "$BASE_URL/api/reservations")
            display_result "Liste de toutes les ru00e9servations:" "$RESULT" "$PRETTY"
            
            # POST cru00e9er une nouvelle ru00e9servation
            echo "Cru00e9ation d'une nouvelle ru00e9servation..."
            RESERVATION_RESULT=$(curl -s -X POST "$BASE_URL/api/reservations" \
                -H "Content-Type: application/json" \
                -d '{"enfantId": "'"$ENFANT_ID"'", "date": "'"$TOMORROW"'", "heureDebut": "09:00", "heureFin": "12:00", "statut": "EN_ATTENTE"}')
            display_result "Ru00e9servation cru00e9u00e9e:" "$RESERVATION_RESULT" "$PRETTY"
            
            # Extraire l'ID de la ru00e9servation cru00e9u00e9e
            if command -v jq &> /dev/null; then
                RESERVATION_ID=$(echo "$RESERVATION_RESULT" | jq -r '.id')
            else
                # Extraction basique sans jq (moins fiable)
                RESERVATION_ID=$(echo "$RESERVATION_RESULT" | grep -o '"id":"[^"]*"' | cut -d '"' -f 4)
            fi
            
            if [ -z "$RESERVATION_ID" ] || [ "$RESERVATION_ID" == "null" ]; then
                echo "u274c Impossible d'extraire l'ID de la ru00e9servation."
            else
                echo "u2705 ID de la ru00e9servation extrait: $RESERVATION_ID"
                
                # GET ru00e9servation par ID
                RESULT=$(curl -s "$BASE_URL/api/reservations/$RESERVATION_ID")
                display_result "Ru00e9cupu00e9ration de la ru00e9servation avec ID $RESERVATION_ID:" "$RESULT" "$PRETTY"
                
                # GET ru00e9servations d'un enfant
                RESULT=$(curl -s "$BASE_URL/api/reservations/enfant/$ENFANT_ID")
                display_result "Ru00e9cupu00e9ration des ru00e9servations de l'enfant avec ID $ENFANT_ID:" "$RESULT" "$PRETTY"
                
                # GET ru00e9servations pour une date
                RESULT=$(curl -s "$BASE_URL/api/reservations/date/$TOMORROW")
                display_result "Ru00e9cupu00e9ration des ru00e9servations pour la date $TOMORROW:" "$RESULT" "$PRETTY"
                
                # PUT mettre u00e0 jour une ru00e9servation
                echo "Mise u00e0 jour de la ru00e9servation..."
                RESULT=$(curl -s -X PUT "$BASE_URL/api/reservations/$RESERVATION_ID" \
                    -H "Content-Type: application/json" \
                    -d '{"enfantId": "'"$ENFANT_ID"'", "date": "'"$TOMORROW"'", "heureDebut": "09:00", "heureFin": "12:00", "statut": "CONFIRMEE"}')
                display_result "Ru00e9servation mise u00e0 jour:" "$RESULT" "$PRETTY"
            fi
        fi
    fi
fi

echo "\n=== Ru00e9sumu00e9 des tests ==="
echo "Tests terminu00e9s. Entitu00e9s cru00e9u00e9es:"
echo "- Parent ID: $PARENT_ID"
echo "- Enfant ID: $ENFANT_ID"
echo "- Disponibilitu00e9 ID: $DISPO_ID"
echo "- Ru00e9servation ID: $RESERVATION_ID"

echo "\nPour nettoyer les donnu00e9es de test, vous pouvez utiliser les commandes DELETE de l'API."