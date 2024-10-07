/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UDP;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;



/**
 *
 * @author MEDIAMART PHU SON
 */
public class B1_DataType {
    
      public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            // 1. Tạo socket UDP
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 2207;

            // 2. Gửi thông điệp đầu tiên với định dạng ";studentCode;qCode"
            String studentCode = "B21DCCN319";  // Mã sinh viên của bạn
            String qCode = "Bca0Dehp";          // Mã câu hỏi
            String message = ";" + studentCode + ";" + qCode;
//            Tất cả đều quy về gửi byte Data hết
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);

            // 3. Nhận thông điệp từ server
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Received from server: " + response);

            // 4. Phân tích thông điệp nhận được
            String[] parts = response.split(";");
            String requestId = parts[0];  // requestId
            String[] numbersStr = parts[1].split(",");  // Chuỗi các số a1, a2, ..., a50

            // 5. Chuyển đổi chuỗi các số thành mảng số nguyên
            int[] numbers = Arrays.stream(numbersStr)
                                  .mapToInt(Integer::parseInt)
                                  .toArray();

            // 6. Tìm giá trị lớn nhất và nhỏ nhất
            int max = Arrays.stream(numbers).max().getAsInt();
            int min = Arrays.stream(numbers).min().getAsInt();

            System.out.println("Max: " + max + ", Min: " + min);

            // 7. Gửi thông điệp lại lên server với định dạng "requestId;max,min"
            String resultMessage = requestId + ";" + max + "," + min;
            byte[] resultData = resultMessage.getBytes();
            DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, serverAddress, serverPort);
            socket.send(resultPacket);

            // 8. Đóng socket và kết thúc chương trình
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}

