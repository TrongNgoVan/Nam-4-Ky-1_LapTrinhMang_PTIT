import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        
        String serverAddress = "26.80.253.0"; 
        int serverPort = 8889;

        
        String studentCode = "B21DCCN726 Ngọ Văn Trọng";
        float studentGpa = 3.5f; // GPA của sinh viên
        String questionCode = "Bài 1: Chuyển đổi điểm sinh viên";

        try {
          
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Kết nối tới server thành công!");

           
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

          
            String message = studentCode + ";" + questionCode;
            oos.writeUTF(message);
            oos.flush();

          
            Student student = (Student) ois.readObject();
            System.out.println("Nhận đối tượng Student từ server: " + student.getCode() + ", GPA: " + student.getGpa());

       
            student.setGpaLetter(student.getGpa() >= 3.7 ? "A" : 
                                  (student.getGpa() >= 3.0 ? "B" : 
                                  (student.getGpa() >= 2.0 ? "C" : 
                                  (student.getGpa() >= 1.0 ? "D" : "F"))));

          
            oos.writeObject(student);
            oos.flush();

            String feedbackMessage = ois.readUTF();
            System.out.println("Thông báo từ server: " + feedbackMessage);

          
            ois.close();
            oos.close();
            socket.close();
            System.out.println("Kết nối đã đóng.");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}


