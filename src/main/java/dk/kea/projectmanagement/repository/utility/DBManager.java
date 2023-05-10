package dk.kea.projectmanagement.repository.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static Connection connection = null;

    public static Connection getConnection(){
        String url = System.getenv("DATABASE_URL");
        String username = System.getenv("DATABASE_USERNAME");
        String password = System.getenv("DATABASE_PASSWORD");

        try{
            connection = DriverManager.getConnection(url,username,password);
        } catch(SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
}
