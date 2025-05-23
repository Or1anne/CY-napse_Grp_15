package com.example.nouveau;

import javax.swing.text.Style;
import java.util.*;



/**
 * Classe principale de l'application de labyrinthe.
 * <p>
 * Cette classe gère le lancement de l'application, soit en interface graphique, soit en mode console.
 * Elle offre des fonctionnalités telles que la génération, la résolution, la modification et la sauvegarde
 * de labyrinthes, ainsi que l'affichage de statistiques.
 * </p>
 */
public class Main {
    private static Maze currentMaze;
    private static final Scanner sc = new Scanner(System.in);
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



    /**
     * Point d'entrée principal de l'application.
     * <p>
     * Affiche une bannière de bienvenue, demande à l'utilisateur s'il souhaite
     * utiliser l'interface graphique, sinon lance le mode console.
     * </p>
     *
     * @param args arguments de la ligne de commande (non utilisés)
     * @throws InterruptedException si le thread est interrompu lors des pauses d'affichage
     */
    public static void main(String[] args) throws InterruptedException {
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
        System.out.print("Souhaitez-vous utiliser l'interface graphique ? (o/n) : ");
        String input = sc.nextLine().trim().toLowerCase();


        if (input.equals("o")) {
            HelloApplication.launch(HelloApplication.class);
        } else {
            startConsoleMode();
        }
    }


    /**
     * Affiche les statistiques de résolution d'un labyrinthe.
     *
     * @param solver instance de la classe Resolve contenant les résultats
     */
    public static void Show_Stat(Resolve solver){
        double durationsMs= solver.getDuration()/1_000_000.0;
        System.out.println("Nombre de cases visitées: " +ANSI_YELLOW+ solver.getNbCase()+ANSI_RESET);
        System.out.println(("Durée " +ANSI_YELLOW+durationsMs+" ms"+ANSI_RESET));
    }



    /**
     * Gère l'interface de génération d'un labyrinthe en mode terminal.
     * <p>
     * Demande les dimensions, la méthode de génération et la seed,
     * puis génère et affiche le labyrinthe correspondant.
     * </p>
     *
     * @throws InterruptedException si le thread est interrompu lors des pauses d'affichage
     */
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
        currentMaze = HelloController.generateMaze(width, height, method, seed);
        HelloController.printTerminal(currentMaze);
    }


    /**
     * Affiche un menu donné sous forme de tableau de chaînes, puis récupère le choix de l'utilisateur.
     *
     * @param Menu tableau de chaînes représentant les options du menu à afficher
     * @return la chaîne saisie par l'utilisateur correspondant à son choix
     * @throws InterruptedException si le thread est interrompu lors de l'affichage
     */
    public static String MenuTerminal(String [] Menu) throws InterruptedException{
        for(String menu : Menu){
            Thread.sleep(200);
            System.out.println(menu);
        }
        System.out.print(handle + ANSI_PURPLE + "===Choix : " + ANSI_RESET);
        String choose = sc.nextLine().trim();
        return choose;

    }


    /**
     * Affiche la liste des labyrinthes sauvegardés, permet à l'utilisateur d'en choisir un à charger,
     * puis charge et affiche ce labyrinthe.
     *
     * @throws InterruptedException si le thread est interrompu lors des pauses d'affichage
     */
    public static void MenuSave() throws InterruptedException{
        Database db = new Database();
        List<String> mazeList = db.getMazeList();
        if (mazeList.isEmpty()) {
            System.out.println(ANSI_RED+"❌ Aucune sauvegarde trouvée.");
            return;
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
                System.out.println(ANSI_RED+"❌ Erreur lors du chargement du labyrinthe.");
            }
        } else {
            System.out.println(ANSI_RED+"❌ Index invalide.");
        }
    }


    /**
     * Permet de modifier un mur d'une case du labyrinthe.
     * <p>
     * L'utilisateur saisit les coordonnées de la case, choisit quel mur modifier,
     * puis décide d'ajouter ou d'enlever ce mur. La modification est répercutée
     * sur la case voisine pour conserver la cohérence du labyrinthe.
     * </p>
     *
     * @param maze le labyrinthe à modifier
     */
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

        // Modifier le mur de la case courante
        if (action.equals("1")) {
            c.addWall(direction);
        } else if (action.equals("2")) {
            c.removeWall(direction);
        } else {
            System.out.println(ANSI_RED + "❌ Action invalide." + ANSI_RESET);
            return;
        }

        // Modifier le mur opposé de la case voisine
        int nx = x, ny = y;  // coordonnées de la case voisine
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

        // Vérifier que la case voisine existe
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



    /**
     * Lance le mode console de l'application.
     * <p>
     * Propose un menu interactif permettant de générer, charger, résoudre,
     * sauvegarder, modifier un labyrinthe ou quitter l'application.
     * </p>
     *
     * @throws InterruptedException si le thread est interrompu lors des pauses d'affichage
     */
    public static void startConsoleMode() throws InterruptedException {
        Database db = new Database();
        String choose;
        while (true) {
            if (currentMaze == null) {
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
            } else {
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
                        Resolve solver = new Resolve(currentMaze);
                        List<Case> path = new ArrayList<>();

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



