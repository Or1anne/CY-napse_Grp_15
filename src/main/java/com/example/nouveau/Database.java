package com.example.nouveau;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.Properties;

public class Database {
    String dbName = "Sauvegarde";
    String userName = "root";
    String password = "";

    public Connection connectServer() throws SQLException {
        Properties IdMdp = new Properties();
        IdMdp.put("user", userName);
        IdMdp.put("password", password);
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/", IdMdp);
    }

    public Connection connectDatabase() throws SQLException {
        Properties IdMdp = new Properties();
        IdMdp.put("user", userName);
        IdMdp.put("password", password);
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + dbName, IdMdp);
    }

    public void createDatabase(){
        try(Connection conn = connectServer()) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmt.close();
            System.out.println("Base créée (ou existante déjà).");
        }catch(SQLException e){
            System.out.print("Erreur dans la création de la BDD");
        }
    }

    public void createTable(){
        try(Connection conn = connectDatabase()) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Maze (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(50) UNIQUE," +
                    "height INT," +
                    "width INT)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Cell (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "maze_id INT," +
                    "x INT," +
                    "y INT," +
                    "North BOOLEAN," +
                    "South BOOLEAN," +
                    "West BOOLEAN," +
                    "East BOOLEAN," +
                    "FOREIGN KEY (maze_id) REFERENCES Maze(id))");
            stmt.close();
        }catch(SQLException e){
            System.out.println("Erreur dans la création des tables");
        }
    }

    public void SaveMaze(Maze labyrinth, String Name){
        try(Connection conn = connectDatabase()){
            PreparedStatement MazeStmt = conn.prepareStatement("INSERT INTO Maze (name, height, width) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            MazeStmt.setString(1, Name);
            MazeStmt.setInt(2, labyrinth.getHeight());
            MazeStmt.setInt(3, labyrinth.getWidth());
            MazeStmt.executeUpdate();

            ResultSet rs = MazeStmt.getGeneratedKeys();
            int mazeId = -1;
            if(rs.next()){
                mazeId = rs.getInt(1);
            }
            else{
                throw new SQLException("Erreur lors de la récupération de l'ID");
            }
            MazeStmt.close();

            PreparedStatement CellStmt = conn.prepareStatement("INSERT INTO Cell (maze_id, x, y, North, South, West, East) VALUES (?, ?, ?, ?, ?, ?, ?)");
            for(int i = 0; i < labyrinth.getHeight(); i++){
                for(int j = 0; j < labyrinth.getWidth(); j++){
                    Case cell = labyrinth.getMaze()[i][j];
                    CellStmt.setInt(1, mazeId);
                    CellStmt.setInt(2, j);
                    CellStmt.setInt(3, i);
                    CellStmt.setBoolean(4, cell.getNorth());
                    CellStmt.setBoolean(5, cell.getSouth());
                    CellStmt.setBoolean(6, cell.getWest());
                    CellStmt.setBoolean(7, cell.getEast());
                    CellStmt.addBatch();
                }
            }
            CellStmt.executeBatch();
            CellStmt.close();
        }catch(SQLException e){
            System.out.println("Erreur lors de la sauvegarde du labyrinthe : " + e.getMessage());
            e.printStackTrace();
        }

    }

    public ObservableList<String> getMazeList(){
        ObservableList<String> SavedMaze = FXCollections.observableArrayList();
        try(Connection conn = connectDatabase()){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM Maze");
            while(rs.next()){
                SavedMaze.add(rs.getString("name"));
            }
            stmt.close();
        }catch(SQLException e){
            System.out.println("Erreur lors de la récupération de la BDD");
        }
        return SavedMaze;
    }

    /*public Maze ChargeMaze(){
        try(Connection conn = connectDatabase()){
            PreparedStatement stmtCollect = conn.prepareStatement("SELECT * FROM Maze WHERE id = ");
            ResultSet rs = stmtCollect.executeQuery();
            while(rs.next()){
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                int id =
            }
            Maze labyrinth = new Maze(width, height);
        }catch(SQLException e){
            System.out.println("Erreur lors de la récupération de la BDD" + e.getMessage());
            e.printStackTrace();
        }
    }*/

}

