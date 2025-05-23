package com.example.nouveau;

/**
 * La classe {@code Case} représente une cellule dans un labyrinthe.
 * Chaque case possède des murs (nord, est, sud, ouest) qui peuvent être présents ou absents,
 * ainsi que des informations sur sa position, son identifiant unique et son état de visite.
 */
public class Case {
    private int count = 0;
    private int id;
    private static int number;
    private boolean North;
    private boolean South;
    private boolean East;
    private boolean West;
    private boolean visited;
    private int x;
    private int y;


    /**
     * Constructeur d'une case à la position donnée. Tous les murs sont initialement présents.
     * @param x Coordonnée verticale de la case.
     * @param y Coordonnée horizontale de la case.
     */
    public Case(int x, int y){
        id = number++;
        North = true;
        South = true;
        East = true;
        West = true;
        this.x = x;
        this.y = y;
        visited = false;
    }

    //Getters
    /** @return Coordonnée verticale (ligne) de la case. */
    public int getX() { return x; }

    /** @return Coordonnée horizontale (colonne) de la case. */
    public int getY() { return y; }

    /** @return Identifiant unique de la case. */
    public int getID() { return id; }

    /** @return Vrai si le mur nord est présent. */
    public boolean getNorth() { return North; }

    /** @return Vrai si le mur sud est présent. */
    public boolean getSouth() { return South; }

    /** @return Vrai si le mur ouest est présent. */
    public boolean getWest() { return West; }

    /** @return Vrai si le mur est est présent. */
    public boolean getEast() { return East; }

    /** @return Faux si la case a déjà été visitée. */
    public boolean getVisited() { return !visited; }

    /** @return Nombre de fois que cette case a été visitée dans certains algorithmes. */
    public int getCount() { return count; }

    /** @return Nombre total de cases créées (statique). */
    public int getNumber(){ return number; }


    //Setters
    public void setNorth(boolean North) { this.North = North; }
    public void setSouth(boolean South) { this.South = South; }
    public void setWest(boolean West)   { this.West = West; }
    public void setEast(boolean East)   { this.East = East; }

    /**
     * Définit l'état de visite de la case.
     * @param visited Vrai si la case doit être considérée comme non visitée.
     */
    public void setVisited(boolean visited) { this.visited = visited; }

    public void setX(int x){ this.x=x;}
    public void setY(int y){ this.y=y;}
    public void setId(int id){this.id = id;}

    /**
     * Réinitialise le compteur de visite de la case.
     * @param count Nouvelle valeur du compteur.
     */
    public void resetCount(int count){ this.count=count;}

    /** Incrémente le compteur de visite de la case. */
    public void incrementCount() { this.count++; }

    /** Réinitialise le compteur global de création de cases. */
    public static void resetNumber() {
        number = 0;
    }

    /**
     * Vérifie si un mur est présent dans la direction donnée.
     * @param direction La direction à vérifier.
     * @return Vrai si un mur est présent, faux sinon.
     */
    public boolean hasWall(Direction direction) {
        switch (direction) {
            case NORTH: return North;
            case SOUTH: return South;
            case EAST:  return East;
            case WEST:  return West;
        }
        return false;
    }

    /**
     * Supprime le mur dans la direction donnée.
     * @param direction Direction dans laquelle enlever le mur.
     */
    public void removeWall(Direction direction) {
        switch (direction) {
            case NORTH: North = false; break;
            case SOUTH: South = false; break;
            case EAST:  East  = false; break;
            case WEST:  West  = false; break;
        }
    }

    /**
     * Ajoute un mur dans la direction donnée.
     * @param direction Direction dans laquelle ajouter le mur.
     */
    public void addWall(Direction direction) {
        switch (direction) {
            case NORTH: North = true; break;
            case SOUTH: South = true; break;
            case EAST:  East  = true; break;
            case WEST:  West  = true; break;
        }
    }

    @Override
    public String toString() {
        return "Case{x=" + x + ", y=" + y + ", count=" + count + "}";
    }
}
