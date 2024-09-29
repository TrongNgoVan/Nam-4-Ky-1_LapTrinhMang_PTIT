package lt1;
import java.io.*;
import java.net.*;

public class LT1_B2{
    
    public static void main(String[] args) {
        // Địa chỉ IP của server và cổng kết nối
        String serverAddress = "172.188.19.218";
        int serverPort = 1604;

        // Mã sinh viên và mã câu hỏi
        String studentCode = "B21DCCN197";
        String questionCode = "ZYo38AE";

        try {
            // Kết nối tới server
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Kết nối tới server thành công!");

            // Tạo luồng dữ liệu để gửi và nhận từ server
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            // Gửi chuỗi mã sinh viên và mã câu hỏi theo định dạng "studentCode;qCode"
            String message = studentCode + ";" + questionCode;
            os.write(message.getBytes());
            os.flush(); // Đảm bảo dữ liệu được gửi ngay

            // Nhận dữ liệu từ server là một chuỗi gồm các giá trị nguyên được phân tách bằng "|"
            byte[] buffer = new byte[1024];
            int bytesRead = is.read(buffer);
            String receivedData = new String(buffer, 0, bytesRead);
            System.out.println("Dữ liệu nhận được từ server: " + receivedData);

            // Tách các số nguyên từ chuỗi nhận được và tính tổng
            String[] numbers = receivedData.split("\\|");
            int sum = 0;
            for (String number : numbers) {
                sum += Integer.parseInt(number.trim());
            }
            System.out.println("Tổng các số nguyên: " + sum);

            // Gửi giá trị tổng lên server
            String sumString = String.valueOf(sum);
            os.write(sumString.getBytes());
            os.flush();

            // Đóng kết nối
            is.close();
            os.close();
            socket.close();
            System.out.println("Kết nối đã đóng.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
