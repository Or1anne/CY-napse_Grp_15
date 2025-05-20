package com.example.nouveau;

import javax.swing.text.Style;
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
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_LIGHT_GRAY = "\u001B[38;5;244m";

    public static void main(String[] args) throws InterruptedException {
        String [] lines={

                "  ______ ____    ____         .__   __.      ___           _______..______    _______",
                        " /      |\\   \\  /   /         |  \\ |  |     /   \\         /       ||   _  \\  |   ____|",
                        "|  ,----' \\   \\/   /   ______ |   \\|  |    /  ^  \\       |   (----`|  |_)  | |  |__   ",
                        "|  |       \\_    _/   |______||  . `  |   /  /_\\  \\       \\   \\    |   ___/  |   __|  ",
                        "| `----.    |  |             |  |\\   |  /  _____  \\  .----)   |   |  |      |  |____  ",
                        " \\______|    |__|             |__| \\__| /__/     \\__\\ |_______/    | _|      |_______|",
                        " --------------------------------------------------------------------------------------",
        };
        for (String line: lines){
            System.out.println(ANSI_YELLOW+line+ANSI_RESET);
        }
        Thread.sleep(600);
        String handle = ANSI_LIGHT_GRAY + "||==||";
        // Lame (verte ici)
        String blade = ANSI_PURPLE + "==============================";

        // Animation : on affiche la lame caractère par caractère
        System.out.print("\n"+ handle);
        for (char c : blade.toCharArray()) {
            System.out.print(c);
            Thread.sleep(20); // effet "allumage"
        }
        System.out.println(ANSI_RESET); // retour à la ligne + reset couleur

        Thread.sleep(600);
        System.out.println("Bienvenue dans l'application de labyrinthe !");
        Thread.sleep(500);
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

        System.out.print("Largeur du labyrinthe (ou 0 pour annuler) : ");
        String inputWidth = sc.nextLine().trim();
        if (inputWidth.equals("0")) return;

        try {
            width = Integer.parseInt(inputWidth);
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "Valeur invalide. " + ANSI_GREEN + "Largeur par défaut : " + width + "." + ANSI_RESET);
        }

        System.out.print("Hauteur du labyrinthe (ou 0 pour annuler) : ");
        String inputHeight = sc.nextLine().trim();
        if (inputHeight.equals("0")) return;

        try {
            height = Integer.parseInt(inputHeight);
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "Valeur invalide. " + ANSI_GREEN + "Hauteur par défaut : " + height + "." + ANSI_RESET);
        }

        System.out.print("Méthode de génération (Parfait/Imparfait ou r pour retour) : ");
        String method = sc.nextLine().trim();
        if (method.equalsIgnoreCase("r")) return;

        if (!method.equalsIgnoreCase("Parfait") && !method.equalsIgnoreCase("Imparfait")) {
            System.out.println(ANSI_RED + "Méthode inconnue." + ANSI_GREEN + " Méthode 'Parfait' utilisée par défaut." + ANSI_RESET);
            method = "Parfait";
        }

        System.out.print("Seed (laisser vide pour aléatoire, ou tapez r pour retour) : ");
        String seedInput = sc.nextLine().trim();
        if (seedInput.equalsIgnoreCase("r")) return;

        Integer seed = null;
        try {
            if (!seedInput.isEmpty()) {
                seed = Integer.parseInt(seedInput);
            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "Seed invalide. Utilisation d'une seed aléatoire." + ANSI_RESET);
        }

        currentMaze = HelloController.generateMaze(width, height, method, seed);
        HelloController.printTerminal(currentMaze);
    }



    public static void startConsoleMode() throws InterruptedException {
        Database db = new Database();
        String handle = ANSI_LIGHT_GRAY + "||==||";

        while (true) {
            if (currentMaze == null) {
                String [] Menu = {
                    "\n"+handle + ANSI_YELLOW+"=== Menu Principal ==="+ANSI_RESET,
                            ANSI_BLUE+"    1"+ANSI_RESET+". Générer un labyrinthe",
                            ANSI_GREEN+"    2"+ANSI_RESET+". Charger un labyrinthe",
                            ANSI_RED+"    3"+ANSI_RESET+". Quitter",
                };
                for(String menu : Menu){
                    Thread.sleep(200);
                    System.out.println(menu);
                }
                System.out.print(handle + ANSI_PURPLE + "===Choix (ou 0 pour retour) : " + ANSI_RESET);
                String choose = sc.nextLine().trim();


                switch (choose) {
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
                    case "0","4":
                        break;
                    default:
                        System.out.println(ANSI_RED+"Choix invalide. Veuillez saisir (1, 2 ou 3).");
                }
            } else {
                String [] Menu = {
                        "\n"+handle + ANSI_YELLOW+"=== Menu Labyrinthe ==="+ANSI_RESET,
                        ANSI_BLUE+"    1"+ANSI_RESET+". Résoudre le labyrinthe",
                        ANSI_GREEN+"    2"+ANSI_RESET+". Sauvegarder le labyrinthe",
                        ANSI_RED+"    3"+ANSI_RESET+". Générer un labyrinthe",
                        ANSI_PURPLE+"    4"+ANSI_RESET+". Revenir au menu principal"

                };
                for(String menu : Menu){
                    Thread.sleep(200);
                    System.out.println(menu);
                }
                System.out.print(handle+ANSI_PURPLE+"===Choix : "+ANSI_RESET);
                String choose = sc.nextLine().trim();

                switch (choose) {
                    case "1":
                        String [] MenuAlgo = {
                                "\n"+handle + ANSI_YELLOW+"=== Choisissez un algorithme de résolution ==="+ANSI_RESET,
                                ANSI_BLUE+"    1"+ANSI_RESET+".  Trémeaux",
                                ANSI_GREEN+"    2"+ANSI_RESET+". HandtoWall",
                                ANSI_RED+"    3"+ANSI_RESET+". BFS",
                                ANSI_PURPLE+"    4"+ANSI_RESET+". Retour"

                        };
                        for(String menu : MenuAlgo){
                            Thread.sleep(200);
                            System.out.println(menu);
                        }
                        System.out.print(handle+ANSI_PURPLE+"===Choix : "+ANSI_RESET);
                        String Algochoose = sc.nextLine().trim();
                        Resolve solver = new Resolve(currentMaze);
                        List<Case> path = new ArrayList<>();

                        switch (Algochoose) {
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
                            case "0","4":
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


