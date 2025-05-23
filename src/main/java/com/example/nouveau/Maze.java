package com.example.nouveau;
import java.util.*;


/**
 * Représente un labyrinthe constitué d'une matrice de cases.
 * Chaque case possède des murs autour d'elle pouvant être ouverts ou fermés.
 * Cette classe permet de générer un labyrinthe parfait ou imparfait
 * selon différentes méthodes (Kruskal parfait ou Kruskal imparfait).
 */
public class Maze {
    private final int height;
    private final int width;
    private final Case[][] maze;

    /////////////////////////////////////////////////////////////////////
    //Constructeur Matrice de Case
    /**
     * Constructeur qui initialise un labyrinthe vide avec des murs pleins.
     * Chaque case est initialisée avec toutes ses frontières présentes.
     *
     * @param width  largeur du labyrinthe (nombre de colonnes)
     * @param height hauteur du labyrinthe (nombre de lignes)
     */
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


    /**
     * Retourne la hauteur du labyrinthe.
     *
     * @return la hauteur (nombre de lignes)
     */
    public int getHeight(){
        return this.height;
    }

    /**
     * Retourne la largeur du labyrinthe.
     *
     * @return la largeur (nombre de colonnes)
     */
    public int getWidth(){
        return this.width;
    }

    /// //////////////////////////////////////////////////////////////////

    /**
     * Génère un labyrinthe parfait en utilisant l'algorithme de Kruskal.
     *
     * @param seed la graine utilisée pour le tirage aléatoire (répétabilité)
     * @return une liste des murs retirés sous forme de tableaux d'entiers
     *         représentant les coordonnées des deux cases séparées par ce mur
     */
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


    /**
     * Génère un labyrinthe imparfait en retirant certains murs aléatoirement
     * selon la probabilité définie et l'état des murs des cases adjacentes.
     *
     * @param seed la graine utilisée pour l'aléatoire
     * @return une liste des murs retirés sous forme de tableaux d'entiers
     */
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


    /**
     * Compte le nombre de murs présents autour d'une case.
     *
     * @param c la case à analyser
     * @return le nombre de murs (entre 0 et 4)
     */
    private int countWalls(Case c) {
        int count = 0;
        if (c.getNorth()) count++;
        if (c.getSouth()) count++;
        if (c.getEast()) count++;
        if (c.getWest()) count++;
        return count;
    }



    /**
     * Trouve la racine d'un ensemble dans la structure de Union-Find.
     * Utilisé pour vérifier si deux cases appartiennent au même ensemble.
     *
     * @param s l'identifiant d'une case
     * @param father tableau représentant les parents dans l'Union-Find
     * @return la racine représentant l'ensemble
     */
    private int find(int s, int[] father){
        if(father[s] != s){
            father[s] = find(father[s], father);
        }
        return father[s];
    }



    /**
     * Effectue la fusion de deux ensembles disjoints si possible.
     *
     * @param s1 identifiant de la première case
     * @param s2 identifiant de la deuxième case
     * @param father tableau représentant les parents dans l'Union-Find
     * @return true si la fusion a eu lieu, false sinon (mêmes ensembles)
     */
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


    /**
     * Modifie les murs entre deux cases adjacentes en supprimant le mur qui les sépare,
     * pour une génération parfaite (pas de cycles).
     *
     * @param wall tableau représentant les coordonnées des deux cases
     * @param c1 première case
     * @param c2 deuxième case adjacente à c1
     */
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



    /**
     * Modifie les murs entre deux cases adjacentes en supprimant le mur qui les sépare,
     * pour une génération imparfaite (avec cycles possibles).
     *
     * @param wall tableau représentant les coordonnées des deux cases
     * @param c1 première case
     * @param c2 deuxième case adjacente à c1
     */
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

    /**
     * Retourne la matrice complète des cases du labyrinthe.
     *
     * @return un tableau 2D des cases
     */
    public Case[][] getMaze(){
        return maze;
    }

    /**
     * Retourne la case à la position donnée.
     *
     * @param x coordonnée en hauteur (ligne)
     * @param y coordonnée en largeur (colonne)
     * @return la case si les coordonnées sont valides, sinon null
     */
    public Case getCase(int x, int y){
        if(x>=0 && x<height && y>=0 && y<width){
            return maze[x][y];
        }
        return null;
    }
}
