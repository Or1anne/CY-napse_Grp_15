# CY-Napse

## Sommaire

* [Contenu](#contenu) : Présentation du projet et de ses fonctionnalités
* [Objectif](#objectif) : But du projet
* [Groupe](#groupe) : Membres de l'équipe
* [Fichiers](#fichiers) : Liste des fichiers et leur fonction
* [Compilation](#compilation) : Instructions pour compiler le projet
* [Prérequis](#prérequis) : Bibliothèques nécessaires


## Contenu

Ce projet propose une application Java avec une interface graphique permettant la création, l'affichage et la résolution de labyrinthes.

Fonctionnalités principales :

* Génération de labyrinthes :
  * Labyrinthes parfaits : sans cycles, un unique chemin entre deux points
  * Labyrinthes imparfaits : avec des cycles ou des impasses
  * Algorithme : Kruskal

* Résolution de labyrinthes :
  * Affichage du chemin de la solution et de son nombre de cases
  * Affichage du temps de résolution et du nombre de cases parcourues par l'algorithme
  * Algorithmes : BFS, Hand-on-wall (main gauche) et Trémaux

* Modification de labyrinthes :
  * Ajout ou suppression de murs manuellement

* Interface utilisateur :
  * Génération et affichage dans le terminal ou via une interface JavaFX


## Objectif

Le but de ce projet est de développer une interface graphique interactive permettant de générer et résoudre des labyrinthes parfaits et imparfaits.
Ces labyrinthes sont construits et parcourus à l’aide d’algorithmes appliqués à des graphes non orientés et acycliques.

L'application permet :

* La génération de labyrinthes à l’aide d’un algorithme tel que Kruskal
* La résolution automatique via des stratégies comme Trémaux, BFS ou Hand-on-wall
* Une visualisation textuelle ou graphique du labyrinthe et de son chemin de solution


## Groupe

Notre groupe est le groupe 15 et est constitué de :
**Orianne Courtade, Alban Souppaya, Loïc Robert, Mathis Lance-Richardot et Emilline Lam.**


## Fichiers

```text
- src : dossier contenant les codes sources
    - lib : 
    - main :
        - java :
            - com.example.nouveau : dossier contenant les fichiers Java principaux
                - Case.java : représente une cellule d'un labyrinthe
                - Database.java : gère la connexion et les opérations sur la base de données
                - Direction.java : énumération représentant les quatre directions (North, East, South, West)
                - HelloApplication.java : point d’entrée de l’application JavaFX
                - HelloController.java : contrôleur de la scène JavaFX pour la génération/résolution
                - HomePageController.java : contrôleur de la page d'accueil
                - Main.java : gère la version terminal
                - Maze.java : classe principale représentant le labyrinthe, contient la logique de génération
                - Resolve.java : contient les algorithmes de résolution
        - resources :
            - com.example.nouveau :
                - AdventureTimeLogo.ttf : police utilisée dans l'interface
                - ChargeScene.gif, ChargeSceneBackWard.gif, ChargeSceneFinal.gif : animations de chargement
            - hello-view.fxml : vue JavaFX liée à HelloController
            - home-sans-cy.png : image utilisée dans l’interface
            - Homepage.css : feuille de style de la page d’accueil
            - Homepage.fxml : interface de la page d’accueil
            - main.css : style général
            - StartNewMaze.gif : animation lancée lors de la création d’un nouveau labyrinthe
- target : contient les fichiers compilés
- README.md : fichier de documentation du projet
- Sauvegarde.db : base de données
```
## Prérequis

**JDK 21** et **JavaFX SDK 21** minimum sont requis pour exécuter correctement l’application.

Lien d'installation de JavaFX SDK : https://gluonhq.com/products/javafx/

#### Pour ubuntu :
Si vous n'avez pas de JDK :
```
sudo apt update
```
```
sudo apt install openjdk-21-jdk
```

#### Pour Windows :

Lien d'installation du JDK : https://www.oracle.com/fr/java/technologies/downloads/#jdk21-linux

Après avoir lancé l'exécutable, aller dans Paramètres/Paramètres de Système avancé/Variables d'environnements/Double clic sur Path et ajouter le dossier bin jdk (Généralement il se trouve dans Programmes/Java/jdk-21/bin)


#### Si vous utilisez IntelliJ IDEA :
Ajoutez manuellement la bibliothèque `sqlite-jdbc` :
  * Naviguez jusqu'à `src/lib/jdk21.../lib`
  * Faites un clic droit sur le fichier `sqlite connectivity` puis sélectionnez `Add as Library`

## Compilation

#### Si vous utilisez le ternimal :

PATH est le chemin absolu du dossier lib se situant dans votre SDK

#### Si vous utilisez windows :
Pour compiler ouvrez un exécute de commande (PowerShell par exemple) dans le répertoire du projet et exécutez ces commande :
```bash
javac --module-path "PATH" --add-modules javafx.controls,javafx.fxml -cp "src\lib\sqlite-jdbc-3.49.1.0.jar" -d out src\main\java\com\example\nouveau\*.java
```
```bash
xcopy src\main\resources\* out\ /E /Y
```

Pour lancer l'application :
```bash
java --module-path "PATH" --add-modules javafx.controls,javafx.fxml -cp "out;src\lib\sqlite-jdbc-3.49.1.0.jar" com.example.nouveau.Main
```

#### Si vous utilisez Ubuntu :
Pour compiler :
```bash
javac --module-path PATH:src/lib/sqlite-jdbc-3.49.1.0.jar --add-modules javafx.controls,javafx.fxml -d out src/main/java/com/example/nouveau/*.java
```
```bash
cp -r src/main/resources/* out/
```

Pour lancer l'application :
```bash
java --module-path PATH:src/lib/sqlite-jdbc-3.49.1.0.jar --add-modules javafx.controls,javafx.fxml -cp out com.example.nouveau.Main
```


#### Si vous utilisez **IntelliJ IDEA** :
Allez dans la classe `Main` et lancez l’application directement.


# CY_Tech
![CYTECH](CY_Tech_logo.jpg)
