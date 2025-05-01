package com.example.nouveau;
import java.util.*;

public class Maze {
    private int height;
    private int width;
    private Case[][] maze;

    public Maze(int width, int height){
        this.height = height;
        this.width = width;
        maze = new Case[height][width];
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                maze[i][j] = new Case(i,j);
            }
        }
    }

    public void generation(){
        Random random = new Random();

        for(int i=0; i<width; i++){
            this.maze[0][i].North = true;
            this.maze[height-1][i].South = true;
        }
        for(int i=0; i<height; i++){
            this.maze[i][width-1].East = true;
            this.maze[i][0].West = true;
        }

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                if(i!=0 && this.maze[i-1][j].South){
                    this.maze[i][j].North = true;
                }
                this.maze[i][j].South = random.nextBoolean();
                if(j!=0 && this.maze[i][j-1].East){
                    this.maze[i][j].West = true;
                }
                this.maze[i][j].East = random.nextBoolean();

            }
        }
    }

    public Case[][] getMaze(){
        return maze;
    }
}
