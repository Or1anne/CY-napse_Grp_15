package com.example.nouveau;

public class Case {
    private int id;
    private static int number;
    public boolean North;
    public boolean South;
    public boolean East;
    public boolean West;

    public Case(int x, int y){
        id = number++;
        North = false;
        South = false;
        East = false;
        West = false;
    }
}
