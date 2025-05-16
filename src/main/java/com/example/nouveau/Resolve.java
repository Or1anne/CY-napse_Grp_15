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
        resetCounts();
        List<Case> path = new ArrayList<>();
        boolean found = explore(start.getX(), start.getY(), path);
        if (!found) return null;
        path.removeIf(c ->(c.getCount()==2));
        return path;
    }

    private boolean explore(int x, int y, List<Case> path) {
        Case current = Labyrinthe[x][y];
        current.incrementCount();
        path.add(current);
        nbCase++;
        System.out.println("Exploration de la case : " + current);

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
        System.out.println("Backtrack à la case : " + current);
        return false;
    }


    //Main Gauche sur le mur
    public List<Case> HandOnWall() {
        resetCounts();
        int x = start.getX();
        int y = start.getY();
        Case current = Labyrinthe[x][y];

        // Position de départ (0, 0)
        Direction dir = Direction.EAST; // Direction initiale (peut être ajustée)
        List<Case> path = new ArrayList<>();
        current.setVisited(true);  // Marquer la première case comme visitée
        path.add(current);

        System.out.println("Début de l'exploration"); // Debug
        System.out.println("[0, 0]"); // Debug

        /* System.out.println("Position actuelle: (" + x + ", " + y + ")");
        System.out.println("Direction: " + dir);
        System.out.println("Mur à droite : " + grid[x][y].getEast());
        System.out.println("Mur à gauche : " + grid[x][y].getWest()); */
        int steps = 0;
        int maxSteps = width * height * 4;

        // Condition pour sortir du labyrinthe
        while (!(x == height - 1 && y == width - 1)) {
            if (steps++ > maxSteps) {
                System.out.println("Labyrinthe insoluble (boucle infinie détectée)");
                return null;
            }

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
}









