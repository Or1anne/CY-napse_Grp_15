package com.example.nouveau;
import java.util.*;

public class Resolve {
    private Case[][] Labyrinthe;
    private int width, height;
    private int nbCase =0;
    private Case start;
    private Case end;

    public Resolve(Maze Labyrinthe) {
        this.Labyrinthe = Labyrinthe.getMaze();
        this.height = this.Labyrinthe.length;
        this.width = this.Labyrinthe[0].length;
        this.start = Labyrinthe.getMaze()[0][0];
        this.end = Labyrinthe.getMaze()[height-1][width-1];
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
        long startTIme = System.nanoTime();
        resetCounts();
        List<Case> path = new ArrayList<>();
        explore(start.getX(), start.getY(), path);
        path.removeIf(c ->(c.getCount()==2));
        long endTime = System.nanoTime();
        long duration = endTime-startTIme;
        System.out.println("Temps d'exécution de Tremaux: " + duration / 1_000_000 + " millisecondes");
        return path;
    }

    private boolean explore(int x, int y, List<Case> path) {
        Case current = Labyrinthe[x][y];
        current.incrementCount();
        path.add(current);
        nbCase++;


        if (x == height - 1 && y == width - 1){
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


    //Main Gauche sur le mur
    public List<Case> HandOnWall() {
        resetCounts();
        long startTime = System.nanoTime();
        int x = start.getX();
        int y = start.getY();
        Case current = Labyrinthe[x][y];

        // Position de départ (0, 0)
        Direction dir = Direction.EAST; // Direction initiale (peut être ajustée)
        List<Case> path = new ArrayList<>();
        current.setVisited(true);  // Marquer la première case comme visitée
        path.add(current);

        //System.out.println("Début de l'exploration"); // Debug
        //System.out.println("[0, 0]"); // Debug

        /* System.out.println("Position actuelle: (" + x + ", " + y + ")");
        System.out.println("Direction: " + dir);
        System.out.println("Mur à droite : " + grid[x][y].getEast());
        System.out.println("Mur à gauche : " + grid[x][y].getWest()); */


        // Condition pour sortir du labyrinthe
        while (!(x == height - 1 && y == width - 1)) {
            nbCase++;
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
        System.out.println("Nb Case visitée: "+nbCase);
        long duration = endTime - startTime;
        System.out.println("Temps d'exécution de Tremaux: " + duration / 1_000_000 + " millisecondes");
        System.out.println("Fin de l'exploration"); // Debug
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
            case EAST  -> new int[] {x, y + 1};  // ← corrigé
            case SOUTH -> new int[] {x + 1, y};
            case WEST  -> new int[] {x, y - 1};
        };
    }

    //BFS resolution
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
}









