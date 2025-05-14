package com.example.nouveau;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Maze currentMaze;
    private static final Scanner sc = new Scanner(System.in);
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        System.out.println(ANSI_YELLOW+
                "  ______ ____    ____         .__   __.      ___           _______..______    _______ \n" +
                        " /      |\\   \\  /   /         |  \\ |  |     /   \\         /       ||   _  \\  |   ____|\n" +
                        "|  ,----' \\   \\/   /   ______ |   \\|  |    /  ^  \\       |   (----`|  |_)  | |  |__   \n" +
                        "|  |       \\_    _/   |______||  . `  |   /  /_\\  \\       \\   \\    |   ___/  |   __|  \n" +
                        "| `----.    |  |             |  |\\   |  /  _____  \\  .----)   |   |  |      |  |____  \n" +
                        " \\______|    |__|             |__| \\__| /__/     \\__\\ |_______/    | _|      |_______| \n" +
                        " --------------------------------------------------------------------------------------\n" +
                        "\n"+ANSI_RESET);
        System.out.println("Bienvenue dans l'application de labyrinthe !");
        System.out.print("Souhaitez-vous utiliser l'interface graphique ? (o/n) : ");
        String input = sc.nextLine().trim().toLowerCase();

        if (input.equals("o")) {
            HelloApplication.launch(HelloApplication.class);
        } else {
            startConsoleMode();
        }
    }

    public static void generateTerminal() {
        int width = 30;
        int height = 30;

        try {
            System.out.print("Largeur du labyrinthe : ");
            width = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Hauteur du labyrinthe : ");
            height = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED+"Valeur invalide."+ ANSI_GREEN+"Utilisation des dimensions par défaut :" + height +"x" +width+"."+ANSI_RESET);
        }

        System.out.print("Méthode de génération (Parfait/Imparfait) : ");
        String method = sc.nextLine().trim();
        if (!method.equalsIgnoreCase("Parfait") && !method.equalsIgnoreCase("Imparfait")) {
            System.out.println(ANSI_RED+"Méthode inconnue."+ ANSI_GREEN+" Méthode 'Parfait' utilisée par défaut."+ANSI_RESET);
            method = "Parfait";
        }

        System.out.print("Seed (laisser vide pour aléatoire) : ");
        String seedInput = sc.nextLine().trim();
        Integer seed = null;
        try {
            if (!seedInput.isEmpty()) {
                seed = Integer.parseInt(seedInput);
            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED+ "Seed invalide. Utilisation d'une seed aléatoire."+ANSI_RESET);
        }

        currentMaze = HelloController.generateMaze(width, height, method, seed);
        HelloController.printTerminal(currentMaze);
    }

    public static void startConsoleMode() {
        Database db = new Database();

        while (true) {
            if (currentMaze == null) {
                System.out.println(ANSI_YELLOW+"\n=== Menu Principal ==="+ANSI_RESET);
                System.out.println("1. Générer un labyrinthe");
                System.out.println("2. Charger un labyrinthe");
                System.out.println("3. Quitter");
                System.out.print("Choix : ");
                String choix = sc.nextLine().trim();

                switch (choix) {
                    case "1":
                        generateTerminal();
                        break;
                    case "2":
                        List<String> mazeList = db.getMazeList();
                        if (mazeList.isEmpty()) {
                            System.out.println(ANSI_RED+"Aucune sauvegarde trouvée.");
                            break;
                        }

                        System.out.println(ANSI_YELLOW+"=== Sauvegardes disponibles ==="+ANSI_RESET);
                        for (int i = 0; i < mazeList.size(); i++) {
                            System.out.println((i + 1) + ". " + mazeList.get(i));
                        }

                        System.out.print("Entrez le numéro du labyrinthe à charger : ");
                        String indexInput = sc.nextLine().trim();
                        int index = Integer.parseInt(indexInput) - 1;

                        if (index >= 0 && index < mazeList.size()) {
                            String selectedMazeName = mazeList.get(index);
                            currentMaze = db.DataChargeMaze(selectedMazeName);
                            if (currentMaze != null) {
                                System.out.println("Labyrinthe '" + selectedMazeName + "' chargé avec succès !");
                                HelloController.printTerminal(currentMaze);
                            } else {
                                System.out.println(ANSI_RED+"Erreur lors du chargement du labyrinthe.");
                            }
                        } else {
                            System.out.println(ANSI_RED+"Index invalide.");
                        }
                        break;

                    case "3":
                        System.out.println("Au revoir !");
                        return;
                    default:
                        System.out.println(ANSI_RED+"Choix invalide. Veuillez saisir (1, 2 ou 3).");
                }
            } else {
                System.out.println(ANSI_YELLOW+"\n=== Menu Labyrinthe ==="+ANSI_RESET);
                System.out.println("1. Résoudre le labyrinthe");
                System.out.println("2. Sauvegarder le labyrinthe");
                System.out.println("3. Générer un labyrinthe");
                System.out.println("4. Revenir au menu principal");
                System.out.print("Choix : ");
                String choix = sc.nextLine().trim();

                switch (choix) {
                    case "1":
                        System.out.println("Choisissez un algorithme de résolution :");
                        System.out.println("1. Trémeaux");
                        System.out.println("2. HandtoWall");
                        System.out.println("3. BFS");
                        System.out.print("Choix de l'algorithme : ");
                        String algoChoice = sc.nextLine().trim();
                        Resolve solver = new Resolve(currentMaze);
                        List<Case> path = new ArrayList<>();

                        switch (algoChoice) {
                            case "1":
                                path = solver.Tremaux();
                                HelloController.printTerminal(currentMaze, path);
                                break;
                            case "2":
                                path = solver.HandOnWall();
                                HelloController.printTerminal(currentMaze, path);
                                break;
                            case "3":
                                path=solver.BFS();
                                HelloController.printTerminal(currentMaze,path);
                                break;
                            default:
                                System.out.println(ANSI_RED+"Choix invalide. Veuillez saisir (1, 2 ou 3).");
                        }
                        break;
                    case "2":
                        HelloController.saveMazeTerminal(currentMaze, db, sc);
                        currentMaze = null;
                        break;
                    case "3":
                        generateTerminal();
                        break;
                    case "4":
                        currentMaze = null;
                        break;
                    default:
                        System.out.println(ANSI_RED+"Choix invalide. Veuillez saisir (1, 2, 3 ou 4).");
                }
            }
        }
    }
}


