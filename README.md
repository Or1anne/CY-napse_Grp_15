# CY-Napse


## Sommaire
- [Contenu](#contenu) Expliquer ce qu'est le projet ainsi que ses fonctionnalités
- [Objectif](#objectif) Expliquer le but de ce projet
- [Groupe](#groupe) Les membres du groupe
- [Fichiers](#fichiers) Quels sont les fichiers et leurs fonctions
- [Compiler](#compiler) Comment compiler le projet
- [Prérequis](#prérequis) Les bibliothèques à installer
- [Lancement](#lancement) Accéder à la page d'accueil du site


## Contenu

Ce projet propose une application Java avec une interface graphique permettant la création, l'affichage et la résolution de labyrinthes. 

Fonctionnalités principales :

- Génération de labyrinthes :
  - labyrinthes parfaits : sans cycles, un unique chemin entre deux points
  - labyrinthes imparfaits : avec des cycles ou des impasses
  - Algorinthme : Kruskal

- Résolution de labyrinthes :
  - affiche le chemin de la solution et son nombre de case
  - affiche le temps de résolution et le nombre de case parcours par l'algorithme
  - Algorithmes : BFS, Hand on wall (main gauche) et Trémaux

- Modification de labyrinthes :
  - ajout ou suppression de murs manuellement

- Interface utilisateur :
  - génération et affichage en terminal ou interface JAvaFX



## Objectif

Le but de ce projet est de développer une interface graphique interactive permettant de générer et résoudre des labyrinthes parfaits et imparfaits.
Ces labyrinthes sont construits et parcourus à l’aide d’algorithmes appliqués à des tableaux non orientés et acycliques.

L'application permet :
- la génération de labyrinthes à l’aide d’algorithme tel que Kruskal
- la résolution automatique via des stratégies comme Trémaux, BFS ou Hand on Wall 
- une visualisation terminale ou graphique du labyrinthe et de son chemin solution.

## Groupe

Notre groupe est le groupe 15 et est constitué d'Orianne Courtade, Alban Soupppaya, Loïc Robert, Mathis Lance-Richardot et Emilline Lam.

## Fichiers

```text
    - src : dossier contenant les codes sources
        - lib :
            -
        - main :
            - java :
                - com.example.nouveau : dossier contenant les fichiers Java principaux
                    - Case.java : représente une cellule d'un labyrinthe
                    - Database.java :  gère la connexion et les opérations sur la base de données
                    - Direction.java : énumération représentant les quatres directions (North, East, South, West)
                    - HelloApplication.java : connexion avec l'application JavaFX
                    - HelloController.java : contrôleur de la scène JavaFX pour la génération/résolution
                    - HomePageController.java : contrôleur de la page d'acceuil
                    - Main.java : 
                    - Maze.java : classe principale représentant le labyrinthe et contenant la logique de la génération
                    - Resolve.java : contient les algorithmes de résolution
            - ressources :
                - com.example.nouveau : dossier contenant les fichiers JavaFX principaux
                    - AdventureTimeLogo.ttf : police d'écriture utilisée dans l'interface
                    - ChargeScene.gif : animations de chargements
                    - ChargeSceneBackWard.gif : animations de chargements
                    - ChargeSceneFinal.gif : animations de chargements
                - hello-view.fxml : vue JavaFX liée à HelloController
                    - home-sans-cy.png : Image utilisée dans l'interface
                    - Homepage.css : ficher de style de la page d'accueil
                    - Homepage.fxml : Interface de la page d'accueil
                    - main.css : style général 
                    - StartNewMaze.gif : Animation lancée lors de la création d’un nouveau labyrinthe
    - target : contient les fichiers compilés
    - README.md : fichier de documentation du projet, instructions et description
    - Sauvergarde.db : base de données
```


## Compiler

Aller dans le dossier du projet puis ouvrir le terminal
```
 javac --module-path src\lib\javafx-sdk-21.0.7\lib --add-modules javafx.controls,javafx.fxml -d out src\main\java\com\example\nouveau\*.java
```

```
 xcopy src\main\resources\* out\ /E /Y
```

## Prérequis

Avoir le JDK21 au minimum

## Lancement

```
 java --module-path src\lib\javafx-sdk-21.0.7\lib --add-modules javafx.controls,javafx.fxml -cp out com.example.nouveau.HelloApplication
```

# CY_Tech
![CYTECH](CY_Tech_logo.jpg)





