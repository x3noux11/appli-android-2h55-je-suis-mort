#!/bin/bash

# Remplacer le fichier InscriptionScreen.kt par la version corrigée
cp app/src/main/java/com/example/applilourde/ui/screens/InscriptionScreen.kt.new app/src/main/java/com/example/applilourde/ui/screens/InscriptionScreen.kt

# Supprimer le fichier temporaire
rm app/src/main/java/com/example/applilourde/ui/screens/InscriptionScreen.kt.new

echo "Le fichier InscriptionScreen.kt a été remplacé avec succès."