# Guide de Test de l'API de Gestion de Cru00e8che

Ce dossier contient des scripts pour tester l'API RESTful de gestion de cru00e8che. Ces scripts vous permettent de vu00e9rifier que tous les endpoints de l'API fonctionnent correctement.

## Pru00e9requis

- Python 3.6 ou supu00e9rieur
- Module Python `requests`
- L'application de gestion de cru00e8che doit u00eatre en cours d'exu00e9cution

## Installation

1. Assurez-vous que Python est installu00e9 sur votre systu00e8me :
   ```
   python3 --version
   ```

2. Installez le module `requests` si nu00e9cessaire :
   ```
   pip install requests
   ```
   ou
   ```
   pip3 install requests
   ```

## Utilisation

### Mu00e9thode 1 : Utiliser le script shell (recommandu00e9 pour Linux/Mac)

1. Rendez le script exu00e9cutable :
   ```
   chmod +x test_api.sh
   ```

2. Exu00e9cutez le script :
   ```
   ./test_api.sh
   ```

### Mu00e9thode 2 : Exu00e9cuter directement le script Python

1. Assurez-vous que l'application est en cours d'exu00e9cution

2. Exu00e9cutez le script Python :
   ```
   python3 test_api.py
   ```

## Fonctionnement

Le script de test effectue les opu00e9rations suivantes :

1. Vu00e9rifie la connexion u00e0 l'API
2. Teste les endpoints des parents (cru00e9ation, ru00e9cupu00e9ration, mise u00e0 jour)
3. Teste les endpoints des enfants (cru00e9ation, ru00e9cupu00e9ration, mise u00e0 jour)
4. Teste les endpoints des disponibilitu00e9s (cru00e9ation, ru00e9cupu00e9ration, mise u00e0 jour)
5. Teste les endpoints des ru00e9servations (cru00e9ation, ru00e9cupu00e9ration, mise u00e0 jour)

Chaque test affiche les ru00e9sultats des requu00eates avec les codes de statut HTTP et les donnu00e9es retournu00e9es par l'API.

## Personnalisation

Vous pouvez modifier le fichier `test_api.py` pour :

- Changer l'URL de base de l'API (par du00e9faut : `http://localhost:8080`)
- Modifier les donnu00e9es utilisu00e9es pour les tests
- Activer/du00e9sactiver certains tests spu00e9cifiques

## Ru00e9solution des problu00e8mes

Si vous rencontrez des erreurs lors de l'exu00e9cution des tests :

1. Assurez-vous que l'application est bien en cours d'exu00e9cution
2. Vu00e9rifiez que l'API est accessible u00e0 l'adresse `http://localhost:8080`
3. Consultez les messages d'erreur pour identifier le problu00e8me spu00e9cifique