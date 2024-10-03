/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TH_UDP;

/**
 *
 * @author MEDIAMART PHU SON
 */
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Server {

    private static Connection conn = null;

    public static void main(String[] args) {
        DatagramSocket serverSocket = null;

        try {
            // Kết nối đến CSDL MySQL
            connectDB();  // Kết nối cơ sở dữ liệu

            // Tạo socket UDP
            serverSocket = new DatagramSocket(8889);
            byte[] receiveData = new byte[1024];

            while (true) {
                System.out.println("Server đang lắng nghe...");

                // Nhận gói tin từ Client
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String clientMessage = new String(receivePacket.getData()).trim();

                // Phân tích yêu cầu từ Client
                String[] requestParts = clientMessage.split(";");
                String command = requestParts[0];
                String response = "";

                switch (command) {
                    case "SEARCH_BY_NAME":
                        String name = requestParts[1];
                        response = searchByName(conn, name);
                        break;
                    case "SEARCH_BY_GPA_RANGE":
                        float minGPA = Float.parseFloat(requestParts[1]);
                        float maxGPA = Float.parseFloat(requestParts[2]);
                        response = searchByGpaRange(conn, minGPA, maxGPA);
                        break;
                    case "UPDATE_STUDENT":
                        // Cập nhật toàn bộ thông tin trừ `idSV`
                        int idSV = Integer.parseInt(requestParts[1]);
                        String newMaSV = requestParts[2];
                        String newHoTen = requestParts[3];
                        int newNamSinh = Integer.parseInt(requestParts[4]);
                        String newQueQuan = requestParts[5];
                        float newGPA = Float.parseFloat(requestParts[6]);
                        response = updateStudent(conn, idSV, newMaSV, newHoTen, newNamSinh, newQueQuan, newGPA);
                        break;
                    default:
                        response = "Lệnh không hợp lệ.";
                        break;
                }

                // Gửi phản hồi về Client
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Kết nối tới cơ sở dữ liệu
    private static void connectDB() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/studentdb?useUnicode=true&characterEncoding=utf8";
        String user = "root";
        String password = "";  // Mật khẩu MySQL (nếu có)
        conn = DriverManager.getConnection(url, user, password);
        System.out.println("Kết nối thành công tới CSDL!");
    }

    // Tìm kiếm sinh viên theo họ tên
    public static String searchByName(Connection conn, String name) {
        StringBuilder result = new StringBuilder();
        try {
            // SQL để tìm kiếm không phân biệt hoa thường
            String sql = "SELECT * FROM student WHERE LOWER(hoTen) LIKE LOWER(?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + name + "%"); // Tìm kiếm theo chuỗi nhập vào
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                result.append("ID: ").append(rs.getInt("idSV")).append(", Name: ").append(rs.getString("hoTen"))
                      .append(", gPA: ").append(rs.getFloat("gPA")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return result.toString().isEmpty() ? "Không tìm thấy sinh viên nào." : result.toString();
    }

    // Tìm kiếm sinh viên theo khoảng gPA
    public static String searchByGpaRange(Connection conn, float minGPA, float maxGPA) {
        StringBuilder result = new StringBuilder();
        try {
            String sql = "SELECT * FROM student WHERE gPA BETWEEN ? AND ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setFloat(1, minGPA);
            pstmt.setFloat(2, maxGPA);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                result.append("ID: ").append(rs.getInt("idSV")).append(", Name: ").append(rs.getString("hoTen"))
                      .append(", gPA: ").append(rs.getFloat("gPA")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return result.toString().isEmpty() ? "Không tìm thấy sinh viên nào." : result.toString();
    }

    // Cập nhật toàn bộ thông tin sinh viên (trừ `idSV`)
    public static String updateStudent(Connection conn, int idSV, String maSV, String hoTen, int namSinh, String queQuan, float gPA) {
        try {
            String sql = "UPDATE student SET maSV = ?, hoTen = ?, namSinh = ?, queQuan = ?, gPA = ? WHERE idSV = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maSV);
            pstmt.setString(2, hoTen);
            pstmt.setInt(3, namSinh);
            pstmt.setString(4, queQuan);
            pstmt.setFloat(5, gPA);
            pstmt.setInt(6, idSV);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Cập nhật thành công!";
            } else {
                return "Không tìm thấy sinh viên với ID: " + idSV;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
