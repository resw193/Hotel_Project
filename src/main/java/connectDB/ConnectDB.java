package connectDB;

import java.sql.*;


public class ConnectDB {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=mimosa_hotel;encrypt=false;";
    private static final String USER = "sa";
    private static final String PASSWORD = "123";

    private static ConnectDB instance;
    private Connection connection;

    private ConnectDB() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Property
    public static ConnectDB getInstance() {
        if (instance == null) {
            instance = new ConnectDB();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // Kết nối csdl
    public Connection connect() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Ngắt kết nối csdl
    public void close(PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
