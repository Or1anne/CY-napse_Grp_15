package com.example.nouveau;
import java.util.*;

/**
 * La classe {@code Resolve} est responsable de la résolution d'un labyrinthe.
 * <p>
 * Elle contient les informations nécessaires pour parcourir un labyrinthe de type {@link Maze},
 * notamment les dimensions, le point de départ, le point d'arrivée, et les métriques de performance.
 */
public class Resolve {
    private Case[][] Labyrinthe;
    private int width, height;
    private int nbCase =0;
    private long duration =0;
    private Case start;
    private Case end;

    /**
     * Constructeur de la classe {@code Resolve}.
     * <p>
     * Ce constructeur initialise un objet de résolution de labyrinthe en utilisant un labyrinthe donné, sans donner de point d'entrée ni de sortie.
     * Il appelle le constructeur principal avec des valeurs {@code null} pour l'entrée et la sortie.
     * @param Labyrinthe le labyrinthe à résoudre
     */
    public Resolve(Maze Labyrinthe) {
        this(Labyrinthe, null, null); // Appelle le constructeur principal avec entry et exit null
    }

    /**
     * Constructeur principal de la classe {@code Resolve}.
     * <p>
     * Initialise la grille du labyrinthe à partir de l'objet {@code Maze} donné,
     * ainsi que les dimensions et les points d'entrée et de sortie.
     * Si les cases d'entrée ou de sortie sont situées sur les bords du labyrinthe,
     * ce constructeur force l'ouverture des murs correspondants pour permettre
     * l'accès ou la sortie.
     * @param Labyrinthe le labyrinthe à résoudre, sous forme d'objet {@link Maze}
     * @param entry case représentant l'entrée du labyrinthe (peut être {@code null})
     * @param exit case représentant la sortie du labyrinthe (peut être {@code null})
     */
    public Resolve(Maze Labyrinthe, Case entry, Case exit) {
        this.Labyrinthe = Labyrinthe.getMaze();
        this.height = this.Labyrinthe.length;
        this.width = this.Labyrinthe[0].length;
        this.start = entry;
        this.end = exit;

        // Forcing walls open for entry/exit
        if (start != null) {
            if (start.getX() == 0) start.setNorth(false);
            else if (start.getX() == height - 1) start.setSouth(false);
            else if (start.getY() == 0) start.setWest(false);
            else if (start.getY() == width - 1) start.setEast(false);
        }

        if (end != null) {
            if (end.getX() == 0) end.setNorth(false);
            else if (end.getX() == height - 1) end.setSouth(false);
            else if (end.getY() == 0) end.setWest(false);
            else if (end.getY() == width - 1) end.setEast(false);
        }
    }

