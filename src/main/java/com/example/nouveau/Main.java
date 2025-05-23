package com.example.nouveau; //Define the package in which the class is located

import javax.swing.text.Style;
import java.util.*;

public class Main {
    //Global variables
    private static Maze currentMaze; // Current maze being worked on
    private static final Scanner sc = new Scanner(System.in); // Scanner for user input
    // Define ANSI color codes for terminal output
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_LIGHT_GRAY = "\u001B[38;5;244m";
    private static final String handle = ANSI_LIGHT_GRAY + "||==||";
    private static final  LinkedList<String> color = new LinkedList<>(Arrays.asList(ANSI_RED, ANSI_BLUE, ANSI_PURPLE, ANSI_GREEN));
    private static final String blade = "==============================";

    public static void main(String[] args) throws InterruptedException {
        //SHow the title of the application
        Collections.shuffle(color, new Random());
        String [] lines={

                "  ______ ____    ____         .__   __.      ___           _______..______    _______",
                " /      |\\   \\  /   /         |  \\ |  |     /   \\         /       ||   _  \\  |   ____|",
                "|  ,----' \\   \\/   /   ______ |   \\|  |    /  ^  \\       |   (----`|  |_)  | |  |__   ",
                "|  |       \\_    _/   |______||  . `  |   /  /_\\  \\       \\   \\    |   ___/  |   __|  ",
                "|  `----.    |  |             |  |\\   |  /  _____  \\  .----)   |   |  |      |  |____  ",
                " \\______|    |__|             |__| \\__| /__/     \\__\\ |_______/    | _|      |_______|",
                " --------------------------------------------------------------------------------------",
        };
        for (String line: lines){
            System.out.println(ANSI_YELLOW+line+ANSI_RESET);
        }
        Thread.sleep(600);

        System.out.print("\n"+ handle + color.getFirst());
        for (char c : blade.toCharArray()) {
            System.out.print(c);
            Thread.sleep(20);
        }
        System.out.println(ANSI_RESET);

        Thread.sleep(600);
        System.out.println("Bienvenue dans l'application de labyrinthe !");
        Thread.sleep(500);
        // Ask the user if they want to use the graphical interface or the terminal version
        System.out.print("Souhaitez-vous utiliser l'interface graphique ? (o/n) : ");
        String input = sc.nextLine().trim().toLowerCase();

        //Start JavaFX application if user chooses "o"
        if (input.equals("o")) {
            HelloApplication.launch(HelloApplication.class);
        } else {
            startConsoleMode();
        }
    }

    // Show the statistics of the maze resolution like the number of visited cells and the duration of the resolution
    public static void Show_Stat(Resolve solver){
        double durationsMs= solver.getDuration()/1_000_000.0;
        System.out.println("Nombre de cases visitées: " +ANSI_YELLOW+ solver.getNbCase()+ANSI_RESET);
        System.out.println(("Durée " +ANSI_YELLOW+durationsMs+" ms"+ANSI_RESET));
    }

    // Generate a new maze in the terminal by asking the user to input
    // the width, height, the type of maze (Perfect or not) and seed (optionnal, will be random if not given)
    public static void generateTerminal() throws InterruptedException {
        int width = 30;
        int height = 30;
        Collections.shuffle(color, new Random());

        System.out.print(" \u2194 Largeur du labyrinthe (ou 0 pour annuler) : ");
        String inputWidth = sc.nextLine().trim();
        if (inputWidth.equals("0")) return;

        try {
            width = Integer.parseInt(inputWidth);
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "❌ Valeur invalide. " + ANSI_GREEN + "Largeur par défaut : " + width + "." + ANSI_RESET);
        }

        System.out.print("\u2195 Hauteur du labyrinthe (ou 0 pour annuler) : ");
        String inputHeight = sc.nextLine().trim();
        if (inputHeight.equals("0")) return;

