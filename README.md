# Conseil des Schtroumpfs

Un jeu de simulation de stratégie et de gestion au tour par tour développé en **JavaFX** et orchestré avec **Maven**. Le joueur incarne le Conseil des Schtroumpfs et doit gérer les ressources d'un village (Baies, Salsepareille, Outils, Or, Savoir, Défense, Moral) sur un cycle de 12 mois tout en survivant à des crises et des événements aléatoires (Attaques de Gargamel, épidémies, tempêtes).

Ce projet universitaire a été conçu selon les méthodologies du **Design Centré Utilisateur (DCU)**, plaçant l'accessibilité et l'ergonomie des interfaces (IHM) au cœur du développement.

---

## Fonctionnalités Principales & Expérience IHM

L'application intègre des fonctionnalités avancées adaptées à quatre personas cibles (Thierry, Delphine, Dorian, Elsa) étudiés lors de la phase de conception :

* **Gestion Inclusive du Daltonisme (`ColorBlindManager`) :** Pour pallier les contraintes d'accessibilité visuelle, l'application bannit le codage colorimétrique binaire (Vert/Rouge) et applique via des matrices d'effet JavaFX (`ColorAdjust`) des filtres adaptés aux principales formes de daltonisme : *Deutéranopie*, *Protanopie*, *Tritanopie* et *Achromatopsie*.
* **Environnement Audio Adaptatif (`MediaManager`) :** Conçu pour les utilisateurs sensibles ou souffrant d'hyperacousie, le gestionnaire audio permet d'ajuster finement et indépendamment le volume de la musique d'ambiance et des effets sonores.
* **Système de Persistance Avancé (`SaveManager`) :** Permet des sessions de jeu fluides grâce à un système multi-slots (4 emplacements) sauvegardant l'intégralité de l'état du village ainsi que l'historique complet des tours dans un format tokenisé.
* **Boutique Premium Intégrée (`ShopViewController`) :** Simulation d'une boutique avec modèle de monétisation alternatif inclusif (Achat fictif par carte bancaire sécurisé par expressions régulières *Regex* ou déblocage alternatif par visionnage de publicité).
* **Tableau des scores (`Scoreboard`) :** Classement persistant trié enregistrant les performances des villages au fil des parties.

---

## Architecture Logicielle (Modèle-Vue-Contrôleur)

Le projet respecte une séparation stricte des responsabilités (MVC) afin de dissocier les règles métiers de la logique d'affichage.

### Le Modèle (`modele`)
* `SmurfVillage` : Cœur de la simulation. Gère la collection de personnages, l'historique et l'application des effets.
* `Character` & `ActionType` : Interface implémentée par les différents Schtroumpfs (Grand Schtroumpf, Schtroumpfette, Schtroumpf Farceur, etc.) définissant leurs actions uniques. Utilisation de la structure moderne `record` pour le `MagicianSmurf`.
* `Event` & `CrisisType` : Modélisation des aléas mensuels (Famine, Révolte, etc.) appliquant des modificateurs de ressources.
* `TurnRecord` : Enregistrement immuable (`record`) d'un état complet de fin de tour.

### Le Contrôleur (`controleur`)
* `JavaFXController` : Récupère les saisies des menus déroulants de la vue, valide les contraintes de ressources (ex: lancer une fête requiert des baies), applique les actions au modèle et déclenche les popups de crises.

### La Vue & les Interfaces (`vue` & `resources`)
* `HomeView.fxml` & `HomeViewController` : Écran d'accueil épuré (Hub) gérant l'aide/règles par pagination, les options d'accessibilité et le chargement.
* `GameView.fxml` & `GameViewController` : Vue principale disposée en triptyque fixe (Ressources à gauche, assignations au centre, menus contextuels). Utilisation d'un effet de flou gaussien (`GaussianBlur`) pour isoler visuellement les dialogues d'événements.
* `ShopView.fxml` & `ShopViewController` : Interface de la boutique rafraîchie dynamiquement lors du déblocage de Schtroumpfs Premium.

---

## Installation et Lancement

### Prérequis
* **Java Development Kit (JDK)** version **21** ou supérieure.
* **Apache Maven** installé et configuré.

### Lancer le jeu depuis un IDE (Eclipse / STS)
1.  Faites un **clic droit** sur la racine du projet.
2.  Sélectionnez **Run As** > **Maven build...**
3.  Dans la fenêtre de configuration qui s'ouvre, localisez le champ **Goals**.
4.  Saisissez la commande suivante :
    ```bash
    javafx:run
    ```
5.  Cliquez sur **Run** pour compiler et lancer l'application.

*Note : Pour vider les anciennes versions de compilation en cas de modification, vous pouvez exécuter un `mvn clean` au préalable.*

---

## Auteurs
Projet universitaire réalisé dans le cadre de l'UE Conception d'Applications et Interfaces/Interactions Homme-Machine (IHM).
