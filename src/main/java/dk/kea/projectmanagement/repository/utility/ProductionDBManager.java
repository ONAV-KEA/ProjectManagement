package dk.kea.projectmanagement.repository.utility;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProductionDBManager implements DBManager {
    public Connection getConnection(){
        String url = System.getenv("DATABASE_URL");
        String username = System.getenv("DATABASE_USERNAME");
        String password = System.getenv("DATABASE_PASSWORD");

        try{
            return DriverManager.getConnection(url,username,password);
        } catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