        try {
            height = Integer.parseInt(inputHeight);
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "❌ Valeur invalide. " + ANSI_GREEN + "Hauteur par défaut : " + height + "." + ANSI_RESET);
        }

        System.out.print("Méthode de génération (Parfait/Imparfait ou r pour retour) : ");
        String method = sc.nextLine().trim();
        if (method.equalsIgnoreCase("r")) return;

        if (!method.equalsIgnoreCase("Parfait") && !method.equalsIgnoreCase("Imparfait")) {
            System.out.println(ANSI_RED + "❌ Méthode inconnue." + ANSI_GREEN + " Méthode 'Parfait' utilisée par défaut." + ANSI_RESET);
            method = "Parfait";
        }

        System.out.print("\uD83C\uDF31 Seed (laisser vide pour aléatoire, ou tapez r pour retour) : ");
        String seedInput = sc.nextLine().trim();
        if (seedInput.equalsIgnoreCase("r")) return;

        Integer seed = null;
        try {
            if (!seedInput.isEmpty()) {
                seed = Integer.parseInt(seedInput);
            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "❌ Seed invalide."+ANSI_GREEN+" Utilisation d'une seed aléatoire." + ANSI_RESET);
        }

        System.out.print(handle+color.getFirst()+"Chargement");
        for (char c : blade.toCharArray()) {
            System.out.print(c);
            Thread.sleep(100);
        }
        System.out.println(ANSI_RESET);
        currentMaze = HelloController.generateMaze(width, height, method, seed); // Generate the maze
        HelloController.printTerminal(currentMaze); // Print the maze in the terminal
    }

    // Display the menu in the terminal (with Thread.sleep) and return the user's choice
    public static String MenuTerminal(String [] Menu) throws InterruptedException{
        for(String menu : Menu){
            Thread.sleep(200);
            System.out.println(menu);
        }
        System.out.print(handle + ANSI_PURPLE + "===Choix : " + ANSI_RESET);
        String choose = sc.nextLine().trim();
        return choose;

    }

    // Load a maze from the database and display it in the terminal
    // by listing all the mazes in the database and asking the user to choose one
    public static void MenuSave() throws InterruptedException{
        Database db = new Database(); // Create a new database instance
        List<String> mazeList = db.getMazeList(); // Get the list of saved mazes
        if (mazeList.isEmpty()) { // Check if the list is empty
            System.out.println(ANSI_RED+"❌ Aucune sauvegarde trouvée.");
            return;
        }

        System.out.println(ANSI_YELLOW+"=== Sauvegardes disponibles ==="+ANSI_RESET);
        // Display the list of mazes
        for (int i = 0; i < mazeList.size(); i++) { 
            System.out.println((i + 1) + ". " + mazeList.get(i)); 
        }

        System.out.print("Entrez le numéro du labyrinthe à charger : ");
        String indexInput = sc.nextLine().trim();
        int index = Integer.parseInt(indexInput) - 1;

        if (index >= 0 && index < mazeList.size()) {
            String selectedMazeName = mazeList.get(index);
            currentMaze = db.DataChargeMaze(selectedMazeName); // Load the maze from the database
            if (currentMaze != null) {
                System.out.println("Labyrinthe '" + selectedMazeName + "' chargé avec succès !");
                HelloController.printTerminal(currentMaze);
            } else {
                System.out.println(ANSI_RED+"❌ Erreur lors du chargement du labyrinthe.");
            }
        } else {
            System.out.println(ANSI_RED+"❌ Index invalide.");
        }
    }

    // Allow the user to edit a wall of a cell in the maze
    // by asking the user to input the coordinates of the cell and the direction of the wall (North, South, East, West)
    // and whether to add or remove the wall.
    // The method also updates the opposite wall of the neighboring cell.
    public static void editCase(Maze maze) {
        System.out.println("Modifier une case du labyrinthe");
        System.out.print("Entrez la coordonnée X (0 à " + (maze.getWidth() - 1) + ", ou r pour retour) : ");
        String inputX = sc.nextLine().trim();
        if (inputX.equalsIgnoreCase("r")) return;

        System.out.print("Entrez la coordonnée Y (0 à " + (maze.getHeight() - 1) + ", ou r pour retour) : ");
        String inputY = sc.nextLine().trim();
        if (inputY.equalsIgnoreCase("r")) return;

        int x, y;
        try {
            x = Integer.parseInt(inputX);
            y = Integer.parseInt(inputY);
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "❌ Coordonnées invalides." + ANSI_RESET);
            return;
        }

        if (x < 0 || x >= maze.getWidth() || y < 0 || y >= maze.getHeight()) {
            System.out.println(ANSI_RED + "❌ Coordonnées hors limites." + ANSI_RESET);
            return;
        }

        Case c = maze.getCase(x, y);

        System.out.println("Mur à modifier :");
        System.out.println("1. Nord");
        System.out.println("2. Sud");
        System.out.println("3. Est");
        System.out.println("4. Ouest");
        System.out.print("Choix (1-4, ou r pour retour) : ");
        String inputWall = sc.nextLine().trim();
        if (inputWall.equalsIgnoreCase("r")) return;

        Direction direction;
        switch (inputWall) {
            case "1": direction = Direction.NORTH; break;
            case "2": direction = Direction.SOUTH; break;
            case "3": direction = Direction.EAST; break;
            case "4": direction = Direction.WEST; break;
            default:
                System.out.println(ANSI_RED + "❌ Choix invalide." + ANSI_RESET);
                return;
        }

        System.out.print("Souhaitez-vous (1) ajouter ou (2) enlever ce mur ? ");
        String action = sc.nextLine().trim();

        //Modify the wall of the current cell
        if (action.equals("1")) {
            c.addWall(direction);
        } else if (action.equals("2")) {
            c.removeWall(direction);
        } else {
            System.out.println(ANSI_RED + "❌ Action invalide." + ANSI_RESET);
            return;
        }

        //Modify the opposite wall of the neighboring cell
        int nx = x, ny = y;  // coordinates of the neighboring cell
        Direction opposite = null;
        switch (direction) {
            case NORTH:
                ny = y - 1;
                opposite = Direction.SOUTH;
                break;
            case SOUTH:
                ny = y + 1;
                opposite = Direction.NORTH;
                break;
            case EAST:
                nx = x + 1;
                opposite = Direction.WEST;
                break;
            case WEST:
                nx = x - 1;
                opposite = Direction.EAST;
                break;
        }

        // Verify that the neighboring cell exists
        // and modify its wall accordingly
        Case neighbor = maze.getCase(nx, ny);
        if (neighbor != null) {
            if (action.equals("1")) {
                neighbor.addWall(opposite);
                System.out.println("Mur ajouté dans la case voisine. ✔\uFE0F");
            } else if (action.equals("2")) {
                neighbor.removeWall(opposite);
                System.out.println("Mur enlevé dans la case voisine. ✔\uFE0F");
            }
        }

        if (action.equals("1")) {
            System.out.println("Mur ajouté. ✔\uFE0F");
        } else {
            System.out.println("Mur enlevé. ✔\uFE0F");
        }
        System.out.println(c.getX()+","+c.getY());
        System.out.println(c.getNorth() +","+ c.getSouth()+"," + c.getWest() +","+ c.getEast());
    }



    // Start the console mode of the application
    // by displaying the main menu and allowing the user to choose between
    // generating a new maze, loading a saved maze, or quitting the application.
    // If a maze is already loaded, the user can choose to solve it, save it, generate a new maze, edit it, or return to the main menu.
    // The method also handles the maze resolution algorithms (Tremaux, HandOnWall, BFS)
    // and displays the statistics of the resolution (number of visited cells and duration).
    public static void startConsoleMode() throws InterruptedException {
        Database db = new Database();
        String choose;
        while (true) {
            if (currentMaze == null) { // If no maze is loaded
                String [] Menu = {
                        "\n"+handle + ANSI_YELLOW+"=== Menu Principal ==="+ANSI_RESET,
                        ANSI_BLUE+"    1"+ANSI_RESET+". \uD83E\uDDE9  Générer un labyrinthe",
                        ANSI_GREEN+"    2"+ANSI_RESET+". \uD83D\uDCC1 Charger un labyrinthe",
                        ANSI_RED+"    3"+ANSI_RESET+". \uD83D\uDEAA Quitter",
                };

                choose = MenuTerminal(Menu);

                switch (choose) {
                    case "1":
                        generateTerminal();
                        break;
                    case "2":
                        MenuSave();
                        break;

                    case "3":
                        System.out.println("Au revoir !");
                        return;
                    default:
                        System.out.println(ANSI_RED+"❌ Choix invalide. Veuillez saisir (1, 2 ou 3).");
                }
            } else { // If a maze is loaded
                String [] Menu = {
                        "\n"+handle + ANSI_YELLOW+"=== Menu Labyrinthe ==="+ANSI_RESET,
                        ANSI_BLUE+"    1"+ANSI_RESET+". \uD83E\uDDE9 Résoudre le labyrinthe",
                        ANSI_GREEN+"    2"+ANSI_RESET+". \uD83D\uDCC2 Sauvegarder le labyrinthe",
                        ANSI_RED+"    3"+ANSI_RESET+". \uD83C\uDD95 Générer un labyrinthe",
                        ANSI_YELLOW+"   4"+ANSI_RESET+".    Modifier Labyrinthe",
                        ANSI_PURPLE+"    5"+ANSI_RESET+". \u2B05 Revenir au menu principal"

                };

                choose = MenuTerminal(Menu);

                switch (choose) {
                    case "1":
                        String [] MenuAlgo = {
                                "\n"+handle + ANSI_YELLOW+"=== Choisissez un algorithme de résolution ==="+ANSI_RESET,
                                ANSI_BLUE+"    1"+ANSI_RESET+".  Trémeaux",
                                ANSI_GREEN+"    2"+ANSI_RESET+".   HandtoWall",
                                ANSI_RED+"    3"+ANSI_RESET+".  BFS",
                                ANSI_PURPLE+"    4"+ANSI_RESET+". \u2B05 Retour"

                        };
                        choose = MenuTerminal(MenuAlgo);
                        Resolve solver = new Resolve(currentMaze); // Create a new solver instance
                        List<Case> path = new ArrayList<>(); // List to store the path of the resolution

                        switch (choose) {
                            case "1":
                                path = solver.Tremaux();
                                HelloController.printTerminal(currentMaze, path);
                                Show_Stat(solver);
                                break;
                            case "2":
                                path = solver.HandOnWall();
                                HelloController.printTerminal(currentMaze, path);
                                Show_Stat(solver);
                                break;
                            case "3":
                                path=solver.BFS();
                                HelloController.printTerminal(currentMaze,path);
                                Show_Stat(solver);
                                break;
                            case "0","4":
                                break;
                            default:
                                System.out.println(ANSI_RED+"❌ Choix invalide. Veuillez saisir (1, 2 ou 3).");
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
                        editCase(currentMaze);
                        HelloController.printTerminal(currentMaze);
                        break;
                    case "5":
                        currentMaze = null;
                        break;
                    default:
                        System.out.println(ANSI_RED+"❌ Choix invalide. Veuillez saisir (1, 2, 3, 4, 5).");
                }
            }
        }
    }
}



