import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port = 8889;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server đang chạy trên cổng " + port);

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Kết nối từ " + socket.getInetAddress() + ":" + socket.getPort());

                    // Tạo luồng đối tượng để nhận và gửi dữ liệu
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    // Nhận mã sinh viên và mã câu hỏi
                    String message = ois.readUTF();
                    System.out.println("Nhận từ client: " + message);

                    // Tạo đối tượng Student
                    Student student = new Student(1, "B21DCCN319 Nguyễn Như Thiệu", 3.5f);
                    
                    // Gửi đối tượng Student về client
                    oos.writeObject(student);
                    oos.flush();
                    System.out.println("Gửi đối tượng Student đến client: " + student.getCode() + ", GPA: " + student.getGpa());

                    // Nhận đối tượng Student đã xử lý từ client
                    Student processedStudent = (Student) ois.readObject();
                    System.out.println("Nhận đối tượng Student từ client: " + processedStudent.getCode() + ", GPA: " + processedStudent.getGpa() + ", GPA Letter: " + processedStudent.getGpaLetter());

                    // Kiểm tra GPA Letter
                    String feedbackMessage = checkGpaLetter(processedStudent);
                    
                    // Gửi thông báo về client
                    oos.writeUTF(feedbackMessage);
                    oos.flush();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức kiểm tra GPA Letter
    private static String checkGpaLetter(Student student) {
        String expectedGpaLetter;

        // Xác định GPA Letter dựa trên GPA
        if (student.getGpa() >= 3.7) {
            expectedGpaLetter = "A";
        } else if (student.getGpa() >= 3.0) {
            expectedGpaLetter = "B";
        } else if (student.getGpa() >= 2.0) {
            expectedGpaLetter = "C";
        } else if (student.getGpa() >= 1.0) {
            expectedGpaLetter = "D";
        } else {
            expectedGpaLetter = "F";
        }

        // So sánh giá trị GPA Letter từ client với giá trị dự kiến
        if (student.getGpaLetter().equals(expectedGpaLetter)) {
            return "Chúc mừng, mày đã tính đúng";
        } else {
            return "Rất tiếc mày đã tính sai" ;
        }
    }
}
