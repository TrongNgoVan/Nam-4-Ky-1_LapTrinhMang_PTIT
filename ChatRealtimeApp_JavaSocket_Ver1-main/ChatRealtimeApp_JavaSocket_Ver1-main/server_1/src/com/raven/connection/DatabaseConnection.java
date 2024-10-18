package com.raven.connection;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private DatabaseConnection() {
    }

    public void connectToDatabase() throws SQLException {
        String server = "localhost";
        String port = "3306";
        String database = "chat_application";
        String userName = "root";
        String password = "";
        connection = java.sql.DriverManager.getConnection("jdbc:mysql://" + server + ":" + port + "/" + database, userName, password);
        System.out.println("Kết nối đến cơ sở dữ liệu thành công!");
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    // Phương thức kiểm tra kết nối
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Phương thức main để kiểm tra kết nối
//    public static void main(String[] args) {
//        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
//
//        try {
//            dbConnection.connectToDatabase(); // Kết nối đến cơ sở dữ liệu
//
//            if (dbConnection.isConnected()) {
//                System.out.println("Kết nối đã được thiết lập.");
//            } else {
//                System.out.println("Không thể thiết lập kết nối.");
//            }
//        } catch (SQLException e) {
//            System.out.println("Lỗi khi kết nối: " + e.getMessage());
//        } finally {
//            dbConnection.setConnection(null); // Đặt lại kết nối
//            System.out.println("Kết nối đã được đóng.");
//        }
//    }
}
