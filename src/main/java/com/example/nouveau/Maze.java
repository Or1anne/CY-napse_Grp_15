package com.example.nouveau;
import java.util.*;

public class Maze {
    private final int height;
    private final int width;
    private Case[][] maze;

/////////////////////////////////////////////////////////////////////

    public Maze(int width, int height){
        this.height = height;
        this.width = width;
        maze = new Case[height][width];
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                maze[i][j] = new Case(i,j);

            }
        }
        this.maze[0][0].setWest(false);
        this.maze[height-1][width-1].setEast(false);
    }

/////////////////////////////////////////////////////////////////////

    public void KruskalGeneration(){
        int[] father = new int[height*width];
        for(int i=0; i<father.length; i++){
            father[i] = i;
        }

        LinkedList<int[]> walls = new LinkedList<>();
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                if(i>0){
                    walls.add(new int[]{i,j,i-1,j});
                }
                if(j>0){
                    walls.add(new int[]{i,j,i,j-1});
                }
            }
        }
        Collections.shuffle(walls, new Random(4)); //Il faut remplacer la seed ici

        for(int[] wall: walls){
            Case c1 = this.maze[wall[0]][wall[1]];
            Case c2 = this.maze[wall[2]][wall[3]];

            if(union(c1.getID(), c2.getID(), father)){
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
                        c2.setSouth(false);
                    }
                    else{
                        c2.setNorth(false);
                        c1.setSouth(false);
                    }
                }
            }
        }
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

/////////////////////////////////////////////////////////////////////

    public Case[][] getMaze(){
        return maze;
    }
}
