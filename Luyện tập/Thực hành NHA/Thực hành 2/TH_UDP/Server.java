 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UDP.Total;

/**
 *
 * @author MEDIAMART PHU SON
 */
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private DatagramSocket socket;
    private Connection conn;
    private static final int BUFFER_SIZE = 65507; // Kích thước tối đa của gói tin UDP

    public Server(int port) {
        try {
            // Khởi động server
            socket = new DatagramSocket(port);
            System.out.println("Server đã khởi động tại cổng " + port);

            // Kết nối cơ sở dữ liệu
            connectDB();
            System.out.println("Đã kết nối với cơ sở dữ liệu MySQL.");

            // Lắng nghe yêu cầu từ client
            listenForRequests();

        } catch (IOException e) {
            System.err.println("Lỗi khi khởi động server: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi khi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }

    // Kết nối tới cơ sở dữ liệu
    private void connectDB() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/studentdb";
        String user = "root";
        String password = "";  // Mật khẩu MySQL (nếu có)
        conn = DriverManager.getConnection(url, user, password);
    }

    // Lắng nghe và xử lý yêu cầu
    private void listenForRequests() {
        while (true) {
            try {
                byte[] receiveData = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                
                System.out.println("Đã nhận yêu cầu từ client.");
                handleRequest(receivePacket);
            } catch (IOException e) {
                System.err.println("Lỗi khi nhận yêu cầu: " + e.getMessage());
            }
        }
    }

    // Xử lý yêu cầu từ client
    private void handleRequest(DatagramPacket receivePacket) {
        new Thread(() -> {
            try {
                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String[] parts = request.split(",");
                String requestType = parts[0];

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintWriter out = new PrintWriter(outputStream, true);

                switch (requestType) {
                    case "searchByName":
                        String hoTen = parts[1];
                        List<Student> studentsByName = searchByName(hoTen);
                        for (Student student : studentsByName) {
                            out.println(student);
                        }
                        out.println("END");
                        break;

                    case "searchByGPA":
                        float minGPA = Float.parseFloat(parts[1]);
                        float maxGPA = Float.parseFloat(parts[2]);
                        List<Student> studentsByGPA = searchByGPA(minGPA, maxGPA);
                        for (Student student : studentsByGPA) {
                            out.println(student);
                        }
                        out.println("END");
                        break;

                    case "update":
                        int idSV = Integer.parseInt(parts[1]);
                        String maSV = parts[2];
                        String ten = parts[3];
                        int namSinh = Integer.parseInt(parts[4]);
                        String queQuan = parts[5];
                        float gPA = Float.parseFloat(parts[6]);
                        String result = updateStudent(idSV, maSV, ten, namSinh, queQuan, gPA);
                        out.println(result);
                        break;

                    default:
                        out.println("Yêu cầu không hợp lệ.");
                        break;
                }

                byte[] sendData = outputStream.toByteArray();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
                    receivePacket.getAddress(), receivePacket.getPort());
                socket.send(sendPacket);

            } catch (IOException | SQLException e) {
                System.err.println("Lỗi xử lý yêu cầu: " + e.getMessage());
            }
        }).start();
    }

    private String updateStudent(int idSV, String maSV, String hoTen, int namSinh, String queQuan, float gPA) throws SQLException {
        String query = "UPDATE Student SET maSV = ?, hoTen = ?, namSinh = ?, queQuan = ?, gPA = ? WHERE idSV = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, maSV);
            stmt.setString(2, hoTen);
            stmt.setInt(3, namSinh);
            stmt.setString(4, queQuan);
            stmt.setFloat(5, gPA);
            stmt.setInt(6, idSV);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return "Cập nhật thành công!";
            } else {
                return "Không tìm thấy sinh viên!";
            }
        }
    }

    private List<Student> searchByName(String hoTen) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM Student WHERE hoTen LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + hoTen + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(createStudentFromResultSet(rs));
                }
            }
        }
        return students;
    }

    private List<Student> searchByGPA(float minGPA, float maxGPA) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM Student WHERE gPA BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setFloat(1, minGPA);
            stmt.setFloat(2, maxGPA);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(createStudentFromResultSet(rs));
                }
            }
        }
        return students;
    }

    private Student createStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setIdSV(rs.getInt("idSV"));
        student.setMaSV(rs.getString("maSV"));
        student.setHoTen(rs.getString("hoTen"));
        student.setNamSinh(rs.getInt("namSinh"));
        student.setQueQuan(rs.getString("queQuan"));
        student.setGPA(rs.getFloat("gPA"));
        return student;
    }

    // Lớp Student nội bộ
    private static class Student {
        private int idSV;
        private String maSV;
        private String hoTen;
        private int namSinh;
        private String queQuan;
        private float gPA;

        // Getters and setters
        public void setIdSV(int idSV) { this.idSV = idSV; }
        public void setMaSV(String maSV) { this.maSV = maSV; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public void setNamSinh(int namSinh) { this.namSinh = namSinh; }
        public void setQueQuan(String queQuan) { this.queQuan = queQuan; }
        public void setGPA(float gPA) { this.gPA = gPA; }

      @Override
        public String toString() {
            return idSV + "," + maSV + "," + hoTen + "," + namSinh + "," + queQuan + "," + gPA;
        }


    }

    public static void main(String[] args) {
        new Server(8889);
    }
}