    /**
     * Réinitialise toutes les cases du labyrinthe.
     * <p>
     * Cette méthode remet à zéro les compteurs de chaque case et marque toutes les
     * cases comme non visitées en mettant leur attribut {@code visited} à {@code false}.
     */
    private void resetCounts() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Labyrinthe[i][j].resetCount(0);
                Labyrinthe[i][j].setVisited(false);
            }
        }
    }

    /**
     * Résout un labyrinthe en utilisant l'algorithme de Trémaux.
     * <p>
     * Cette méthode applique une version modifiée de l'algorithme de Trémaux pour
     * explorer le labyrinthe. Elle réinitialise les cases, explore récursivement
     * depuis la case de départ, puis filtre les chemins explorés deux fois, ce qui
     * correspond aux impasses ou aux détours inutiles.
     * <p>
     * La durée d'exécution est mesurée en nanosecondes et stockée via {@code setDuration}.
     *
     * @return Une liste de {@link Case} représentant le chemin solution entre l'entrée et la sortie du labyrinthe,
     *         ou {@code null} si aucun chemin n’a été trouvé.
     */
    public List<Case> Tremaux(){
        long startTIme = System.nanoTime();
        resetCounts();
        List<Case> path = new ArrayList<>();
        boolean found = explore(start.getX(), start.getY(), path);
        if (!found) return null;
        path.removeIf(c ->(c.getCount()==2));
        long endTime = System.nanoTime();
        setDuration(endTime-startTIme);
        return path;
    }

    /**
     * Méthode récursive utilisée dans l'algorithme de Trémaux pour explorer le labyrinthe.
     * <p>
     * Cette méthode visite récursivement les cases accessibles à partir de la position donnée.
     * Elle marque les cases visitées, compte les passages, et tente de construire un chemin vers la sortie.
     * Les directions possibles sont mélangées pour éviter un parcours systématique et simuler un comportement aléatoire.
     * <p>
     * Une case est explorée si elle a été déjà visitée mais n'a pas encore été explorée deux fois.
     * @param x La coordonnée verticale (ligne) de la case actuelle.
     * @param y La coordonnée horizontale (colonne) de la case actuelle.
     * @param path La liste des cases empruntées constituant le chemin actuel.
     * @return {@code true} si la sortie a été atteinte à partir de cette position, sinon {@code false}.
     */
    private boolean explore(int x, int y, List<Case> path) {
        Case current = Labyrinthe[x][y];
        current.incrementCount();
        path.add(current);
        addNbCase();


        if (Labyrinthe[x][y] == end){
            System.out.println("Nb Case visité:" + nbCase);
            return true;
        }

        List<int[]> directions = new ArrayList<>();

        // Ajouter les directions possibles
        if (!current.getNorth() && x > 0) directions.add(new int[]{x - 1, y});
        if (!current.getSouth() && x < height - 1) directions.add(new int[]{x + 1, y});
        if (!current.getWest() && y > 0) directions.add(new int[]{x, y - 1});
        if (!current.getEast() && y < width - 1) directions.add(new int[]{x, y + 1});

        Collections.shuffle(directions);

        for (int[] dir : directions) {
            Case next = Labyrinthe[dir[0]][dir[1]];

            if (next.getVisited() && next.getCount() < 2) {
                current.setVisited(true);
                if (explore(dir[0], dir[1], path)) {
                    return true;
                }
            }
        }
        current.incrementCount();
        return false;
    }


    /**
     * Algorithme de résolution de labyrinthe : "Hand on Wall"
     * <p>
     * Cette méthode consiste à suivre en permanence le mur situé à gauche jusqu'à atteindre la sortie.
     * @return un tableau de cases représentant le chemin de résolution du labyrinthe
     */
    public List<Case> HandOnWall() {
        resetCounts();
        long startTime = System.nanoTime();
        int x = start.getX();
        int y = start.getY();
        Case current = Labyrinthe[x][y];

        // Initiale la direction a gauche car l'entrée est toujours sur le bord gauche du labyrinthe
        Direction dir = Direction.EAST;
        List<Case> path = new ArrayList<>();
        current.setVisited(true);
        path.add(current);
        int steps = 0;
        int maxSteps = width * height * 4;


        // Condition pour sortir du labyrinthe
        while (Labyrinthe[x][y] != end) {
            if (steps++ > maxSteps) {
                System.out.println("Labyrinthe insoluble (boucle infinie détectée)");
                return null;
            }

            addNbCase();
            Direction left = dir.turnLeft();

            // Tester si on peut aller à gauche
            if (canMove(x, y, left)) {
                dir = left;
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            }
            // Sinon tester en face
            else if (canMove(x, y, dir)) {
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            }
            // Sinon tester à droite
            else if (canMove(x, y, dir.turnRight())) {
                dir = dir.turnRight();
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            }
            // Sinon faire demi-tour
            else {
                dir = dir.opposite();
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            }

            // Si la case n'est pas visitée, la visitée et l'ajouter au chemin parcouru
            if (Labyrinthe[x][y].getVisited()) {
                Labyrinthe[x][y].setVisited(true);
                path.add(Labyrinthe[x][y]);
            }
        }
        long endTime = System.nanoTime();
        setDuration(endTime - startTime);

        return path;
    }


    /**
     * Vérifie si un déplacement est possible depuis une case donnée dans une direction spécifique.
     *
     * @param x La coordonnée verticale (ligne) de la case actuelle dans le labyrinthe.
     * @param y La coordonnée horizontale (colonne) de la case actuelle dans le labyrinthe.
     * @param dir La direction vers laquelle on souhaite se déplacer (NORTH, EAST, SOUTH ou WEST).
     * @return true si le déplacement est possible, s'il n'y a pas de mur et que la case cible est dans les limites du labyrinthe, false sinon.
     */
    public boolean canMove(int x, int y, Direction dir){
        Case c = Labyrinthe[x][y];
        return switch (dir) {
            case NORTH -> !c.getNorth() && x > 0;
            case EAST  -> !c.getEast() && y < width - 1;
            case SOUTH -> !c.getSouth() && x < height - 1;
            case WEST  -> !c.getWest() && y > 0;
        };
    }


    /**
     * Calcule la nouvelle position après un déplacement depuis une position donnée dans une direction spécifiée.
     *
     * @param x La coordonnée verticale (ligne) actuelle.
     * @param y La coordonnée horizontale (colonne) actuelle.
     * @param dir La direction dans laquelle effectuer le déplacement (NORTH, EAST, SOUTH, WEST).
     * @return Un tableau d'entiers de taille 2 contenant la nouvelle position après déplacement : [nouveauX, nouveauY].
     */
    public int[] move(int x, int y, Direction dir){
        return switch (dir) {
            case NORTH -> new int[] {x - 1, y};
            case EAST  -> new int[] {x, y + 1};
            case SOUTH -> new int[] {x + 1, y};
            case WEST  -> new int[] {x, y - 1};
        };
    }


    /**
     * Résout le labyrinthe en utilisant l'algorithme de parcours en largueur (BFS - Breadth-First Search).
     * <p>
     * Cet algorithme garantit de trouver le plus court chemin (en nombre de cases) entre l'entrée {@code start}
     * et la sortie {@code end} dans le labyrinthe non pondéré.
     * <p>
     * Le parcours est réalisé en explorant les voisins accessibles de chaque case de manière itérative,
     * en enregistrant le parent de chaque case visitée pour permettre la reconstruction du chemin une fois l’arrivée atteinte.
     * @return Une liste ordonnée de {@code Case} représentant le chemin solution de l’entrée à la sortie.
     *         Si aucun chemin n'existe, retourne une liste vide.
     */
    public List<Case> BFS() {
        resetCounts();
        Queue<Case> queue = new LinkedList<>();
        Map<Case, Case> parentMap = new HashMap<>();
        Set<Case> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        parentMap.put(start, null);

        while (!queue.isEmpty()) {
            Case current = queue.poll();

            // Si le serpent arrive à la fin
            if (current == end) {
                return reconstructPath(parentMap, end);
            }

            // On explore tous les voisins accessibles
            for (Case neighbor : getAccessibleNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return Collections.emptyList(); // Aucun chemin trouvé
    }

    // On reconstruis le chemin à partir des parents
    /**
     * Reconstitue le chemin depuis l'entrée jusqu'à la sortie en utilisant la carte des parents.
     * <p>
     * Cette méthode retrace le chemin en partant de la case de fin {@code end} jusqu'à l'entrée
     * en suivant les références contenues dans {@code parentMap}, puis inverse la liste
     * pour obtenir l'ordre correct (de l'entrée vers la sortie).
     *
     * @param parentMap Une map contenant, pour chaque case visitée, la case dont elle provient.
     * @param end La case de destination (fin du labyrinthe).
     * @return Une liste ordonnée de {@code Case} représentant le chemin de la solution.
     */
    private List<Case> reconstructPath(Map<Case, Case> parentMap, Case end) {
        List<Case> path = new ArrayList<>();
        Case current = end;

        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    // Voisins accessibles d'une case
    /**
     * Retourne la liste des voisins accessibles depuis une case donnée.
     * <p>
     * Cette méthode examine les murs de la case actuelle pour déterminer
     * les directions dans lesquelles un déplacement est possible (c'est-à-dire
     * où il n'y a pas de mur). Elle retourne toutes les cases voisines
     * qui peuvent être atteintes directement depuis la case courante.
     *
     * @param current La case courante dont on souhaite connaître les voisins accessibles.
     * @return Une liste de {@code Case} représentant les cases voisines accessibles.
     */

    private List<Case> getAccessibleNeighbors(Case current) {
        List<Case> neighbors = new ArrayList<>();
        int x = current.getX();
        int y = current.getY();

        // Voisin du haut
        if (!current.getNorth() && x > 0) {
            neighbors.add(Labyrinthe[x-1][y]);
        }
        // Voisin du bas
        if (!current.getSouth() && x < height - 1) {
            neighbors.add(Labyrinthe[x+1][y]);
        }
        // Voisin de gauche
        if (!current.getWest() && y > 0) {
            neighbors.add(Labyrinthe[x][y-1]);
        }
        // Voisin de droite
        if (!current.getEast() && y < width - 1) {
            neighbors.add(Labyrinthe[x][y+1]);
        }

        return neighbors;
    }


    /**
     * Retourne le nombre de cases visitées lors de la résolution du labyrinthe.
     *
     * @return Le nombre de cases visitées.
     */
    public int getNbCase() { return nbCase; }


    /**
     * Retourne la durée de la résolution du labyrinthe en nanosecondes.
     *
     * @return La durée en nanosecondes.
     */
    public long getDuration() { return duration; }


    /**
     * Incrémente de 1 le compteur de cases visitées.
     */
    public void addNbCase(){ this.nbCase++; }


    /**
     * Définit le nombre de cases visitées.
     *
     * @param nbCase Le nouveau nombre de cases visitées.
     */
    public void setNbCase(int nbCase) { this.nbCase = nbCase; }


    /**
     * Définit la durée de la résolution du labyrinthe.
     *
     * @param duration La durée en nanosecondes.
     */
    public void setDuration(long duration) { this.duration = duration; }

}