#!/bin/bash

echo "Script de test de l'API de Gestion de Crèche"
echo "----------------------------------------"

# Vérifier si Python est installé
if ! command -v python3 &> /dev/null; then
    echo "❌ Python3 n'est pas installé. Veuillez l'installer pour exécuter les tests."
    exit 1
fi

# Vérifier si le module requests est installé
python3 -c "import requests" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "⚠️ Le module Python 'requests' n'est pas installé."
    read -p "Voulez-vous l'installer maintenant? (o/n): " install_requests
    if [ "$install_requests" = "o" ] || [ "$install_requests" = "O" ]; then
        echo "Installation du module requests..."
        pip install requests || pip3 install requests
        if [ $? -ne 0 ]; then
            echo "❌ Échec de l'installation du module requests. Veuillez l'installer manuellement avec 'pip install requests'."
            exit 1
        fi
        echo "✅ Module requests installé avec succès."
    else
        echo "❌ Le module requests est nécessaire pour exécuter les tests. Installation annulée."
        exit 1
    fi
fi

# Vérifier si l'application est en cours d'exécution
echo "Vérification de la disponibilité de l'API..."
if command -v curl &> /dev/null; then
    curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/parents > /dev/null
    if [ $? -ne 0 ]; then
        echo "⚠️ L'API ne semble pas être accessible. Assurez-vous que l'application est en cours d'exécution."
        read -p "Voulez-vous continuer quand même? (o/n): " continue_anyway
        if [ "$continue_anyway" != "o" ] && [ "$continue_anyway" != "O" ]; then
            echo "Tests annulés."
            exit 1
        fi
    else
        echo "✅ L'API est accessible."
    fi
else
    echo "⚠️ La commande curl n'est pas disponible. Impossible de vérifier la disponibilité de l'API."
    echo "Nous allons continuer, mais les tests peuvent échouer si l'API n'est pas accessible."
fi

# Exécuter les tests
echo "\nDémarrage des tests de l'API..."
echo "----------------------------------------"
python3 test_api.py

echo "\nTests terminés."