package dk.kea.projectmanagement.repository.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBManager implements DBManager {
    public Connection getConnection(){
        String url = System.getenv("TEST_DB_URL");
        String username = System.getenv("TEST_DB_USERNAME");
        String password = System.getenv("TEST_DB_PASSWORD");

        try{
            return DriverManager.getConnection(url,username,password);
        } catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}

