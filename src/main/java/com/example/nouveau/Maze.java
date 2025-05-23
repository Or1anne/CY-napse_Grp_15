package com.example.nouveau;
import java.util.*;

public class Maze {
    private final int height;
    private final int width;
    private final Case[][] maze;

    /////////////////////////////////////////////////////////////////////
    //Constructeur Matrice de Case
    public Maze(int width, int height){
        this.height = height;
        this.width = width;
        maze = new Case[height][width];
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                maze[i][j] = new Case(i,j);
            }
        }
        Case.resetNumber();
    }


    public int getHeight(){
        return this.height;
    }
    public int getWidth(){
        return this.width;
    }

    /// //////////////////////////////////////////////////////////////////

    public LinkedList<int[]> KruskalGeneration(int seed){
        int[] father = new int[this.height * this.width];
        for(int i = 0; i< father.length; i++){
            father[i] = i;
        }

        LinkedList<int[]> steps = new LinkedList<>();
        LinkedList<int[]> walls = new LinkedList<>();

        for(int i=0; i<this.height; i++){
            for(int j=0; j<this.width; j++){
                if(i>0){
                    walls.add(new int[]{i,j,i-1,j});
                }
                if(j>0){
                    walls.add(new int[]{i,j,i,j-1});
                }
            }
        }
        Collections.shuffle(walls, new Random(seed));

        for(int[] wall: walls){
            Case c1 = this.maze[wall[0]][wall[1]];
            Case c2 = this.maze[wall[2]][wall[3]];

            if(union(c1.getID(), c2.getID(), father)){
                steps.add(wall);
            }
        }
        return steps;
    }

    public LinkedList<int[]> KruskalImperfectGeneration(int seed){
        LinkedList<int[]> steps = new LinkedList<>();
        Random randSeed = new Random(seed);
        for(int i=0; i<this.height; i++){
            for(int j=0; j<this.width; j++){
                if(i<this.height-1 && countWalls(this.maze[i][j])>1 && countWalls(this.maze[i+1][j])>1 && randSeed.nextDouble() < 0.7) {
                    steps.add(new int[]{i, j, i + 1, j});
                }
                if(j<this.width-1 && countWalls(this.maze[i][j])>1 && countWalls(this.maze[i][j+1])>1 && randSeed.nextDouble() < 0.7) {
                    steps.add(new int[]{i, j, i, j + 1});
                }
            }
        }
        return steps;
    }

    private int countWalls(Case c) {
        int count = 0;
        if (c.getNorth()) count++;
        if (c.getSouth()) count++;
        if (c.getEast()) count++;
        if (c.getWest()) count++;
        return count;
    }



    private int find(int s, int[] father){
        if(father[s] != s){
            father[s] = find(father[s], father);
        }
        return father[s];
    }

    private boolean union(int s1, int s2, int[] father){
        int root_s1 = find(s1, father);
        int root_s2 = find(s2, father);
        if(root_s1 != root_s2){
            father[root_s2] = root_s1;
            return true;
        }
        else{
            return false;
        }
    }

    public void setWallsPerfect(int[] wall, Case c1, Case c2){
        if(wall[0] == wall[2]){
            if(wall[1] > wall[3]){
                c1.setWest(false);
                c2.setEast(false);
            }
            else{
                c2.setWest(false);
                c1.setEast(false);
            }
        }
        else{
            if(wall[0] > wall[2]){
                c1.setNorth(false);
                c2.setSouth(false);;
            }
            else{
                c2.setNorth(false);
                c1.setSouth(false);
            }
        }
    }
    public void setWallsPerfectImperfect(int[] wall, Case c1, Case c2){
        if(wall[0] == wall[2]){
            c1.setEast(false);
            c2.setWest(false);
        }
        else{
            c1.setSouth(false);
            c2.setNorth(false);
        }
    }


/////////////////////////////////////////////////////////////////////

    public Case[][] getMaze(){
        return maze;
    }
    public Case getCase(int x, int y){
        if(x>=0 && x<height && y>=0 && y<width){
            return maze[x][y];
        }
        return null;
    }

    public boolean isPerfect() {
        int height = this.getHeight();
        int width = this.getWidth();
        Case[][] maze = this.getMaze();

        boolean[][] visited = new boolean[height][width];
        int totalCells = height * width;
        int visitedCount = 0;

        Stack<Case> stack = new Stack<>();
        stack.push(maze[0][0]); // Démarre depuis la première case
        visited[0][0] = true;
        visitedCount++;

        // Pour stocker le parent de chaque cellule (éviter cycles)
        Map<Case, Case> parentMap = new HashMap<>();

        while (!stack.isEmpty()) {
            Case current = stack.pop();
            int x = current.getX();
            int y = current.getY();

            for (int[] dir : new int[][]{{-1,0},{1,0},{0,-1},{0,1}}) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx < 0 || ny < 0 || nx >= height || ny >= width) continue;

                Case neighbor = maze[nx][ny];

                // Vérifie si un mur bloque le passage
                if ((nx == x - 1 && current.getNorth()) ||
                    (nx == x + 1 && current.getSouth()) ||
                    (ny == y - 1 && current.getWest()) ||
                    (ny == y + 1 && current.getEast())) {
                    continue;
                }

                // Déjà visité ?
                if (!visited[nx][ny]) {
                    visited[nx][ny] = true;
                    visitedCount++;
                    parentMap.put(neighbor, current);
                    stack.push(neighbor);
                } else {
                    // Si déjà visité et pas le parent => cycle détecté
                    if (parentMap.get(current) != neighbor) {
                        return false; // Il y a un cycle, donc pas parfait
                    }
                }
            }
        }

        // Vérifie que toutes les cases ont été visitées (connexité)
        return visitedCount == totalCells;
    }

}
