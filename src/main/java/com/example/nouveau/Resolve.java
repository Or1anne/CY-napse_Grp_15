package com.example.nouveau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Resolve {
    private Case start;
    private Case end;
    private Maze maze;

    public List<Case> HandOnWall(Maze maze) {
        this.maze = maze;
        Case[][] grid = maze.getMaze();
        int height = maze.getHeight();
        int width = maze.getWidth();

        // Position de départ (0, 0)
        int x = 0, y = 0;
        Direction dir = Direction.EAST; // Direction initiale (peut être ajustée)
        List<Case> path = new ArrayList<>();
        grid[x][y].setVisited(true);  // Marquer la première case comme visitée
        path.add(grid[x][y]);

        System.out.println("Début de l'exploration"); // Debug
        System.out.println("[0, 0]"); // Debug

        /* System.out.println("Position actuelle: (" + x + ", " + y + ")");
        System.out.println("Direction: " + dir);
        System.out.println("Mur à droite : " + grid[x][y].getEast());
        System.out.println("Mur à gauche : " + grid[x][y].getWest()); */


        // Condition pour sortir du labyrinthe
        while (!(x == height - 1 && y == width - 1)) {
            Direction left = dir.turnLeft();

            if (canMove(x, y, left, grid)) {
                dir = left;
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            } else if (canMove(x, y, dir, grid)) {
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            } else if (canMove(x, y, dir.turnRight(), grid)) {
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


            if (!grid[x][y].getVisited()) {
                grid[x][y].setVisited(true);
                path.add(grid[x][y]);
            }
        }

        System.out.println("Fin de l'exploration"); // Debug
        return path;
    }


    public boolean canMove(int x, int y, Direction dir, Case[][] grid){
        Case c = grid[x][y];
        int height = maze.getHeight();
        int width = maze.getWidth();
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
