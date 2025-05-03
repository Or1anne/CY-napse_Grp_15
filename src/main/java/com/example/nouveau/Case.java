package com.example.nouveau;

public class Case {
    private final int id;
    public static int number;
    public boolean North;
    public boolean South;
    public boolean East;
    public boolean West;
    public boolean visited;

    public Case(int x, int y){
        id = number++;
        North = true;
        South = true;
        East = true;
        West = true;
        visited = false;
    }

    public int getID(){
        return this.id;
    }
}
