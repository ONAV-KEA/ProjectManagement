package dk.kea.projectmanagement.repository.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBManager implements DBManager {
    public Connection getConnection(){
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        String username = "sa";
        String password = "";

        try{
            return DriverManager.getConnection(url,username,password);
        } catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
