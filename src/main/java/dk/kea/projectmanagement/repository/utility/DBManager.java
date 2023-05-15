package dk.kea.projectmanagement.repository.utility;

import java.sql.Connection;

public interface DBManager {
    Connection getConnection();
}
