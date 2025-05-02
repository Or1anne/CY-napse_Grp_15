package com.example.nouveau;
import java.util.*;

public class Maze {
    private int height;
    private int width;
    private Case[][] maze;
    private int[] father;

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
        Case.number = 0;
        this.maze[0][0].West = false;
        this.maze[height-1][width-1].East = false;
    }

    public int getHeight(){
        return this.height;
    }
    public int getWidth(){
        return this.width;
    }

/////////////////////////////////////////////////////////////////////

    public void KruskalGeneration(int seed){
        father = new int[this.height*this.width];
        for(int i=0; i<father.length; i++){
            father[i] = i;
        }

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
        Collections.shuffle(walls, new Random(seed)); //Il faut remplacer la seed ici

        for(int[] wall: walls){
            Case c1 = this.maze[wall[0]][wall[1]];
            Case c2 = this.maze[wall[2]][wall[3]];

            if(union(c1.getID(), c2.getID(), father)){
                if(wall[0] == wall[2]){
                    if(wall[1] > wall[3]){
                        c1.West = false;
                        c2.East = false;
                    }
                    else{
                        c2.West = false;
                        c1.East = false;
                    }
                }
                else{
                    if(wall[0] > wall[2]){
                        c1.North = false;
                        c2.South = false;
                    }
                    else{
                        c2.North = false;
                        c1.South = false;
                    }
                }
            }
        }
    }

    public void KruskalImperfectGeneration(int seed){
        Random randSeed = new Random(seed);
        for(int i=0; i<this.height; i++){
            for(int j=0; j<this.width; j++){
                if(j == 0 && randSeed.nextDouble() < 0.2){
                    this.maze[i][j].West = false;
                } else if (j == this.width-1 && randSeed.nextDouble() < 0.2) {
                    this.maze[i][j].East = false;
                }
                if(i<this.height-1 && countWalls(this.maze[i][j])>1 && countWalls(this.maze[i+1][j])>1 && randSeed.nextDouble() < 0.7) {
                    this.maze[i][j].South = false;
                    this.maze[i+1][j].North = false;
                }
                if(j<this.width-1 && countWalls(this.maze[i][j])>1 && countWalls(this.maze[i][j+1])>1 && randSeed.nextDouble() < 0.7) {
                    this.maze[i][j].East = false;
                    this.maze[i][j+1].West = false;
                }
            }
        }
    }

    private int countWalls(Case c) {
        int count = 0;
        if (c.North) count++;
        if (c.South) count++;
        if (c.East) count++;
        if (c.West) count++;
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

/////////////////////////////////////////////////////////////////////

    public Case[][] getMaze(){
        return maze;
    }
}
