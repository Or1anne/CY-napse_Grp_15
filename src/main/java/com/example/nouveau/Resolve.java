package com.example.nouveau;
import java.util.*;

public class Resolve {
    private Case[][] Labyrinthe;
    private int width, height;
    private int nbCase =0;
    private long duration =0;
    private Case start;
    private Case end;
    private Map<Case, Case> parentMap;
    private List<Case> visitedCases;

    public Resolve(Maze Labyrinthe) {
        this(Labyrinthe, Labyrinthe.getMaze()[0][0], Labyrinthe.getMaze()[Labyrinthe.getHeight()-1][Labyrinthe.getWidth()-1] ); // Appelle le constructeur principal avec entry et exit null
    }


    public Resolve(Maze Labyrinthe, Case entry, Case exit) {
        this.Labyrinthe = Labyrinthe.getMaze();
        this.height = this.Labyrinthe.length;
        this.width = this.Labyrinthe[0].length;
        this.start = entry;
        this.end = exit;

        // Forcer l'ouverture des murs pour les entrées/sorties
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


    private void resetCounts() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Labyrinthe[i][j].resetCount(0);
                Labyrinthe[i][j].setVisited(false);
            }
        }
    }

    //Trémaux resolution
    public List<Case> Tremaux(){
        visitedCases = new ArrayList<>();
        long startTIme = System.nanoTime();
        resetCounts();
        List<Case> path = new ArrayList<>();
        boolean found = explore(start.getX(), start.getY(), path);
        if (!found) return null;
        long endTime = System.nanoTime();
        setDuration(endTime-startTIme);
        return path;
    }

    private boolean explore(int x, int y, List<Case> path) {
        Case current = Labyrinthe[x][y];
        current.incrementCount();
        path.add(current);
        addNbCase();

        visitedCases.add(current);

        if (current == end){
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
        path.removeLast();
        current.incrementCount();
        return false;
    }

    public List<Case> getVisitedCases() {
        return visitedCases;
    }


    //Main Gauche sur le mur
    public List<Case> HandOnWall() {
        resetCounts();
        long startTime = System.nanoTime();
        int x = start.getX();
        int y = start.getY();
        Case current = Labyrinthe[x][y];

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

            if (canMove(x, y, left)) {
                dir = left;
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            } else if (canMove(x, y, dir)) {
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            } else if (canMove(x, y, dir.turnRight())) {
                dir = dir.turnRight();
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            } else {
                dir = dir.opposite();
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            }


            if (Labyrinthe[x][y].getVisited()) {
                Labyrinthe[x][y].setVisited(true);
                path.add(Labyrinthe[x][y]);
            }
        }
        long endTime = System.nanoTime();
        setDuration(endTime - startTime);
        return path;
    }


    public boolean canMove(int x, int y, Direction dir){
        Case c = Labyrinthe[x][y];
        return switch (dir) {
            case NORTH -> !c.getNorth() && x > 0;
            case EAST  -> !c.getEast() && y < width - 1;
            case SOUTH -> !c.getSouth() && x < height - 1;
            case WEST  -> !c.getWest() && y > 0;
        };
    }


    public int[] move(int x, int y, Direction dir){
        return switch (dir) {
            case NORTH -> new int[] {x - 1, y};
            case EAST  -> new int[] {x, y + 1};
            case SOUTH -> new int[] {x + 1, y};
            case WEST  -> new int[] {x, y - 1};
        };
    }

    //BFS resolution
    public List<Case> BFS() {
        return BFS(false);
    }

    public List<Case> BFS(boolean stepByStep) {
        if (!isSolvable()) {
            return null; // Retourne null si le labyrinthe est insoluble
        }

        resetCounts();
        Queue<Case> queue = new LinkedList<>();
        parentMap = new HashMap<>();
        Set<Case> visited = new HashSet<>();
        List<Case> path = new ArrayList<>();

        long startTime = System.nanoTime();
        queue.add(start);
        visited.add(start);
        parentMap.put(start, null);
        path.add(start);

        while (!queue.isEmpty()) {
            Case current = queue.poll();

            // Si le serpent arrive à la fin
            if (current == end) {
                setDuration(System.nanoTime() - startTime);
                return stepByStep ? path : getFinalPath();
            }

            // On explore tous les voisins accessibles
            for (Case neighbor : getAccessibleNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                    addNbCase();

                    if (stepByStep) {
                        path.add(neighbor);
                    }
                }
            }
        }

        setDuration(System.nanoTime() - startTime);
        return stepByStep ? path : getFinalPath();
    }

    // On reconstruis le chemin à partir des parents
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

    public List<Case> getFinalPath() {
        if (parentMap == null || end == null) {
            return new ArrayList<>();
        }
        return reconstructPath(parentMap, end);
    }

    public boolean isSolvable() {
        if (start == null || end == null) return false;

        Queue<Case> queue = new LinkedList<>();
        Set<Case> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Case current = queue.poll();

            if (current == end) {
                return true; // Chemin trouvé
            }

            for (Case neighbor : getAccessibleNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return false; // Aucun chemin trouvé
    }


    public int getNbCase() { return nbCase; }
    public long getDuration() { return duration; }

    public void addNbCase(){ this.nbCase++; }

    public void setNbCase(int nbCase) { this.nbCase = nbCase; }

    public void setDuration(long duration) { this.duration = duration; }
}